package com.oop.project.util;

import java.security.MessageDigest;
/*
 * An utility function to hash strings
 */
public class HashingUtil {
    public static String bySHA256(String word) throws Exception {
        /*
        * SHA-256 is a hash function.
        * It hashes original string into hexadecimal format.
        * The hashed string is not invertible and fixed at 64 characters.
        */
        
        // Get SHA-256 hash function
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        
        // Hashing the word into hashed bytes
        byte[] hashedBytes = md.digest(word.getBytes());
        
        // Concatenate and convert hashed bytes to hashed string
        StringBuilder hashedWord = new StringBuilder();
        
        for (byte b : hashedBytes) {
            hashedWord.append(String.format("%02x", b));
        }
        
        // Return hashed word in String
        return hashedWord.toString();
    }
}
