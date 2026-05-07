package com.auction.user.dao;

import com.auction.common.util.DatabaseConnection;
import com.auction.user.model.Admin;
import com.auction.user.model.Bidder;
import com.auction.user.model.Seller;
import com.auction.user.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAOSQLiteImpl implements UserDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(User user) {
        String sql =
            "INSERT INTO users(id, username, password_hash, balance, failed_attempts, is_banned, role, shop_name)" +
            " VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPasswordHash());
            ps.setDouble(4, user.getBalance());
            ps.setInt(5, 0);
            ps.setInt(6, user.isBanned() ? 1 : 0);
            ps.setString(7, roleOf(user));
            ps.setString(8, user instanceof Seller ? ((Seller) user).getShopName() : null);
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
    public User findByUsername(String username) {
        return queryOne("SELECT * FROM users WHERE username = ?", username);
    }
    @Override
    public User findById(String id) {
        return queryOne("SELECT * FROM users WHERE id = ?", id);
    }
    @Override
    public List<User> findAll() {
        List<User> result = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM users")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] findAll lỗi: " + e.getMessage());
        }
        return result;
    }

    @Override
    public void update(User user) {
        String sql =
            "UPDATE users SET password_hash=?, balance=?, failed_attempts=?," +
            " is_banned=?, role=?, shop_name=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, user.getPasswordHash());
            ps.setDouble(2, user.getBalance());
            ps.setInt(3, 0);
            ps.setInt(4, user.isBanned() ? 1 : 0);
            ps.setString(5, roleOf(user));
            ps.setString(6, user instanceof Seller ? ((Seller) user).getShopName() : null);
            ps.setString(7, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[UserDAO] update lỗi: " + e.getMessage());
        }
    }
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
    private User mapRow(ResultSet rs) throws SQLException {
        String id        = rs.getString("id");
        String username  = rs.getString("username");
        String hash      = rs.getString("password_hash");
        double balance   = rs.getDouble("balance");
        boolean banned   = rs.getInt("is_banned") == 1;
        String role      = rs.getString("role");
        String shopName  = rs.getString("shop_name");
        User user;
        if ("SELLER".equals(role)) {
            Seller s = new Seller(id, username, hash, "SELLER");
            if (shopName != null) s.setShopName(shopName);
            user = s;
        } else if ("ADMIN".equals(role)) {
            user = new Admin(id, username, hash);
        } else {
            user = new Bidder(id, username, hash, "BIDDER");
        }

        user.setBalance(balance);
        user.setBanned(banned);
        return user;
    }
    private String roleOf(User user) {
        if (user instanceof Admin)  return "ADMIN";
        if (user instanceof Seller) return "SELLER";
        return "BIDDER";
    }
}
