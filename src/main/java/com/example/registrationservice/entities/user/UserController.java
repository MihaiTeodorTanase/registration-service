package com.example.registrationservice.entities.user;

import com.example.registrationservice.util.custom.exceptions.UserAlreadyExistsException;
import com.example.registrationservice.util.custom.exceptions.UserAlreadyLoggedIn;
import com.example.registrationservice.util.custom.exceptions.UserNotFoundException;
import com.example.registrationservice.util.custom.exceptions.UserValidationException;
import com.example.registrationservice.util.custom.security.AuthTokenGenerator;
import com.example.registrationservice.util.custom.security.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users/")
public class UserController {

    private UserService userService;
    private UserValidator userValidator;


    @Autowired
    public UserController(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path="/registration")
    public ResponseEntity register(@RequestBody UserModel userModel) {
        try {
            UserModel addedUserModel = userService.add(userModel);
            return ResponseEntity.ok(addedUserModel);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path="/login")
    public ResponseEntity login(@RequestBody UserModel userModel) throws UserNotFoundException, UserValidationException {
        try {
            UserModel dbUser = userService.get(userModel.getEmail());
            userValidator.validateUser(dbUser,userModel);
            String authToken = AuthTokenGenerator.generateNewToken();
            dbUser.setAuthToken(authToken);
            userService.update(dbUser);
            return ResponseEntity.ok(authToken);
        } catch (UserNotFoundException | UserValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path="/get/auth/{authToken}")
    public ResponseEntity<UserModel> getUserByAuthToken(@PathVariable("authToken") String authToken) throws UserNotFoundException {
        UserModel user = userService.getUserWithAuthtoken(authToken);
        if(user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="/update")
    public ResponseEntity update(@RequestBody UserModel userModel) {
        try {
            Optional<UserModel> updatedUser = userService.update(userModel);
            if(updatedUser.isPresent()) {
                return ResponseEntity.ok(updatedUser.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch(UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path="/get/{id}")
    public ResponseEntity<UserModel> get(@PathVariable("id") Long id) {
        Optional<UserModel> user = userService.get(id);
        if(user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path="/get")
    public ResponseEntity<List<UserModel>> get() {
        List<UserModel> users = userService.get();
        if(users.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(users);
        }
    }

    @DeleteMapping(path="/delete/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            userService.delete(id);
        } catch(UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }
}
