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

            // Handle newlines
            if (ch == '\n') {
                lineNumber++;
                index++;
                continue;
            }

            // Handle comments
            if (ch == '/' && index + 1 < fileContents.length() && fileContents.charAt(index + 1) == '/') {
                // Skip to end of line or end of file
                while (index < fileContents.length() && fileContents.charAt(index) != '\n') {
                    index++;
                }
                continue;
            }

            // Handle single-character tokens
            if ("(){}*+-.,;".indexOf(ch) != -1) {
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
                index++;
                continue;
            }

            // Handle whitespace
            if (Character.isWhitespace(ch)) {
                index++;
                continue;
            }

            // Handle string literals
            if (ch == '"') {
                StringBuilder stringLiteral = new StringBuilder();
                index++; // Skip opening quote
                boolean unterminated = true;

                while (index < fileContents.length()) {
                    ch = fileContents.charAt(index);
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
                continue;
            }

            // Handle number literals
            if (Character.isDigit(ch)) {
                StringBuilder numberLiteral = new StringBuilder();
                while (index < fileContents.length() && Character.isDigit(fileContents.charAt(index))) {
                    numberLiteral.append(fileContents.charAt(index));
                    index++;
                }
                System.out.println("NUMBER " + numberLiteral + " null");
                continue;
            }

            // Handle identifiers
            if (Character.isLetter(ch) || ch == '_') {
                StringBuilder identifier = new StringBuilder();
                while (index < fileContents.length() && 
                       (Character.isLetterOrDigit(fileContents.charAt(index)) || 
                        fileContents.charAt(index) == '_')) {
                    identifier.append(fileContents.charAt(index));
                    index++;
                }
                System.out.println("IDENTIFIER " + identifier + " null");
                continue;
            }

            // Handle invalid characters
            System.err.println("[line " + lineNumber + "] Error: Unexpected character: " + ch);
            hasError = true;
            index++;
        }

        System.out.println("EOF  null");
        System.exit(hasError ? 65 : 0);
    }
}
