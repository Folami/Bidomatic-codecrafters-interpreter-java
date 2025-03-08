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

    // Uncomment this block to pass the first stage
    // 
    if (fileContents.length() > 0) {
      for (char ch : fileContents.toCharArray()) {
        switch (ch) {
          case '(': System.out.println("LEFT_PAREN ( null");
          case ')': System.out.println("RIGHT_PAREN ) null");
          case '{': System.out.println("LEFT_BRACE { null");
          case '}': System.out.println("RIGHT_BRACE } null");
          case ',': System.out.println("COMMA , null");
          case '.': System.out.println("DOT . null");
          case '-': System.out.println("MINUS - null");
          case '+': System.out.println("PLUS + null");
          case ';': System.out.println("SEMICOLON ; null");
          case '*': System.out.println("STAR * null"); 
        }
      }
    } 
    System.out.println("EOF  null"); // Placeholder, remove this line when implementing the scanner
  }
}
