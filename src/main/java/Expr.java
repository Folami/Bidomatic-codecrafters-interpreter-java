//> Appendix II expr
// package com.craftinginterpreters.lox;

import java.util.List;
// import Main.LoxScanner.Token;

abstract class Expr {
    interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitBinaryExpr(Binary expr);
        R visitCallExpr(Call expr);
        R visitGetExpr(Get expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr);
        R visitSetExpr(Set expr);
        R visitSuperExpr(Super expr);
        R visitThisExpr(This expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
    }

    // Nested Expr classes here...
    
    //> expr-binary
    static class Binary extends Expr {
        Binary(Expr left, Main.LoxScanner.Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        final Expr left;
        final Main.LoxScanner.Token operator;
        final Expr right;
    }

    //> expr-grouping
    static class Grouping extends Expr {
        Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final Expr expression;
    }

    //> expr-literal
    static class Literal extends Expr {
        final Object value;

        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }

    static class Unary extends Expr {
        Unary(Main.LoxScanner.Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final Main.LoxScanner.Token operator;
        final Expr right;
    }

    //> expr-variable
    static class Variable extends Expr {
        Variable(Main.LoxScanner.Token name) {
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        final Main.LoxScanner.Token name;
    }

     
    //> expr-assign
    static class Assign extends Expr {
        Assign(Main.LoxScanner.Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

        final Main.LoxScanner.Token name;
        final Expr value;
    }

    
    //> expr-call
    static class Call extends Expr {
        Call(Expr callee, Main.LoxScanner.Token paren, List<Expr> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }

        final Expr callee;
        final Main.LoxScanner.Token paren;
        final List<Expr> arguments;
    }

    //> expr-get
    static class Get extends Expr {
        Get(Expr object, Main.LoxScanner.Token name) {
            this.object = object;
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetExpr(this);
        }

        final Expr object;
        final Main.LoxScanner.Token name;
    }

    //> expr-logical
    static class Logical extends Expr {
        Logical(Expr left, Main.LoxScanner.Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }

        final Expr left;
        final Main.LoxScanner.Token operator;
        final Expr right;
    }

    //> expr-set
    static class Set extends Expr {
        Set(Expr object, Main.LoxScanner.Token name, Expr value) {
            this.object = object;
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSetExpr(this);
        }

        final Expr object;
        final Main.LoxScanner.Token name;
        final Expr value;
    }

    //> expr-super
    static class Super extends Expr {
        Super(Main.LoxScanner.Token keyword, Main.LoxScanner.Token method) {
            this.keyword = keyword;
            this.method = method;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSuperExpr(this);
        }

        final Main.LoxScanner.Token keyword;
        final Main.LoxScanner.Token method;
    }
    
    //> expr-this
    static class This extends Expr {
        This(Main.LoxScanner.Token keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitThisExpr(this);
        }

        final Main.LoxScanner.Token keyword;
    }

    abstract <R> R accept(Visitor<R> visitor);
}
