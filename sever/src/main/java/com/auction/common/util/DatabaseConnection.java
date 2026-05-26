package com.auction.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Lớp này quản lý kết nối SQLite, đảm bảo chỉ có 1 instance duy nhất (singleton) và tự động tạo bảng nếu chưa có
public class DatabaseConnection {
    // URL kết nối SQLite (tạo file auction.db trong thư mục hiện tại)
    //Sqlite là một thư viện nhúng, không cần server riêng, dữ liệu được lưu trong file .db
    private static final String DB_URL = "jdbc:sqlite:auction.db";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            // Dùng DriverManager để kết nối, SQLite sẽ tự tạo file nếu chưa tồn tại
            connection = DriverManager.getConnection(DB_URL);
            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA journal_mode=WAL"); // nhiều gười đọc 1 ng ghi
                st.execute("PRAGMA busy_timeout=5000");
            }
            initSchema();// tạo bảng
            System.out.println("[DB] Kết nối SQLite thành công.");
        } catch (SQLException e) {
            System.err.println("[DB] Lỗi kết nối: " + e.getMessage());
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) instance = new DatabaseConnection();
        return instance;
    }

    // Lấy kết nối để thực hiện truy vấn
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
            // Tạo bảng users, items, auctions, bids nếu chưa tồn tại
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
            // Tạo tài khoản admin mặc định nếu chưa có (id: admin-default, username: admin, password: Admin123)
            stmt.executeUpdate(
                "INSERT OR IGNORE INTO users(id,username,password_hash,balance,failed_attempts,is_banned,role)" +
                " VALUES('admin-default','admin','Admin123',0,0,0,'ADMIN')"
            );
        }
    }
    // Kiểm tra xem bảng có cột nhất định không (dùng để phát hiện schema lỗi cũ)
    private boolean tableHasColumn(String table, String column) {
        try (ResultSet rs = connection.createStatement()
                .executeQuery("PRAGMA table_info(" + table + ")")) {
            while (rs.next()) {
                if (column.equalsIgnoreCase(rs.getString("name"))) return true;
            }
        } catch (SQLException e) {
            System.err.println("[DB] Lỗi khi kiểm tra cột bảng: " + e.getMessage());
        }
        return false;
    }
}
