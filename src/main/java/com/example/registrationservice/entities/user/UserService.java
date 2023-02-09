package com.example.registrationservice.entities.user;


import com.example.registrationservice.util.custom.exceptions.UserAlreadyExistsException;
import com.example.registrationservice.util.custom.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserModel add(UserModel userModel) throws UserAlreadyExistsException;

    Optional<UserModel> update(UserModel userModel) throws UserNotFoundException;

    Optional<UserModel> get(Long id);

    UserModel get(String email) throws UserNotFoundException;

    List<UserModel> get();

    UserModel getUserWithAuthtoken(String authToken) throws UserNotFoundException;

    void delete(Long id) throws UserNotFoundException;
}
