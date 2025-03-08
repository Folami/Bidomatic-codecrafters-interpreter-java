import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
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

    if (fileContents.length() > 0) {
      for (int i = 0; i < fileContents.length(); i++) {
        char ch = fileContents.charAt(i);
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
          case '=' -> {
            if (i + 1 < fileContents.length() && fileContents.charAt(i + 1) == '=') {
              System.out.println("EQUAL_EQUAL == null");
              i++; // Skip the next character
            } else {
              System.out.println("EQUAL = null");
            }
          }
          case '!' -> {
            if (i + 1 < fileContents.length() && fileContents.charAt(i + 1) == '=') {
              System.out.println("BANG_EQUAL != null");
              i++; // Skip the next character
            } else {
              System.out.println("BANG ! null");
            }
          }
          case '<' -> {
            if (i + 1 < fileContents.length() && fileContents.charAt(i + 1) == '=') {
              System.out.println("LESS_EQUAL <= null");
              i++; // Skip the next character
            } else {
              System.out.println("LESS < null");
            }
          }
          case '>' -> {
            if (i + 1 < fileContents.length() && fileContents.charAt(i + 1) == '=') {
              System.out.println("GREATER_EQUAL >= null");
              i++; // Skip the next character
            } else {
              System.out.println("GREATER > null");
            }
          }
          case ' ', '\r', '\t', '\n' -> {} // Ignore whitespace
          default -> {
            // Handle unsupported characters
            System.err.println("[line 1] Error: Unexpected character: " + ch);
            hasError = true;
          }
        }
      }
    }

    // Print EOF token regardless of file contents
    System.out.println("EOF  null");

    if (hasError) {
      System.exit(65);
    } else {
      System.exit(0);
    }
  }
}
