// package com.craftinginterpreters.lox;

import java.io.*;
import java.nio.*;
import java.util.*;

public class Lox {
    static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;


    protected static List<Token> runLoxScanner(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        return tokens;
    }

    protected static Expr runLoxParser(String source) {
        List<Token> tokens = runLoxScanner(source);
        Parser parser = new Parser(tokens);
        Expr expression = parser.parseExpression();
        return expression;
    }

    protected static void runResolver(List<Stmt> statements, Interpreter interpreter) {
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
    }

    protected static Expr runLoxInterpreter(String source) {
        Expr expression = runLoxParser(source);
        return expression;
    }

    protected static List<Stmt> runLox(String source) {
        List<Token> tokens = runLoxScanner(source);
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parseStatements();
        return statements;
    }

    // lox-error
    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where,String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    // Parsing Expressions token-error
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    // Evaluating Expressions runtime-error-method
    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
            "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }
}