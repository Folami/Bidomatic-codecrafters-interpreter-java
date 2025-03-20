// package com.craftinginterpreters.lox;

import java.io.*;
import java.nio.file.*;
import java.util.*;


public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: ./your_program.sh <command> <filename>");
            System.exit(1);
        }

        String command = args[0];
        String filename = args[1];
        String fileContents = "";
        try {
            fileContents = Files.readString(Path.of(filename));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }

        Lox lox = new Lox();
        switch (command) {
            case "tokenize":
                List<Token> tokens = lox.ruLoxScanner(fileContents);
                for (Token token : tokens) {
                    System.out.println(token);
                }
                break;

            case "parse":
                List<Expr> expressions = lox.runLoxParser(fileContents);
                for (Expr expression : expressions) {
                    System.out.println(new AstPrinter().print(expression));
                }
                break;

            case "evaluate":
                List<Expr> expressionsToInterpret = lox.runLoxInterpreter(fileContents);
                for (Expr expression : expressionsToInterpret) {
                    lox.interpreter.interpretExpression(expression);
                }
                break;

            case "run":
                List<Stmt> statementsToRun = lox.runLox(fileContents);
                lox.interpreter.interpretStatements(statementsToRun);
                break;

            default:
                System.err.println("Unknown command: " + command);
                System.exit(1);
        }

        if (Lox.hadError) 
            System.exit(65);

        if (Lox.hadRuntimeError)
            System.exit(70);
    }
}