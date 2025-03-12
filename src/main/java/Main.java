// package com.craftinginterpreters.lox;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: ./your_program.sh tokenize <filename>");
            System.exit(1);
        }
        String command = args[0];
        String filename = args[1];
        if (!command.equals("tokenize")) {
            System.err.println("Unknown command: " + command);
            System.exit(1);
        }
        String fileContents = "";
        try {
            fileContents = Files.readString(Path.of(filename));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
        if (fileContents.length() > 0) {
            
            
            if (LoxScanner.hadError) {
                System.exit(65);
            }
        } else {
            System.out.println("EOF  null"); // Placeholder, remove this line when implementing the scanner
        }
        try {
            switch (command) {
                case "tokenize":
                    LoxScanner scanner = new LoxScanner(fileContents);
                    scanner.scanTokens();
                    break;
                case "parse":
                    Parser parser = new Parser(tokens);
                    parser.parse();
                    break;
                default:
                    System.err.println("Unknown command: " + command);
                    System.exit(1);
                    break;
            }
        } catch (Exception e) {
            System.exit(Integer.parseInt(e.getMessage()));
        }
    }

    protected static class LoxScanner {
        private static String source;
        private static final List<Token> tokens = new ArrayList<>();
        private static int start = 0;
        private static int current = 0;
        private static int line = 1;
        private static boolean hadError = false;

        enum TokenType {
            // Single-character tokens.
            LEFT_PAREN, RIGHT_PAREN,
            LEFT_BRACE, RIGHT_BRACE,
            DOT, COMMA, SEMICOLON,
            MINUS, PLUS, SLASH, STAR,
            // One or two character tokens.
            BANG, BANG_EQUAL,
            LESS, LESS_EQUAL,
            EQUAL, EQUAL_EQUAL,
            GREATER, GREATER_EQUAL,
            // Literals.
            IDENTIFIER, STRING, NUMBER,
            // Keywords.
            AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
            PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,
            // End of file.
            EOF
        }

        private static final Map<String, TokenType> keywords;
        static {
            keywords = new HashMap<>();
            keywords.put("if", TokenType.IF);
            keywords.put("or", TokenType.OR);
            keywords.put("var", TokenType.VAR);
            keywords.put("and", TokenType.AND);
            keywords.put("for", TokenType.FOR);
            keywords.put("fun", TokenType.FUN);
            keywords.put("nil", TokenType.NIL);
            keywords.put("this", TokenType.THIS);
            keywords.put("true", TokenType.TRUE);
            keywords.put("else", TokenType.ELSE);
            keywords.put("print", TokenType.PRINT);
            keywords.put("super", TokenType.SUPER);
            keywords.put("while", TokenType.WHILE);
            keywords.put("class", TokenType.CLASS);
            keywords.put("false", TokenType.FALSE);
            keywords.put("return", TokenType.RETURN);
        }

        protected static class Token {
            final TokenType type;
            final String lexeme;
            final Object literal;
            final int line;

            Token(TokenType type, String lexeme, Object literal, int line) {
                this.type = type;
                this.lexeme = lexeme;
                this.literal = literal;
                this.line = line;
            }
            public String toString() {
                return type + " " + lexeme + " " + literal;
            }
        }

        LoxScanner(String source) {
            this.source = source;
        }

        private static List<Token> scanTokens() {
            while (!isAtEnd()) {
                start = current;
                scanToken();
            }
            tokens.add(new Token(TokenType.EOF, "", null, line));
            return tokens;
        }

        private static boolean isAtEnd() {
            return current >= source.length();
        }

        private static void scanToken() {
            char c = advance();
            switch (c) {
                case '(': addToken(TokenType.LEFT_PAREN); break;
                case ')': addToken(TokenType.RIGHT_PAREN); break;
                case '{': addToken(TokenType.LEFT_BRACE); break;
                case '}': addToken(TokenType.RIGHT_BRACE); break;
                case ',': addToken(TokenType.COMMA); break;
                case '.': addToken(TokenType.DOT); break;
                case '-': addToken(TokenType.MINUS); break;
                case '+': addToken(TokenType.PLUS); break;
                case ';': addToken(TokenType.SEMICOLON); break;
                case '*': addToken(TokenType.STAR); break;
                case '!': handleBang(); break;
                case '=': handleEqual(); break;
                case '<': handleLess(); break;
                case '>': handleGreater(); break;
                case '/': handleSlash(); break;
                case ' ':
                case '\r':
                case '\t': break;
                case '\n': line++; break;
                case '"': string(); break;
                default:
                    if (isDigit(c)) {
                        number();
                    } else if (isAlpha(c)) {
                        identifier();
                    } else {
                        error(line, "Unexpected character: " + c);
                    }
                    break;
            }
        }

        private static void handleBang() {
            addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
        }

        private static void handleEqual() {
            addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
        }

        private static void handleLess() {
            addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
        }

        private static void handleGreater() {
            addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
        }

        private static void handleSlash() {
            if (match('/')) {
                while (peek() != '\n' && !isAtEnd()) advance();
            } else {
                addToken(TokenType.SLASH);
            }
        }

        private static char advance() {
            return source.charAt(current++);
        }

        private static void addToken(TokenType type) {
            addToken(type, null);
        }

        private static void addToken(TokenType type, Object literal) {
            String text = source.substring(start, current);
            tokens.add(new Token(type, text, literal, line));
        }

        private static boolean match(char expected) {
            if (isAtEnd()) return false;
            if (source.charAt(current) != expected) return false;
            current++;
            return true;
        }

        private static char peek() {
            if (isAtEnd()) return '\0';
            return source.charAt(current);
        }

        private static void string() {
            while (peek() != '"' && !isAtEnd()) {
                if (peek() == '\n') line++;
                advance();
            }
            if (isAtEnd()) {
                error(line, "Unterminated string.");
                return;
            }
            advance();
            String value = source.substring(start + 1, current - 1);
            addToken(TokenType.STRING, value);
        }

        private static boolean isDigit(char c) {
            return c >= '0' && c <= '9';
        }

        private static void number() {
            while (isDigit(peek())) advance();
            if (peek() == '.' && isDigit(peekNext())) {
                advance();
                while (isDigit(peek())) advance();
            }
            addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
        }

        private static char peekNext() {
            if (current + 1 >= source.length()) return '\0';
            return source.charAt(current + 1);
        }

        private static boolean isAlpha(char c) {
            return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
        }

        private static void identifier() {
            while (isAlphaNumeric(peek())) advance();
            String text = source.substring(start, current);
            TokenType type = keywords.get(text);
            if (type == null) type = TokenType.IDENTIFIER;
            addToken(type);
        }

        private static boolean isAlphaNumeric(char c) {
            return isAlpha(c) || isDigit(c);
        }

        protected static void error(int line, String message) {
            report(line, "", message);
        }

        private static void report(int line, String where, String message) {
            System.err.println("[line " + line + "] Error" + where + ": " + message);
            hadError = true;
        }
    }
}