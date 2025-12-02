package org.lab.services;

import java.util.List;

import org.lab.entities.User;

public interface UserService {
    User register(String username);

    List<User> listUsers();
}
