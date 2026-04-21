package com.auction.common.until;
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private DatabaseConnection() {}
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
            
        }
        return instance;
    }
    public Connection getConnection() {
        return connection;
    }
}

