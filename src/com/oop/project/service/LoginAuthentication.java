package com.oop.project.service;


import com.oop.project.repository.UserRepository;
import com.oop.project.model.User;
import com.oop.project.exception.AuthenticationException;

/*  
 * Bussiness logic for Login action
 */

public class LoginAuthentication {
    
    public static User getUser(String username, String password, String role) throws Exception {
        try {
            User userLogin = new UserRepository(username, password, role).login();
            
            // User is found (not null)
            if (userLogin != null) {
                return userLogin;
                
            // User is not found (null)
            } else {
                throw new AuthenticationException("User not found! Wrong username or password.");
            }
            
        } catch (Exception e) {
            throw e;
        }
    }
}
