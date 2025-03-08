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
        int lineNumber = 1; // Initialize line number
        int index = 0;
        while(index < fileContents.length()) {
            char ch = fileContents.charAt(index);
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
                case '\n':
                    lineNumber++; // Increment line number on newline
                    break;
                case ' ':
                case '\t':
                case '\r':
                    //Ignore whitespace;
                    break;
                default:
                    // Handle unsupported characters
                    System.err.println("[line " + lineNumber + "] Error: Unexpected character: " + ch);
                    hasError = true;
                    break;
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
