//> Representing Code generate-ast
package com.craftinginterpreters.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];

        //> call-define-ast
        defineAst(outputDir, "Expr", Arrays.asList(
            // Statements and State assign-expr
            "Assign   : Token name, Expr value",
            // Statements and State binary-ast
            "Binary   : Expr left, Token operator, Expr right",
            // Functions call-expr
            "Call     : Expr callee, Token paren, List<Expr> arguments",
            // Classes get-ast
            "Get      : Expr object, Token name",
            // Classes grouping-ast
            "Grouping : Expr expression",
            // Functions literal-ast
            "Literal  : Object value",
            // Control Flow logical-ast
            "Logical  : Expr left, Token operator, Expr right",
            // Classes set-ast
            "Set      : Expr object, Token name, Expr value",
            // Inheritance super-expr
            "Super    : Token keyword, Token method",
            // Classes this-ast
            "This     : Token keyword",
            // Control Flow unary-ast
            "Unary    : Token operator, Expr right",
            // Functions variable-ast
            "Variable : Token name"
        ));

        defineAst(outputDir, "Stmt", Arrays.asList(
            // block-ast
            "Block      : List<Stmt> statements",
            // Classes class-ast < Inheritance superclass-ast
            "Class      : Token name, Expr.Variable superclass," +
                        " List<Stmt.Function> methods",
            // Expressions expression-ast
            "Expression : Expr expression",
            // Functions function-ast
            "Function   : Token name, List<Token> params," +
                        " List<Stmt> body",
            // Control Flow if-ast
            "If         : Expr condition, Stmt thenBranch," +
                        " Stmt elseBranch",
            // Print print-ast
            "Print      : Expr expression",
            // Functions return-ast
            "Return     : Token keyword, Expr value",
            // Variables var-ast
            "Var        : Token name, Expr initializer",
            // Control Flow while-ast
            "While      : Expr condition, Stmt body"
        ));
    }

    //> define-ast
    private static void defineAst(
        String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package com.craftinginterpreters.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        //> call-define-visitor
        defineVisitor(writer, baseName, types);

        writer.println();
        writer.println("  // Nested " + baseName + " classes here...");
    
        // The AST classes.
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim(); // [robust]
            defineType(writer, baseName, className, fields);
        }
        
        // The base accept() method.
        writer.println();
        writer.println("  abstract <R> R accept(Visitor<R> visitor);");
        writer.println("}");
        writer.close();
    }
    
    // define-visitor
    private static void defineVisitor(
        PrintWriter writer, String baseName, List<String> types) {
        writer.println("  interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println(
                "    R visit" + typeName + baseName
                + "(" + typeName + " " + baseName.toLowerCase() + ");"
            );
        }
        writer.println("  }");
    }
    
    // define-type
    private static void defineType(
        PrintWriter writer, String baseName,
        String className, String fieldList) {
        // omit
        writer.println("// " + baseName.toLowerCase() + "-" + className.toLowerCase());

        writer.println("  static class " + className + " extends " + baseName + " {");

        // Constructor.
        writer.println("    " + className + "(" + fieldList + ") {");

        // Store parameters in fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("      this." + name + " = " + name + ";");
        }

        writer.println("    }");

        // Visitor pattern.
        writer.println();
        writer.println("    @Override");
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.println("      return visitor.visit" + className + baseName + "(this);");
        writer.println("    }");

        // Fields.
        writer.println();
        for (String field : fields) {
            writer.println("    final " + field + ";");
        }

        writer.println("  }");
    }
    
    // pastry-visitor
    interface PastryVisitor {
        void visitBeignet(Beignet beignet); // [overload]
        void visitCruller(Cruller cruller);
    }
    //< pastry-visitor
    //> pastries
    abstract class Pastry {
    //> pastry-accept
        abstract void accept(PastryVisitor visitor);
    //< pastry-accept
    }

    class Beignet extends Pastry {
    //> beignet-accept
        @Override
        void accept(PastryVisitor visitor) {
        visitor.visitBeignet(this);
        }
    //< beignet-accept
    }

    class Cruller extends Pastry {
    //> cruller-accept
        @Override
        void accept(PastryVisitor visitor) {
        visitor.visitCruller(this);
        }
    //< cruller-accept
    }
    //< pastries
}
