package com.oop.project.exception;
/*
 * Exception when login authenticatiton failed
 */
public class AuthenticationException extends Exception {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException() {
        super("");
    }
}
