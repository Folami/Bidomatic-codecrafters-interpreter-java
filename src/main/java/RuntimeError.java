// package com.craftinginterpreters.lox;

public class RuntimeError extends RuntimeException {
    final Main.LoxScanner.Token token;

    public RuntimeError(Main.LoxScanner.Token token, String message) {
        super(message);
        this.token = token;
    }
}