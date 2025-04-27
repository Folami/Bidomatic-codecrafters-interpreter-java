// package com.craftinginterpreters.lox;

import java.util.*;
// import static com.craftinginterpreters.lox.TokenType.*; 


class Scanner {
    private static String source;
    private static final List<Token> tokens = new ArrayList<>();
    private static int start = 0;
    private static int current = 0;
    private static int line = 1;
        
    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }

    protected static List<Token> scanTokens() {
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
            // Single-character tokens.
            case '(':
            case ')':
            case '{':
            case '}':
            case ',':
            case '.':
            case '-':
            case '+':
            case ';':
            case '*':
                handleSingleCharacterToken(c);
                break;
            // One or two character tokens.
            case '!':
            case '=':
            case '<':
            case '>':
                handleOneOrTwoCharacterToken(c);
                break;
            // Other cases, for example:
            case '?':
            case ':':
                // Handle these if needed.
                break;
            // Slash case.
            case '/':
                handleSlashToken();
                break;
            // Whitespace.
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            default:
                if (Character.isDigit(c)) {
                    number();
                } else if (Character.isAlphabetic(c) || c == '_') {
                    identifier();
                } else {
                    Lox.error(line, "Unexpected character: " + c);
                }
                break;
        }
    }

    private static void handleSingleCharacterToken(char c) {
        switch (c) {
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;
        }
    }

    private static void handleOneOrTwoCharacterToken(char c) {
        switch (c) {
            case '!':
                addToken(
                    match('=') 
                    ? TokenType.BANG_EQUAL 
                    : TokenType.BANG
                );
                break;
            case '=':
                addToken(
                    match('=') 
                    ? TokenType.EQUAL_EQUAL 
                    : TokenType.EQUAL
                );
                break;
            case '<':
                addToken(
                    match('=') 
                    ? TokenType.LESS_EQUAL 
                    : TokenType.LESS
                );
                break;
            case '>':
                addToken(
                    match('=') 
                    ? TokenType.GREATER_EQUAL 
                    : TokenType.GREATER
                );
                break;
        }
    }

    private static void handleSlashToken() {
        if (match('/')) {
            while (peek() != '\n' && !isAtEnd()) {
                advance();
            }
        } else if (match('*')) {
            // Block comment.
            while (!isAtEnd() && !(peek() == '*' && peekNext() == '/')) {
                if (peek() == '\n') line++;
                advance();
            }
            // Consume closing '*/'
            if (!isAtEnd()) {
                advance(); // for '*'
                advance(); // for '/'
            }
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
        Token token = new Token(type, text, literal, line);
        tokens.add(token);
    }

    private static boolean match(char expected) {
        if (isAtEnd()) 
            return false;
                
        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    private static char peek() {
        if (isAtEnd()) 
            return '\0';

        return source.charAt(current);
    }

    private static void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') 
                line++;
            advance();
        }
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }
        advance();
        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static void number() {
        while (isDigit(peek())) 
            advance();
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) 
                advance();
        }
        // Parse the number as a double.
        String number = source.substring(start, current);
        addToken(TokenType.NUMBER, Double.parseDouble(number));
    }

    private static char peekNext() {
        if (current + 1 >= source.length()) 
            return '\0';

        return source.charAt(current + 1);
    }

    private static boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
               c == '_';
    }

    private static void identifier() {
        while (isAlphaNumeric(peek())) 
            advance();
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) 
            type = TokenType.IDENTIFIER;
        addToken(type);
    }

    private static boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}
