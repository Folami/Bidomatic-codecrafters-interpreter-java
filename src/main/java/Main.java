import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    private static final String[] TOKENS = {
        "LEFT_PAREN", "RIGHT_PAREN",
        "LEFT_BRACE", "RIGHT_BRACE",
        "COMMA", "DOT", "MINUS", "PLUS",
        "SEMICOLON", "STAR", "SLASH",
        "QUESTION", "COLON", "EQ",
        "EQEQ", "NEQ", "LT", "GT",
        "LTEQ", "GTEQ", "PLUSPLUS",
        "MINUSMINUS", "ARROW",
        "IDENTIFIER", "STRING", "NUMBER",
        "AND", "OR", "NOT",
        "IF", "ELSE", "FOR",
        "WHILE", "FUN", "RETURN",
        "BREAK", "CONTINUE", "CLASS",
        "TRUE", "FALSE", "NULL",
        "EOF"
    };

    private static final String[] SINGLE_CHAR_TOKENS = {
        "(", ")", "{", "}", ",", ".", "-",
        "+", ";", "*", "/", "?", ":", "=",
        "<", ">", "!", "&", "|", "@"
    };

    private static final String[] TWO_CHAR_TOKENS = {
        "==", "!=", "<=", ">=", "//", "++",
        "--", "->", "&&", "||", "<<" , ">>"
    };

    private static final String WHITESPACE = " \t\n\r";
    private static final String DIGITS = "0123456789";
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LETTERS_DIGITS = LETTERS + DIGITS + "_";

    private static int line = 1;
    private static int position = 0;
    private static String input;
    private static int inputPos = 0;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <filename>");
            return;
        }
        try {
            File file = new File(args[0]);
            Scanner scanner = new Scanner(file);
            input = scanner.useDelimiter("\\Z").next();
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + args[0]);
            return;
        }
        while (inputPos < input.length()) {
            char c = input.charAt(inputPos);
            if (WHITESPACE.indexOf(c) != -1) {
                if (c == '\n') {
                    line++;
                }
                inputPos++;
                continue;
            }
            if (c == '/') {
                if (inputPos + 1 < input.length() && input.charAt(inputPos + 1) == '/') {
                    // Skip comment
                    while (inputPos < input.length() && input.charAt(inputPos) != '\n') {
                        inputPos++;
                    }
                    if (inputPos < input.length()) {
                        line++;
                        inputPos++;
                    }
                    continue;
                }
            }
            for (String token : SINGLE_CHAR_TOKENS) {
                if (token.length() == 1 && c == token.charAt(0)) {
                    System.out.println(token.toUpperCase().replaceFirst("([A-Z])", " $1").trim() + " null");
                    inputPos++;
                    break;
                }
            }
            for (String token : TWO_CHAR_TOKENS) {
                if (inputPos + 1 < input.length()) {
                    String twoChars = input.substring(inputPos, inputPos + 2);
                    if (twoChars.equals(token)) {
                        System.out.println(token.toUpperCase().replaceFirst("([A-Z])", " $1").trim() + " null");
                        inputPos += 2;
                        break;
                    }
                }
            }
            if (inputPos >= input.length()) {
                break;
            }
            if (c == '"' || c == '\'') {
                // Handle strings
                inputPos++;
                while (inputPos < input.length() && input.charAt(inputPos) != c) {
                    inputPos++;
                }
                if (inputPos < input.length()) {
                    inputPos++;
                }
                continue;
            }
            if (LETTERS.indexOf(c) != -1) {
                // Handle identifiers
                while (inputPos < input.length() && LETTERS_DIGITS.indexOf(input.charAt(inputPos)) != -1) {
                    inputPos++;
                }
                String identifier = input.substring(inputPos - LETTERS_DIGITS.indexOf(c), inputPos);
                System.out.println("IDENTIFIER " + identifier + " null");
                continue;
            }
            if (DIGITS.indexOf(c) != -1) {
                // Handle numbers
                while (inputPos < input.length() && DIGITS.indexOf(input.charAt(inputPos)) != -1) {
                    inputPos++;
                }
                String number = input.substring(inputPos - 1, inputPos);
                System.out.println("NUMBER " + number + " null");
                continue;
            }
            System.out.println("[line " + line + "] Error: Unexpected character: " + c);
            inputPos++;
        }
        System.out.println("EOF null");
    }
}
