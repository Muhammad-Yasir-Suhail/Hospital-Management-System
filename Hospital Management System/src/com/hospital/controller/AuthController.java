package com.hospital.controller;

import com.hospital.dao.UserDAO;
import com.hospital.model.User;
import com.hospital.utils.Validator;

public class AuthController {
    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password) throws Exception {
        if (!Validator.isNotEmpty(username) || !Validator.isNotEmpty(password)) throw new IllegalArgumentException("Username and password are required.");
        return userDAO.login(username, password);
    }
}
