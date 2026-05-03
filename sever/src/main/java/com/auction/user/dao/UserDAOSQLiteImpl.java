package com.auction.user.dao;

// [THÊM] Toàn bộ file này là mới - UserDAO dùng SQLite thay vì in-memory HashMap
// Cần thiết để dữ liệu user tồn tại sau khi server khởi động lại

import com.auction.common.until.DatabaseConnection;
import com.auction.user.model.Admin;
import com.auction.user.model.Bidder;
import com.auction.user.model.Seller;
import com.auction.user.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOSQLiteImpl implements UserDAO {

    // [THÊM] Lấy connection từ Singleton DatabaseConnection thay vì tự tạo
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(User user) {
        String sql =
            "INSERT INTO users(id, username, email, password_hash, balance, failed_attempts, is_banned, role, shop_name)" +
            " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setDouble(5, user.getBalance());
            ps.setInt(6, user.getFailedLoginAttempts());
            ps.setInt(7, user.isBanned() ? 1 : 0);
            ps.setString(8, roleOf(user));
            // [THÊM] shop_name chỉ Seller mới có, các loại khác lưu NULL
            ps.setString(9, user instanceof Seller ? ((Seller) user).getShopName() : null);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[UserDAO] save lỗi: " + e.getMessage());
        }
    }

    @Override
    public boolean existsById(String id) {
        return countWhere("SELECT COUNT(*) FROM users WHERE id = ?", id) > 0;
    }

    @Override
    public boolean existsByUsername(String username) {
        return countWhere("SELECT COUNT(*) FROM users WHERE username = ?", username) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return countWhere("SELECT COUNT(*) FROM users WHERE email = ?", email) > 0;
    }

    @Override
    public User findByUsername(String username) {
        return queryOne("SELECT * FROM users WHERE username = ?", username);
    }

    @Override
    public User findById(String id) {
        return queryOne("SELECT * FROM users WHERE id = ?", id);
    }

    @Override
    public void update(User user) {
        String sql =
            "UPDATE users SET email=?, password_hash=?, balance=?, failed_attempts=?," +
            " is_banned=?, role=?, shop_name=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPasswordHash());
            ps.setDouble(3, user.getBalance());
            ps.setInt(4, user.getFailedLoginAttempts());
            ps.setInt(5, user.isBanned() ? 1 : 0);
            ps.setString(6, roleOf(user));
            ps.setString(7, user instanceof Seller ? ((Seller) user).getShopName() : null);
            ps.setString(8, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[UserDAO] update lỗi: " + e.getMessage());
        }
    }

    // [THÊM] Helper đếm rows để kiểm tra tồn tại - tái sử dụng cho 3 exists methods
    private int countWhere(String sql, String param) {
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, param);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[UserDAO] count lỗi: " + e.getMessage());
        }
        return 0;
    }

    // [THÊM] Helper lấy 1 row và chuyển thành User object
    private User queryOne(String sql, String param) {
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, param);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[UserDAO] query lỗi: " + e.getMessage());
        }
        return null;
    }

    // [THÊM] Chuyển ResultSet row thành đúng lớp con User (Bidder/Seller/Admin)
    // Dựa vào cột role để quyết định tạo lớp nào
    private User mapRow(ResultSet rs) throws SQLException {
        String id        = rs.getString("id");
        String username  = rs.getString("username");
        String email     = rs.getString("email");
        String hash      = rs.getString("password_hash");
        double balance   = rs.getDouble("balance");
        int    failed    = rs.getInt("failed_attempts");
        boolean banned   = rs.getInt("is_banned") == 1;
        String role      = rs.getString("role");
        String shopName  = rs.getString("shop_name");

        User user;
        if ("SELLER".equals(role)) {
            Seller s = new Seller(id, username, email, hash);
            if (shopName != null) s.setShopName(shopName);
            user = s;
        } else if ("ADMIN".equals(role)) {
            user = new Admin(id, username, email, hash);
        } else {
            user = new Bidder(id, username, email, hash);
        }

        // [THÊM] Khôi phục trạng thái balance, failedAttempts, banned từ DB
        // Cần thiết vì constructor User luôn set balance=0, failed=0, banned=false
        user.setBalance(balance);
        user.setFailedLoginAttempts(failed);
        user.setBanned(banned);
        return user;
    }

    // [THÊM] Map lớp Java → giá trị chuỗi lưu trong cột role của SQLite
    private String roleOf(User user) {
        if (user instanceof Admin)  return "ADMIN";
        if (user instanceof Seller) return "SELLER";
        return "BIDDER";
    }
}
