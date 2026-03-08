package com.oop.project.db;

import java.sql.*;

/*
* This is used to establish a connection to MySQL database.
* Required Connector/J v9.5.0+ (known as Java Database Connector or JDBC).
*/
public class DBConnection {
    /*
    * Developers should modify these parameters to match the local connection.
    * Connection URL composition: jdbc:mysql://Hostname:Port/your_schema
    * Left click the connection in MySQL, then choose 'Edit connection' to see your parameters.
    */
    
    private final String url = "jdbc:mysql://Hostname:Port/your_schema";
    private final String username = "root";
    private final String password = "";
    
    public DBConnection() {}
    
    // This method is to establish a connection.
    public Connection establish() throws SQLException {
        // return a connection if valid, else SQLException Error.
        return DriverManager.getConnection(url, username, password);
        }
}
