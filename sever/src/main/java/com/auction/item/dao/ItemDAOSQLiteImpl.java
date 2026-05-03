package com.auction.item.dao;

// [THÊM] Toàn bộ file này là mới - ItemDAO dùng SQLite thay vì in-memory HashMap
// Cần thiết để dữ liệu sản phẩm tồn tại sau khi server khởi động lại

import com.auction.common.until.DatabaseConnection;
import com.auction.item.model.Product.Art;
import com.auction.item.model.Product.Electronics;
import com.auction.item.model.Product.Item;
import com.auction.item.model.Product.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAOSQLiteImpl implements ItemDAO {

    // [THÊM] Lấy connection từ Singleton DatabaseConnection
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Item item) {
        String sql =
            "INSERT INTO items(id, name, description, start_price, category, seller_id, item_type," +
            " artist, medium, make, model, year, brand, warranty_months)" +
            " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            fillBaseFields(ps, item);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ItemDAO] save lỗi: " + e.getMessage());
        }
    }

    @Override
    public Item findById(String id) {
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM items WHERE id = ?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[ItemDAO] findById lỗi: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Item> findAll() {
        List<Item> result = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM items")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ItemDAO] findAll lỗi: " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<Item> findBySellerId(String sellerId) {
        List<Item> result = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM items WHERE seller_id = ?")) {
            ps.setString(1, sellerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ItemDAO] findBySellerId lỗi: " + e.getMessage());
        }
        return result;
    }

    @Override
    public void update(Item item) {
        String sql =
            "UPDATE items SET name=?, description=?, start_price=?, category=?, seller_id=?, item_type=?," +
            " artist=?, medium=?, make=?, model=?, year=?, brand=?, warranty_months=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            // [THÊM] Dùng lại logic fill fields, chỉ thay đổi thứ tự param cuối cùng là id
            ps.setString(1,  item.getName());
            ps.setString(2,  item.getDes());
            ps.setDouble(3,  item.getStartPrice());
            ps.setString(4,  item.getCategory());
            ps.setString(5,  item.getSellerId());
            ps.setString(6,  typeOf(item));
            setTypeFields(ps, item, 7);
            ps.setString(14, item.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ItemDAO] update lỗi: " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM items WHERE id = ?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ItemDAO] delete lỗi: " + e.getMessage());
        }
    }

    // [THÊM] Chuyển ResultSet row thành đúng lớp con Item (Art/Vehicle/Electronics)
    // Dựa vào cột item_type để quyết định tạo lớp nào
    private Item mapRow(ResultSet rs) throws SQLException {
        String id         = rs.getString("id");
        String name       = rs.getString("name");
        String des        = rs.getString("description");
        double startPrice = rs.getDouble("start_price");
        String category   = rs.getString("category");
        String sellerId   = rs.getString("seller_id");
        String itemType   = rs.getString("item_type");

        switch (itemType) {
            case "ART":
                return new Art(id, name, des, startPrice, category, sellerId,
                    nvl(rs.getString("artist")),
                    nvl(rs.getString("medium")));
            case "VEHICLE":
                return new Vehicle(id, name, des, startPrice, category, sellerId,
                    nvl(rs.getString("make")),
                    nvl(rs.getString("model")),
                    rs.getInt("year"));
            case "ELECTRONICS":
                return new Electronics(id, name, des, startPrice, category, sellerId,
                    nvl(rs.getString("brand")),
                    rs.getInt("warranty_months"));
            default:
                // [THÊM] Fallback nếu gặp item_type không xác định - không nên xảy ra
                return new Art(id, name, des, startPrice, category, sellerId, "", "");
        }
    }

    // [THÊM] Helper fill 14 params cho INSERT - dùng lại cho cả save()
    private void fillBaseFields(PreparedStatement ps, Item item) throws SQLException {
        ps.setString(1,  item.getId());
        ps.setString(2,  item.getName());
        ps.setString(3,  item.getDes());
        ps.setDouble(4,  item.getStartPrice());
        ps.setString(5,  item.getCategory());
        ps.setString(6,  item.getSellerId());
        ps.setString(7,  typeOf(item));
        setTypeFields(ps, item, 8);
    }

    // [THÊM] Fill các cột đặc thù theo từng loại Item (Art/Vehicle/Electronics)
    // offset là vị trí param bắt đầu điền (khác nhau giữa INSERT và UPDATE)
    private void setTypeFields(PreparedStatement ps, Item item, int offset) throws SQLException {
        if (item instanceof Art) {
            Art a = (Art) item;
            ps.setString(offset,     a.getArtist());
            ps.setString(offset + 1, a.getMedium());
            ps.setNull(offset + 2, java.sql.Types.VARCHAR);
            ps.setNull(offset + 3, java.sql.Types.VARCHAR);
            ps.setNull(offset + 4, java.sql.Types.INTEGER);
            ps.setNull(offset + 5, java.sql.Types.VARCHAR);
            ps.setNull(offset + 6, java.sql.Types.INTEGER);
        } else if (item instanceof Vehicle) {
            Vehicle v = (Vehicle) item;
            ps.setNull(offset,     java.sql.Types.VARCHAR);
            ps.setNull(offset + 1, java.sql.Types.VARCHAR);
            ps.setString(offset + 2, v.getMake());
            ps.setString(offset + 3, v.getModel());
            ps.setInt(offset + 4, v.getYear());
            ps.setNull(offset + 5, java.sql.Types.VARCHAR);
            ps.setNull(offset + 6, java.sql.Types.INTEGER);
        } else if (item instanceof Electronics) {
            Electronics e = (Electronics) item;
            ps.setNull(offset,     java.sql.Types.VARCHAR);
            ps.setNull(offset + 1, java.sql.Types.VARCHAR);
            ps.setNull(offset + 2, java.sql.Types.VARCHAR);
            ps.setNull(offset + 3, java.sql.Types.VARCHAR);
            ps.setNull(offset + 4, java.sql.Types.INTEGER);
            ps.setString(offset + 5, e.getBrand());
            ps.setInt(offset + 6, e.getWarrantyMonths());
        }
    }

    // [THÊM] Map lớp Java → giá trị chuỗi lưu trong cột item_type của SQLite
    private String typeOf(Item item) {
        if (item instanceof Art)         return "ART";
        if (item instanceof Vehicle)     return "VEHICLE";
        if (item instanceof Electronics) return "ELECTRONICS";
        return "UNKNOWN";
    }

    // [THÊM] null-safe: trả về chuỗi rỗng thay vì null để tránh NullPointerException trong constructor
    private String nvl(String s) {
        return s != null ? s : "";
    }
}
