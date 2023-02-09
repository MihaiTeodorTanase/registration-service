package com.example.registrationservice.util.custom.security;

import com.example.registrationservice.entities.user.UserModel;
import com.example.registrationservice.util.custom.exceptions.UserNotFoundException;
import com.example.registrationservice.util.custom.exceptions.UserValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    private PasswordManager passwordManager;

    @Autowired
    public UserValidator(PasswordManager passwordManager) {
        this.passwordManager = passwordManager;
    }

    public Boolean validateUser (UserModel dbUser, UserModel receivedUser) throws UserValidationException {
        if(dbUser.getEmail().equals(receivedUser.getEmail()) &&
                passwordManager.decrypt(dbUser.getPassword()).equals(receivedUser.getPassword())){
            return true;
        }
        throw new UserValidationException("Email or password are incorrect.");
    }
}
