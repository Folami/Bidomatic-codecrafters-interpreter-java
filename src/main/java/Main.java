import java.io.*;
import java.nio.*;
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
            LoxScanner scanner = new LoxScanner(fileContents);
            scanner.scanTokens();
            if (LoxScanner.hadError) {
                System.exit(65);
            }
        } else {
            System.out.println("EOF  null"); // Placeholder, remove this line when implementing the scanner
        }
    }

    private static class LoxScanner {
        private static String source;
        private static final List<Token> tokens = new ArrayList<>();
        private static int start = 0;
        private static int current = 0;
        private static int line = 1;
        private static boolean hadError = false;

        enum TokenType {
            // Single-character tokens.
            LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
            COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,
            // One or two character tokens.
            BANG, BANG_EQUAL,
            EQUAL, EQUAL_EQUAL,
            GREATER, GREATER_EQUAL,
            LESS, LESS_EQUAL,
            // Literals.
            IDENTIFIER, STRING, NUMBER,
            // Keywords.
            AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
            PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

            EOF
        }

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

        private static class Token {
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

        private static void scanTokens()  {
            while (!isAtEnd()) {
                start = current;
                scanToken();
            }
            tokens.add(new Token(TokenType.EOF, "", null, line));
        }

        //> is-at-end
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
                case '*': addToken(TokenType.STAR); break; // [slash]
                //> two-char-tokens
                case '!':
                    addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                    break;
                case '=':
                    addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                    break;
                case '<':
                    addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                    break;
                case '>':
                    addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                    break;
                //> slash
                case '/':
                    if (match('/')) {
                        // A comment goes until the end of the line.
                        while (peek() != '\n' && !isAtEnd()) 
                            advance();
                    } else {
                        addToken(TokenType.SLASH);
                    }
                    break;
                //> whitespace
                case ' ':
                case '\r':
                case '\t':
                    // Ignore whitespace.
                    break;
                case '\n':
                    line++;
                    break;
                //> string-start
                case '"': 
                    string(); 
                    break;
                //> char-error
                default:
                    //> digit-start
                    if (isDigit(c)) {
                        number();
                    //> identifier-start
                    } else if (isAlpha(c)) {
                        identifier();
                    } else {
                        LoxScanner.error(line, "Unexpected character.");
                    }
                    break;
            }
        }

        //> advance-and-add-token
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
        //< advance-and-add-token

        //> match
        private static boolean match(char expected) {
            if (isAtEnd()) 
                return false;

            if (source.charAt(current) != expected) 
                return false;

            current++;
            return true;
        }

        //> peek
        private static char peek() {
            if (isAtEnd()) 
                return '\0';

            return source.charAt(current);
        }

        //> string
        private static void string() {
            while (peek() != '"' && !isAtEnd()) {
                if (peek() == '\n') 
                        line++;
                advance();
            }
            if (isAtEnd()) {
                LoxScanner.error(line, "Unterminated string.");
                return;
            }
            // The closing ".
            advance();
            // Trim the surrounding quotes.
            String value = source.substring(start + 1, current - 1);
            addToken(TokenType.STRING, value);
        }

        //> is-digit
        private static boolean isDigit(char c) {
            return c >= '0' && c <= '9';
        }

        //> number
        private static void number() {
            while (isDigit(peek())) 
                advance();
            // Look for a fractional part.
            if (peek() == '.' && isDigit(peekNext())) {
                // Consume the "."
                advance();
                while (isDigit(peek())) 
                    advance();
            }
            addToken(
                TokenType.NUMBER,
                Double.parseDouble(source.substring(
                    start, 
                    current
                ))
            );
        }

        //> peek-next
        private static char peekNext() {
            if (current + 1 >= source.length()) 
                return '\0';

            return source.charAt(current + 1);
        }

        //> is-alpha
        private static boolean isAlpha(char c) {
            return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                    c == '_';
        }

        //> identifier
        private static void identifier() {
            while (isAlphaNumeric(peek())) 
                advance();
            //Scanning identifier < Scanning keyword-type
            String text = source.substring(start, current);
            TokenType type = keywords.get(text);
            if (type == null) 
                type = TokenType.IDENTIFIER;
            addToken(type);
        }

        private static boolean isAlphaNumeric(char c) {
            return isAlpha(c) || isDigit(c);
        }

        private static void error(int line, String message) {
            report(line, "", message);
        }

        private static void report(int line, String where, String message) {
            System.err.println(
                "[line " + line + "] Error" + where + ": " + message
            );
            hadError = true;
        }
    } 
}