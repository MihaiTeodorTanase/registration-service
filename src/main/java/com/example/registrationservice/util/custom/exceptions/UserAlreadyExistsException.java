package com.example.registrationservice.util.custom.exceptions;

public class UserAlreadyExistsException extends Exception{

    public UserAlreadyExistsException() {
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
