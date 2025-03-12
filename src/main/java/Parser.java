import java.util.*;

class Parser {
    private final List<Main.LoxScanner.Token> tokens;
    private int current = 0;
    private static class ParseError extends RuntimeException {}

    Parser(List<Main.LoxScanner.Token> tokens) {
        this.tokens = tokens;
    }

    Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(Main.LoxScanner.TokenType.BANG_EQUAL, Main.LoxScanner.TokenType.EQUAL_EQUAL)) {
            Main.LoxScanner.Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(Main.LoxScanner.TokenType.GREATER, Main.LoxScanner.TokenType.GREATER_EQUAL,
                     Main.LoxScanner.TokenType.LESS, Main.LoxScanner.TokenType.LESS_EQUAL)) {
            Main.LoxScanner.Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(Main.LoxScanner.TokenType.MINUS, Main.LoxScanner.TokenType.PLUS)) {
            Main.LoxScanner.Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(Main.LoxScanner.TokenType.SLASH, Main.LoxScanner.TokenType.STAR)) {
            Main.LoxScanner.Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(Main.LoxScanner.TokenType.BANG, Main.LoxScanner.TokenType.MINUS)) {
            Main.LoxScanner.Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() {
        if (match(Main.LoxScanner.TokenType.FALSE)) return new Expr.Literal(false);
        if (match(Main.LoxScanner.TokenType.TRUE)) return new Expr.Literal(true);
        if (match(Main.LoxScanner.TokenType.NIL)) return new Expr.Literal(null);

        if (match(Main.LoxScanner.TokenType.NUMBER, Main.LoxScanner.TokenType.STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(Main.LoxScanner.TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(Main.LoxScanner.TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    private boolean match(Main.LoxScanner.TokenType... types) {
        for (Main.LoxScanner.TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(Main.LoxScanner.TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Main.LoxScanner.Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == Main.LoxScanner.TokenType.EOF;
    }

    private Main.LoxScanner.Token peek() {
        return tokens.get(current);
    }

    private Main.LoxScanner.Token previous() {
        return tokens.get(current - 1);
    }

    private Main.LoxScanner.Token consume(Main.LoxScanner.TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Main.LoxScanner.Token token, String message) {
        Main.LoxScanner.error(token.line, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == Main.LoxScanner.TokenType.SEMICOLON) 
                return;

            switch (peek().type) {
                case Main.LoxScanner.TokenType.CLASS:
                case Main.LoxScanner.TokenType.FUN:
                case Main.LoxScanner.TokenType.VAR:
                case Main.LoxScanner.TokenType.FOR:
                case Main.LoxScanner.TokenType.IF:
                case Main.LoxScanner.TokenType.WHILE:
                case Main.LoxScanner.TokenType.PRINT:
                case Main.LoxScanner.TokenType.RETURN:
                return;
            }
            advance();
        }
    }
}
