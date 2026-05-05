package com.auction.common.until;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:auction.db";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            initSchema();
            System.out.println("[DB] Kết nối SQLite thành công.");
        } catch (SQLException e) {
            System.err.println("[DB] Lỗi kết nối: " + e.getMessage());
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) instance = new DatabaseConnection();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "  id TEXT PRIMARY KEY, username TEXT NOT NULL UNIQUE," +
                "  email TEXT NOT NULL UNIQUE, password_hash TEXT NOT NULL," +
                "  balance REAL DEFAULT 0.0, failed_attempts INTEGER DEFAULT 0," +
                "  is_banned INTEGER DEFAULT 0, role TEXT DEFAULT 'BIDDER', shop_name TEXT)"
            );
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS items (" +
                "  id TEXT PRIMARY KEY, name TEXT NOT NULL, description TEXT," +
                "  start_price REAL NOT NULL, category TEXT, seller_id TEXT NOT NULL," +
                "  item_type TEXT NOT NULL, artist TEXT, medium TEXT," +
                "  make TEXT, model TEXT, year INTEGER, brand TEXT, warranty_months INTEGER)"
            );
        }
    }
}
