package com.auction.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:auction.db";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA journal_mode=WAL");
                st.execute("PRAGMA busy_timeout=5000");
            }
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
            // Nếu bảng users cũ có cột email (schema lỗi), xóa và tạo lại
            if (tableHasColumn("users", "email")) {
                System.out.println("[DB] Phát hiện schema cũ (có cột email), đang tạo lại bảng users...");
                stmt.executeUpdate("DROP TABLE IF EXISTS users");
            }
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "  id TEXT PRIMARY KEY, username TEXT NOT NULL UNIQUE," +
                "  password_hash TEXT NOT NULL," +
                "  balance REAL DEFAULT 0.0, failed_attempts INTEGER DEFAULT 0," +
                "  is_banned INTEGER DEFAULT 0, role TEXT DEFAULT 'BIDDER', shop_name TEXT)"
            );
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS items (" +
                "  id TEXT PRIMARY KEY, name TEXT NOT NULL, description TEXT," +
                "  start_price REAL NOT NULL, category TEXT, seller_id TEXT NOT NULL," +
                "  item_type TEXT NOT NULL, artist TEXT, medium TEXT," +
                "  make TEXT, model TEXT, year INTEGER, brand TEXT, warranty_months INTEGER," +
                "  approved INTEGER DEFAULT 0)"
            );
            // Migration: thêm cột approved cho DB cũ chưa có
            if (!tableHasColumn("items", "approved")) {
                stmt.executeUpdate("ALTER TABLE items ADD COLUMN approved INTEGER DEFAULT 0");
                System.out.println("[DB] Đã thêm cột approved vào bảng items.");
            }
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS auctions (" +
                "  id TEXT PRIMARY KEY, item_id TEXT NOT NULL, seller_id TEXT NOT NULL," +
                "  current_price REAL NOT NULL, highest_bidder_id TEXT," +
                "  status TEXT NOT NULL, start_time INTEGER NOT NULL, end_time INTEGER NOT NULL)"
            );
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS bids (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  bidder_id TEXT NOT NULL, bidder_name TEXT NOT NULL," +
                "  auction_id TEXT NOT NULL, amount REAL NOT NULL, timestamp INTEGER NOT NULL)"
            );
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS auctions (" +
                "  id TEXT PRIMARY KEY, item_id TEXT NOT NULL, seller_id TEXT NOT NULL," +
                "  current_price REAL NOT NULL, highest_bidder_id TEXT," +
                "  status TEXT NOT NULL, start_time INTEGER NOT NULL, end_time INTEGER NOT NULL)"
            );
        }
    }

    private boolean tableHasColumn(String table, String column) {
        try (ResultSet rs = connection.createStatement()
                .executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                if (column.equalsIgnoreCase(rs.getString("name"))) return true;
            }
        } catch (SQLException e) {
        }
        return false;
    }
}
