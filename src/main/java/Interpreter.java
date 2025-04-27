import java.util.*;

class Interpreter implements Expr.Visitor<Object>,
                             Stmt.Visitor<Void> {

    final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();

    Interpreter() {
        globals.define("clock", new LoxCallable() {
            // ... (native clock function remains the same) ...
            @Override
            public int arity() { return 0; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double)System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
    }

    // ... interpretExpression, interpretStatements, visitLiteralExpr etc. ...

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        // Ensure this calls the single, correct lookup method
        return lookupVariable(expr.name, expr);
    }

    // REMOVE THE INCORRECT DYNAMIC SCOPE VERSION:
    /*
    private Object lookupVariable(Token name, Expr expr) { // <--- DELETE THIS METHOD
        Object value = environment.get(name);
        if (value != null) return value;
        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
    */

    // RENAME and KEEP THE CORRECT LEXICAL SCOPE VERSION:
    private Object lookupVariable(Token name, Expr expr) { // Renamed from lookUpVariable
        // Check the map populated by the Resolver
        Integer distance = locals.get(expr);
        if (distance != null) {
            // It's a local variable, get it from the correct environment in the chain
            return environment.getAt(distance, name.lexeme);
        } else {
            // It's a global variable
            return globals.get(name);
        }
    }

    // Also update visitThisExpr to use the correct method
    @Override
    public Object visitThisExpr(Expr.This expr) {
        // 'this' is resolved like a local variable
        return lookupVariable(expr.keyword, expr);
    }


    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    // Method called by the Resolver to store scope depth info
    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    void executeBlock(List<Stmt> statements, Environment blockEnvironment) { // Renamed param for clarity
        Environment previous = this.environment;
        try {
            this.environment = blockEnvironment; // Use the passed-in environment
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous; // Restore previous environment
        }
    }

    // ... other visit methods, stringify, isTruthy, isEqual, checkNumberOperand(s) ...

    // --- Ensure all other visit methods are present and correct ---
    // (Copying from provided context, assuming they are mostly correct,
    //  but focusing on the lookupVariable consolidation)

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);
        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left))
                return left;
        } else { // AND
            if (!isTruthy(left))
                return left;
        }
        return evaluate(expr.right);
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                // Ensure cast to double if necessary, though Java handles unboxing
                return -(double)right;
        }
        // Unreachable
        return null;
    }

     @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }
                // Add concatenation for string + number if desired by Lox spec
                // if (left instanceof String || right instanceof String) {
                //    return stringify(left) + stringify(right);
                // }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                 if ((double)right == 0.0) {
                     throw new RuntimeError(expr.operator, "Division by zero.");
                 }
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
        }
        // Unreachable.
        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);
        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }
        if (!(callee instanceof LoxCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }
        LoxCallable function = (LoxCallable)callee;
        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected " +
                function.arity() + " arguments but got " +
                arguments.size() + ".");
        }
        // Pass 'this' interpreter instance to the call method
        return function.call(this, arguments);
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        Integer distance = locals.get(expr); // Check resolver map for the assignment target
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }
        return value;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookupVariable(expr.name, expr);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookupVariable(expr.keyword, expr);
    }

    private Object lookupVariable(Token name, Expr expr) {
        Object value = environment.get(name);
        if (value != null) return value;
        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }
    /*
    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body);
        }
        return null;
    }
    */
    private String stringify(Object object) {
        if (object == null)
            return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }

    private boolean isTruthy(Object object) {
        if (object == null)
            return false;

        if (object instanceof Boolean) 
            return (boolean)object;

        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) 
            return true;

        if (a == null) 
            return false;

        return a.equals(b);
    }

    void executeBlock(List<Stmt> statements,
                    Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) 
            return;

        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }
}