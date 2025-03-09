import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        System.err.println("Logs from your program will appear here!");
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
        scanFile(fileContents); // Call the new function to handle scanning
        System.exit(0); // Exit with success code
    }

    private static void scanFile(String fileContents) {
        boolean hasError = false;
        int lineNumber = 1;
        int index = 0;

        while (index < fileContents.length()) {
            char ch = fileContents.charAt(index);
            if (ch == '\n') {
                lineNumber++;
                index++;
                continue;
            }
            hasError = handleToken(fileContents, index, lineNumber, hasError);
            index++;
        }
        System.out.println("EOF  null");
        if (hasError) {
            System.exit(65);
        }
    }

    private static boolean handleToken(String fileContents, int index, int lineNumber, boolean hasError){
        char ch = fileContents.charAt(index);
        //Handle Comments
        if (ch == '/' && index + 1 < fileContents.length() && fileContents.charAt(index + 1) == '/') {
            index = handleSingleLineComment(fileContents, index);
            return hasError;
        }
        //Handle Single Character Tokens
        if ("(){}*+-.,;".indexOf(ch) != -1) {
            handleSingleCharToken(ch);
            return hasError;
        }
        //Handle Whitespace
        if (Character.isWhitespace(ch)) {
            return hasError;
        }
        //Handle String Literals
        if (ch == '"') {
            hasError = handleStringLiteral(fileContents, index, lineNumber, hasError);
            return hasError;
        }
        //Handle Number Literals
        if (Character.isDigit(ch)) {
            handleNumberLiteral(fileContents, index);
            return hasError;
        }
        //Handle Identifiers
        if (Character.isLetter(ch) || ch == '_') {
            handleIdentifier(fileContents, index);
            return hasError;
        }
        //Handle Invalid Characters
        hasError = handleInvalidChar(ch, lineNumber, hasError);
        return hasError;
    }

    private static int handleSingleLineComment(String fileContents, int index) {
        // Skip to end of line or end of file
        while (index < fileContents.length() && fileContents.charAt(index) != '\n') {
            index++;
        }
        return index;
    }

    private static void handleSingleCharToken(char ch) {
        switch (ch) {
            case '(' -> System.out.println("LEFT_PAREN ( null");
            case ')' -> System.out.println("RIGHT_PAREN ) null");
            case '{' -> System.out.println("LEFT_BRACE { null");
            case '}' -> System.out.println("RIGHT_BRACE } null");
            case '*' -> System.out.println("STAR * null");
            case '+' -> System.out.println("PLUS + null");
            case '-' -> System.out.println("MINUS - null");
            case ',' -> System.out.println("COMMA , null");
            case '.' -> System.out.println("DOT . null");
            case ';' -> System.out.println("SEMICOLON ; null");
        }
    }

    private static boolean handleStringLiteral(String fileContents, int index, int lineNumber, boolean hasError) {
        StringBuilder stringLiteral = new StringBuilder();
        index++; // Skip opening quote
        boolean unterminated = true;
        while (index < fileContents.length()) {
            char ch = fileContents.charAt(index);
            if (ch == '"') {
                unterminated = false;
                break;
            }
            if (ch == '\n') lineNumber++;
            stringLiteral.append(ch);
            index++;
        }
        if (unterminated) {
            System.err.println("[line " + lineNumber + "] Error: Unterminated string.");
            hasError = true;
        } else {
            System.out.println("STRING \"" + stringLiteral + "\" null");
            index++; // Skip closing quote
        }
        return hasError;
    }

    private static void handleNumberLiteral(String fileContents, int index) {
        StringBuilder numberLiteral = new StringBuilder();
        while (index < fileContents.length() && Character.isDigit(fileContents.charAt(index))) {
            numberLiteral.append(fileContents.charAt(index));
            index++;
        }
        System.out.println("NUMBER " + numberLiteral + " null");
    }

    private static void handleIdentifier(String fileContents, int index) {
        StringBuilder identifier = new StringBuilder();
        while (index < fileContents.length() &&
               (Character.isLetterOrDigit(fileContents.charAt(index)) ||
                fileContents.charAt(index) == '_')) {
            identifier.append(fileContents.charAt(index));
            index++;
        }
        System.out.println("IDENTIFIER " + identifier + " null");
    }

    private static boolean handleInvalidChar(char ch, int lineNumber, boolean hasError) {
        System.err.println("[line " + lineNumber + "] Error: Unexpected character: " + ch);
        hasError = true;
        return hasError;
    }
}
