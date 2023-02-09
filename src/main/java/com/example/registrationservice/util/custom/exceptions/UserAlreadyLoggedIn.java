package com.example.registrationservice.util.custom.exceptions;

public class UserAlreadyLoggedIn extends Exception{

    public UserAlreadyLoggedIn() {
    }

    public UserAlreadyLoggedIn(String message) {
        super(message);
    }
}
