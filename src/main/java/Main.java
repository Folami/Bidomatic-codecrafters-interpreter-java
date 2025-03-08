import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    boolean hasError = false;
    int lineNumber = 1;

    if (fileContents.length() > 0) {
      for (int i = 0; i < fileContents.length(); i++) {
        char ch = fileContents.charAt(i);
        
        // Handle string literals
        if (ch == '"') {
          StringBuilder string = new StringBuilder();
          i++;
          boolean isUnterminated = true;
          
          while (i < fileContents.length()) {
            char c = fileContents.charAt(i);
            if (c == '"') {
              isUnterminated = false;
              break;
            }
            if (c == '\n') lineNumber++;
            string.append(c);
            i++;
          }
          
          if (isUnterminated) {
            System.err.println("[line " + lineNumber + "] Error: Unterminated string");
            System.exit(65);
          }
          
          System.out.println("STRING \"" + string + "\" " + string);
          continue;
        }
        
        // Handle number literals
        if (Character.isDigit(ch)) {
          StringBuilder number = new StringBuilder();
          boolean hasDecimalPoint = false;
          
          while (i < fileContents.length()) {
            char c = fileContents.charAt(i);
            if (c == '.' && !hasDecimalPoint && i + 1 < fileContents.length() 
                && Character.isDigit(fileContents.charAt(i + 1))) {
              hasDecimalPoint = true;
              number.append(c);
            } else if (Character.isDigit(c)) {
              number.append(c);
            } else {
              i--;
              break;
            }
            i++;
          }
          System.out.println("NUMBER " + number + " " + number);
          continue;
        }
        
        // Handle identifiers
        if (Character.isLetter(ch) || ch == '_') {
          StringBuilder identifier = new StringBuilder();
          
          while (i < fileContents.length()) {
            char c = fileContents.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '_') {
              identifier.append(c);
            } else {
              i--;
              break;
            }
            i++;
          }
          System.out.println("IDENTIFIER " + identifier + " null");
          continue;
        }

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
              i++;
            } else {
              System.out.println("EQUAL = null");
            }
          }
          case '!' -> {
            if (i + 1 < fileContents.length() && fileContents.charAt(i + 1) == '=') {
              System.out.println("BANG_EQUAL != null");
              i++;
            } else {
              System.out.println("BANG ! null");
            }
          }
          case '<' -> {
            if (i + 1 < fileContents.length() && fileContents.charAt(i + 1) == '=') {
              System.out.println("LESS_EQUAL <= null");
              i++;
            } else {
              System.out.println("LESS < null");
            }
          }
          case '>' -> {
            if (i + 1 < fileContents.length() && fileContents.charAt(i + 1) == '=') {
              System.out.println("GREATER_EQUAL >= null");
              i++;
            } else {
              System.out.println("GREATER > null");
            }
          }
          case '/' -> {
            if (i + 1 < fileContents.length() && fileContents.charAt(i + 1) == '/') {
              while (i < fileContents.length() && fileContents.charAt(i) != '\n') {
                i++;
              }
              i--; // Adjust for the upcoming increment
            } else {
              System.out.println("SLASH / null");
            }
          }
          case ' ', '\r', '\t', '\n' -> {
            if (ch == '\n') lineNumber++;
          }
          default -> {
            if (!Character.isWhitespace(ch)) {
              System.err.println("[line " + lineNumber + "] Error: Unexpected character: " + ch);
              System.exit(65);
            }
          }
        }
      }
    }

    System.out.println("EOF  null");
    System.exit(0);
  }
}
