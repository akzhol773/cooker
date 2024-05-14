package org.example.cookercorner.services;

import org.example.cookercorner.entities.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findUserByEmail(String email);
}
