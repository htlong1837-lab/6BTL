package com.auction.bid.dao;

import com.auction.bid.model.BidTransaction;
import com.auction.common.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BidDAOSQLiteImpl implements BidDAO {
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(BidTransaction tx) {
        if (tx == null) return;
        String sql = "INSERT INTO bids(bidder_id, bidder_name, auction_id, amount, timestamp) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, tx.getBidderId());
            ps.setString(2, tx.getBidderName());
            ps.setString(3, tx.getAuctionId());
            ps.setDouble(4, tx.getAmount());
            ps.setLong(5, tx.getTimestamp());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[BidDAO] save lỗi: " + e.getMessage());
        }
    }

    @Override
    public List<BidTransaction> findAll() {
        return query("SELECT * FROM bids", null);
    }

    @Override
    public List<BidTransaction> findByAuctionId(String auctionId) {
        return query("SELECT * FROM bids WHERE auction_id = ?", auctionId);
    }

    @Override
    public List<BidTransaction> findByBidderId(String bidderId) {
        return query("SELECT * FROM bids WHERE bidder_id = ?", bidderId);
    }

    @Override
    public BidTransaction findHighestBidByAuction(String auctionId) {
        String sql = "SELECT * FROM bids WHERE auction_id = ? ORDER BY amount DESC LIMIT 1";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, auctionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[BidDAO] findHighest lỗi: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteByAuctionId(String auctionId) {
        String sql = "DELETE FROM bids WHERE auction_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, auctionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[BidDAO] delete lỗi: " + e.getMessage());
        }
    }

    private List<BidTransaction> query(String sql, String param) {
        List<BidTransaction> result = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            if (param != null) ps.setString(1, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BidDAO] query lỗi: " + e.getMessage());
        }
        return result;
    }

    private BidTransaction mapRow(ResultSet rs) throws SQLException {
        return new BidTransaction(
            rs.getString("bidder_id"),
            rs.getString("bidder_name"),
            rs.getString("auction_id"),
            rs.getDouble("amount"),
            rs.getLong("timestamp")
        );
    }
}

