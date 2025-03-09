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
        tokenize(fileContents);
    }

    private static void tokenize(String fileContents) {
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
            
            // Handle comments first
            if (ch == '/' && index + 1 < fileContents.length() && fileContents.charAt(index + 1) == '/') {
                index = handleComment(fileContents, index);
                continue;
            }
            
            // Then handle division operator
            if (ch == '/') {
                System.out.println("SLASH / null");
                index++;
                continue;
            }
            
            if ("(){}*+-.,;".indexOf(ch) != -1) {
                handleSingleCharacterToken(ch);
                index++;
                continue;
            }
            
            if (Character.isWhitespace(ch)) {
                index++;
                continue;
            }
            
            if (ch == '"') {
                index = handleStringLiteral(fileContents, index, lineNumber);
                continue;
            }
            
            if (Character.isDigit(ch)) {
                index = handleNumberLiteral(fileContents, index);
                continue;
            }
            
            if (Character.isLetter(ch) || ch == '_') {
                index = handleIdentifier(fileContents, index);
                continue;
            }
            
            if (handleRelationalOperator(fileContents, index, lineNumber)) {
                continue;
            }
            
            if (handleAssignmentOrEqualityOperator(fileContents, index, lineNumber)) {
                continue;
            }
            
            if (handleNegationOrInequalityOperator(fileContents, index, lineNumber)) {
                continue;
            }
            
            System.err.println("[line " + lineNumber + "] Error: Unexpected character: " + ch);
            hasError = true;
            index++;
        }
        
        System.out.println("EOF  null");
        System.exit(hasError ? 65 : 0);
    }

    private static int handleComment(String fileContents, int index) {
        while (index < fileContents.length() && fileContents.charAt(index) != '\n') {
            index++;
        }
        return index;
    }

    private static void handleSingleCharacterToken(char ch) {
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

    private static int handleStringLiteral(String fileContents, int index, int lineNumber) {
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
        } else {
            System.out.println("STRING \"" + stringLiteral + "\" null");
            index++; // Skip closing quote
        }
        return index;
    }

    private static int handleNumberLiteral(String fileContents, int index) {
        StringBuilder numberLiteral = new StringBuilder();
        while (index < fileContents.length() && Character.isDigit(fileContents.charAt(index))) {
            numberLiteral.append(fileContents.charAt(index));
            index++;
        }
        System.out.println("NUMBER " + numberLiteral + " null");
        return index;
    }

    private static int handleIdentifier(String fileContents, int index) {
        StringBuilder identifier = new StringBuilder();
        while (index < fileContents.length() && 
               (Character.isLetterOrDigit(fileContents.charAt(index)) || 
                fileContents.charAt(index) == '_')) {
            identifier.append(fileContents.charAt(index));
            index++;
        }
        System.out.println("IDENTIFIER " + identifier + " null");
        return index;
    }

    private static boolean handleRelationalOperator(String fileContents, int index, int lineNumber) {
        if (index + 1 >= fileContents.length()) {
            return false;
        }
        char c1 = fileContents.charAt(index);
        char c2 = fileContents.charAt(index + 1);
        if (c1 == '<' && c2 == '=') {
            System.out.println("LESS_EQUAL <= null");
            index += 2;
            return true;
        } else if (c1 == '>' && c2 == '=') {
            System.out.println("GREATER_EQUAL >= null");
            index += 2;
            return true;
        } else if (c1 == '<') {
            System.out.println("LESS < null");
            index++;
            return true;
        } else if (c1 == '>') {
            System.out.println("GREATER > null");
            index++;
            return true;
        }
        return false;
    }

    private static boolean handleAssignmentOrEqualityOperator(String fileContents, int index, int lineNumber) {
        if (index + 1 >= fileContents.length()) {
            return false;
        }
        char c1 = fileContents.charAt(index);
        char c2 = fileContents.charAt(index + 1);
        if (c1 == '=' && c2 == '=') {
            System.out.println("EQUAL_EQUAL == null");
            return true;
        } else if (c1 == '=') {
            System.out.println("EQUAL = null");
            return true;
        }
        return false;
    }

    private static boolean handleNegationOrInequalityOperator(String fileContents, int index, int lineNumber) {
        if (index + 1 >= fileContents.length()) {
            return false;
        }
        char c1 = fileContents.charAt(index);
        char c2 = fileContents.charAt(index + 1);
        if (c1 == '!' && c2 == '=') {
            System.out.println("BANG_EQUAL != null");
            return true;
        } else if (c1 == '!') {
            System.out.println("BANG ! null");
            return true;
        }
        return false;
    }
}

