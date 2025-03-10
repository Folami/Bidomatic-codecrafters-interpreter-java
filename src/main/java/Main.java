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
        } else {
            System.out.println("EOF  null"); // Placeholder, remove this line when implementing the scanner
        }
    }

    private static class LoxScanner {
        private final String source;
        private final List<Token> tokens = new ArrayList<>();
        private int start = 0;
        private int current = 0;
        private int line = 1;
        static boolean hadError = false;

        private static final Map<String, TokenType> keywords;
        
        static {
            keywords = new HashMap<>();
            keywords.put("and",    AND);
            keywords.put("class",  CLASS);
            keywords.put("else",   ELSE);
            keywords.put("false",  FALSE);
            keywords.put("for",    FOR);
            keywords.put("fun",    FUN);
            keywords.put("if",     IF);
            keywords.put("nil",    NIL);
            keywords.put("or",     OR);
            keywords.put("print",  PRINT);
            keywords.put("return", RETURN);
            keywords.put("super",  SUPER);
            keywords.put("this",   THIS);
            keywords.put("true",   TRUE);
            keywords.put("var",    VAR);
            keywords.put("while",  WHILE);
        }

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

        class Token {
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
        private boolean isAtEnd() {
            return current >= source.length();
        }

        private void scanToken() {
            char c = advance();
            switch (c) {
                case '(': addToken(LEFT_PAREN); break;
                case ')': addToken(RIGHT_PAREN); break;
                case '{': addToken(LEFT_BRACE); break;
                case '}': addToken(RIGHT_BRACE); break;
                case ',': addToken(COMMA); break;
                case '.': addToken(DOT); break;
                case '-': addToken(MINUS); break;
                case '+': addToken(PLUS); break;
                case ';': addToken(SEMICOLON); break;
                case '*': addToken(STAR); break; // [slash]
                //> two-char-tokens
                case '!':
                    addToken(match('=') ? BANG_EQUAL : BANG);
                    break;
                case '=':
                    addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                    break;
                case '<':
                    addToken(match('=') ? LESS_EQUAL : LESS);
                    break;
                case '>':
                    addToken(match('=') ? GREATER_EQUAL : GREATER);
                    break;
                //> slash
                case '/':
                    if (match('/')) {
                        // A comment goes until the end of the line.
                        while (peek() != '\n' && !isAtEnd()) 
                            advance();
                    } else {
                        addToken(SLASH);
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
        private char advance() {
            return source.charAt(current++);
        }

        private void addToken(TokenType type) {
            addToken(type, null);
        }

        private void addToken(TokenType type, Object literal) {
            String text = source.substring(start, current);
            tokens.add(new Token(type, text, literal, line));
        }
        //< advance-and-add-token

        //> match
        private boolean match(char expected) {
            if (isAtEnd()) 
                return false;

            if (source.charAt(current) != expected) 
                return false;

            current++;
            return true;
        }

        //> peek
        private char peek() {
            if (isAtEnd()) 
                return '\0';

            return source.charAt(current);
        }

        //> string
        private void string() {
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
            addToken(STRING, value);
        }

        //> is-digit
        private boolean isDigit(char c) {
            return c >= '0' && c <= '9';
        }

        //> number
        private void number() {
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
                NUMBER,
                Double.parseDouble(source.substring(
                    start, 
                    current
                ))
            );
        }

        //> peek-next
        private char peekNext() {
            if (current + 1 >= source.length()) 
                return '\0';

            return source.charAt(current + 1);
        }

        //> is-alpha
        private boolean isAlpha(char c) {
            return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                    c == '_';
        }

        //> identifier
        private void identifier() {
            while (isAlphaNumeric(peek())) 
                advance();
            //Scanning identifier < Scanning keyword-type
            String text = source.substring(start, current);
            TokenType type = keywords.get(text);
            if (type == null) 
                type = IDENTIFIER;
            addToken(type);
        }

        private boolean isAlphaNumeric(char c) {
            return isAlpha(c) || isDigit(c);
        }

        static void error(int line, String message) {
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