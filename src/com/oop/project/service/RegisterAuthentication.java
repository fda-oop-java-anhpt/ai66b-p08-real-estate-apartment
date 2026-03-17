package com.oop.project.service;

import com.oop.project.repository.UserRepository;
/*
 * Bussiness logic for register action
 */
public class RegisterAuthentication {
    
    public static void createUser(String username, String password, String role) throws Exception {
        
        try {
            new UserRepository(username, password, role).register();
        
        } catch (Exception e) {
            throw e;
        }
    }
}
