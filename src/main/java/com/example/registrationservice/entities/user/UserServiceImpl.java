package com.example.registrationservice.entities.user;

import com.example.registrationservice.util.custom.exceptions.UserAlreadyExistsException;
import com.example.registrationservice.util.custom.exceptions.UserNotFoundException;
import com.example.registrationservice.util.custom.security.PasswordManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private Map<String, UserModel> usersDatabase = new TreeMap<>();

    List<Long> unusedIds = new ArrayList<>();

    private final PasswordManager passwordManager;

    @Autowired
    public UserServiceImpl(PasswordManager passwordManager) {
        this.passwordManager = passwordManager;
    }

    @Override
    public UserModel add(UserModel userModel) throws UserAlreadyExistsException {
        if (findUserInDbByEmail(userModel.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("The email address is already in use!");
        } else {
            String newId;
            if (unusedIds.isEmpty()) {
                newId = String.valueOf(usersDatabase.size() + 1);
            } else {
                newId = String.valueOf(unusedIds.get(unusedIds.size() - 1));
                unusedIds.remove(unusedIds.size() - 1);
            }

            userModel.setId(newId);
            userModel.setPassword(passwordManager.encrypt(userModel.getPassword()));
            usersDatabase.put(newId, userModel);
        }
        return userModel;
    }

    @Override
    public Optional<UserModel> update(UserModel userModel) throws UserNotFoundException {
        Optional<String> dbUser = findUserInDbByEmail(userModel.getEmail());
        if (dbUser.isPresent()) {
            userModel.setId(dbUser.get());
            if (userModel.getAuthToken() == null) {
                userModel.setPassword(passwordManager.encrypt(userModel.getPassword()));
            }
            usersDatabase.put(dbUser.get(), userModel);
            return Optional.of(usersDatabase.get(dbUser.get()));
        } else {
            throw new UserNotFoundException("User does not exist!");
        }
    }

    @Override
    public Optional<UserModel> get(Long id) {
        return Optional.of(usersDatabase.get(String.valueOf(id)));
    }

    @Override
    public UserModel get(String email) throws UserNotFoundException {
        Optional<String> userOptionalId = findUserInDbByEmail(email);
        if (userOptionalId.isPresent()) {
            return usersDatabase.get(userOptionalId.get());
        } else {
            throw new UserNotFoundException("No users registered with this email address have been found.");
        }
    }

    @Override
    public UserModel getUserWithAuthtoken(String authToken) throws UserNotFoundException {
        Optional<String> userOptionalId = findUserInDbByAuth(authToken);
        if (userOptionalId.isPresent()) {
            UserModel userModel = usersDatabase.get(userOptionalId.get());
            userModel.setPassword(passwordManager.decrypt(userModel.getPassword()));
            return userModel;
        } else {
            throw new UserNotFoundException("This authentication token is no longer valid. Please relog.");
        }
    }

    @Override
    public List<UserModel> get() {
        return new ArrayList<>(usersDatabase.values());
    }

    @Override
    public void delete(Long id) throws UserNotFoundException {
        if (findUserInDbById(id).isPresent()) {
            usersDatabase.remove(String.valueOf(id));
            unusedIds.add(id);
        } else {
            throw new UserNotFoundException();
        }
    }

    private Optional<String> findUserInDbByEmail(String email) {
        for (Map.Entry entry : usersDatabase.entrySet()) {
            if (((UserModel) entry.getValue()).getEmail().equals(email)) {
                return Optional.of(((UserModel) entry.getValue()).getId());
            }
        }
        return Optional.empty();
    }

    private Optional<String> findUserInDbById(Long id) {
        for (Map.Entry entry : usersDatabase.entrySet()) {
            if (((UserModel) entry.getValue()).getId().equals(String.valueOf(id))) {
                return Optional.of(String.valueOf(id));
            }
        }
        return Optional.empty();
    }

    private Optional<String> findUserInDbByAuth(String authToken) {
        for (Map.Entry entry : usersDatabase.entrySet()) {
            if (authToken != null && authToken.equals(((UserModel) entry.getValue()).getAuthToken())) {
                return Optional.of(String.valueOf(((UserModel) entry.getValue()).getId()));
            }
        }
        return Optional.empty();
    }
}
