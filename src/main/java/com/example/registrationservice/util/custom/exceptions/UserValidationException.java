package com.example.registrationservice.util.custom.exceptions;

public class UserValidationException extends Exception{

    public UserValidationException() {
    }

    public UserValidationException(String message) {
        super(message);
    }
}
