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
            } else if ("() {}*+-.,;".indexOf(ch) != -1) {
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
                // case ' ':
                // case '\t':
                // case '\r':
                //       break;
            } else {
                // Handle invalid character; skip rest of line
                hasError = true;
                System.err.println("[line " + lineNumber + "] Error: Unexpected character: " + ch);
                index = fileContents.indexOf('\n', index + 1);
                if (index == -1) {
                    index = fileContents.length();
                }
                continue; // Skip to the beginning of the next line
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

