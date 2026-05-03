package com.auction.common.until;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    // [THÊM] Đường dẫn file SQLite - tạo tại thư mục chạy server
    private static final String DB_URL = "jdbc:sqlite:auction.db";

    private static DatabaseConnection instance;
    private Connection connection;

    // [THÊM] Mở kết nối thực sự tới SQLite và khởi tạo schema khi tạo instance
    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            initSchema();
            System.out.println("[DB] Kết nối SQLite thành công: " + DB_URL);
        } catch (SQLException e) {
            System.err.println("[DB] Lỗi kết nối SQLite: " + e.getMessage());
        }
    }

    // [THÊM] Double-checked locking để thread-safe khi nhiều ClientHandler gọi đồng thời
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    // [THÊM] Tạo bảng users và items khi khởi động server lần đầu
    // Dùng CREATE TABLE IF NOT EXISTS nên gọi nhiều lần không bị lỗi
    private void initSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            // Bảng users: lưu tất cả loại user (Bidder/Seller/Admin)
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "  id               TEXT    PRIMARY KEY," +
                "  username         TEXT    NOT NULL UNIQUE," +
                "  email            TEXT    NOT NULL UNIQUE," +
                "  password_hash    TEXT    NOT NULL," +
                "  balance          REAL    DEFAULT 0.0," +
                "  failed_attempts  INTEGER DEFAULT 0," +
                "  is_banned        INTEGER DEFAULT 0," +   // SQLite không có BOOLEAN, dùng 0/1
                "  role             TEXT    DEFAULT 'BIDDER'," + // BIDDER | SELLER | ADMIN
                "  shop_name        TEXT"                        // chỉ Seller mới có giá trị
                + ")"
            );

            // Bảng items: lưu tất cả loại sản phẩm (Art/Vehicle/Electronics) trong 1 bảng
            // Các cột đặc thù của từng loại để NULL nếu loại khác không dùng
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS items (" +
                "  id               TEXT    PRIMARY KEY," +
                "  name             TEXT    NOT NULL," +
                "  description      TEXT," +
                "  start_price      REAL    NOT NULL," +
                "  category         TEXT," +
                "  seller_id        TEXT    NOT NULL," +
                "  item_type        TEXT    NOT NULL," +   // ART | VEHICLE | ELECTRONICS
                "  artist           TEXT," +               // chỉ Art
                "  medium           TEXT," +               // chỉ Art
                "  make             TEXT," +               // chỉ Vehicle
                "  model            TEXT," +               // chỉ Vehicle
                "  year             INTEGER," +            // chỉ Vehicle
                "  brand            TEXT," +               // chỉ Electronics
                "  warranty_months  INTEGER"               // chỉ Electronics
                + ")"
            );
        }
    }
}
