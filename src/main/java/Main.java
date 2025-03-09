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

        boolean hasError = false;
        int lineNumber = 1;
        int index = 0;

        while (index < fileContents.length()) {
            char ch = fileContents.charAt(index);

            if (ch == '\n') {
                lineNumber++;
            } else if (ch == '/') { // Check for comments
                if (index + 1 < fileContents.length() && fileContents.charAt(index + 1) == '/') {
                    // Single-line comment; skip to next line
                    index = fileContents.indexOf('\n', index + 1);
                    if (index == -1) {
                        index = fileContents.length(); // Handle comment to EOF
                    }
                    lineNumber++;
                    continue;
                }
            } else if ("(){}*+-.,;".indexOf(ch) != -1) {
                // Valid single-character token handling remains the same ...
                switch (ch) {
                    case '(':
                        System.out.println("LEFT_PAREN ( null");
                        break;
                    case ')':
                        System.out.println("RIGHT_PAREN ) null");
                        break;
                    case '{':
                        System.out.println("LEFT_BRACE { null");
                        break;
                    case '}':
                        System.out.println("RIGHT_BRACE } null");
                        break;
                    case '*':
                        System.out.println("STAR * null");
                        break;
                    case '+':
                        System.out.println("PLUS + null");
                        break;
                    case '-':
                        System.out.println("MINUS - null");
                        break;
                    case ',':
                        System.out.println("COMMA , null");
                        break;
                    case '.':
                        System.out.println("DOT . null");
                        break;
                    case ';':
                        System.out.println("SEMICOLON ; null");
                        break;
                }
            } else if (Character.isWhitespace(ch)) {
                // Ignore whitespace
            } else if (ch == '"') {
                // Handle string literals
                StringBuilder stringLiteral = new StringBuilder();
                index++;
                boolean unterminated = true;
                while (index < fileContents.length()) {
                    ch = fileContents.charAt(index);
                    if (ch == '"') {
                        unterminated = false;
                        break;
                    }
                    if (ch == '\n') {
                        lineNumber++;
                    }
                    stringLiteral.append(ch);
                    index++;
                }
                if (unterminated) {
                    System.err.println("[line " + lineNumber + "] Error: Unterminated string.");
                    hasError = true;
                } else {
                    System.out.println("STRING \"" + stringLiteral.toString() + "\" null");
                }
            } else if (Character.isDigit(ch)) {
                // Handle number literals
                StringBuilder numberLiteral = new StringBuilder();
                while (index < fileContents.length() && Character.isDigit(fileContents.charAt(index))) {
                    numberLiteral.append(fileContents.charAt(index));
                    index++;
                }
                index--; // Adjust for the loop increment
                System.out.println("NUMBER " + numberLiteral.toString() + " null");
            } else if (Character.isLetter(ch) || ch == '_') {
                // Handle identifiers
                StringBuilder identifier = new StringBuilder();
                while (index < fileContents.length() && (Character.isLetterOrDigit(fileContents.charAt(index)) || fileContents.charAt(index) == '_')) {
                    identifier.append(fileContents.charAt(index));
                    index++;
                }
                index--; // Adjust for the loop increment
                System.out.println("IDENTIFIER " + identifier.toString() + " null");
            } else {
                // Handle invalid character
                hasError = true;
                System.err.println("[line " + lineNumber + "] Error: Unexpected character: " + ch);
            }
            index++;
        }

        System.out.println("EOF  null");
        if (hasError) {
            System.exit(65);
        } else {
            System.exit(0);
        }
    }
}

