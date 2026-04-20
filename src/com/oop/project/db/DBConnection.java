package com.oop.project.db;

import java.sql.*;
import java.util.Map;
import com.oop.project.util.ReadEnv;

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
    private static final Map<String,String> envMap = new ReadEnv("/com/oop/project/config/Connection.env").load();
    
    private static final String HOST = envMap.getOrDefault("HOST", "localhost");
    private static final String PORT = envMap.getOrDefault("PORT", "3306");
    private static final String SCHEMA = envMap.getOrDefault("SCHEMA", "");
    
    private static final String URL = String.format("jdbc:mysql://%s:%s/%s", HOST, PORT, SCHEMA);
    private static final String USERNAME = envMap.getOrDefault("USERNAME", "root");
    private static final String PASSWORD = envMap.getOrDefault("PASSWORD", "");
    
    
    public DBConnection() {}
    
    // This method is to establish a connection.
    public Connection establish() throws SQLException {
        // return a connection if valid, else SQLException Error.
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
    
    public static void setCurrentUser(Connection conn, String username) throws SQLException {
        if (username != null && !username.isEmpty()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET @current_username = '" + username.replace("'", "''") + "'");
            }
        }
    }
}
