package com.auction.auction.dao;

import com.auction.auction.model.Auction;
import com.auction.auction.model.AuctionStatus;
import com.auction.common.util.DatabaseConnection;
import com.auction.bid.dao.BidDAO;
import com.auction.bid.dao.BidDAOSQLiteImpl;
import com.auction.bid.model.BidTransaction;
import com.auction.item.dao.ItemDAO;
import com.auction.item.dao.ItemDAOSQLiteImpl;
import com.auction.item.model.Product.Item;
import com.auction.user.dao.UserDAO;
import com.auction.user.dao.UserDAOSQLiteImpl;
import com.auction.user.model.Seller;
import com.auction.user.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AuctionSQLiteDAOImpl implements AuctionDAO {

    private final ItemDAO itemDAO;
    private final UserDAO userDAO;
    private final BidDAO bidDAO;

    public AuctionSQLiteDAOImpl() {
        this.itemDAO = new ItemDAOSQLiteImpl();
        this.userDAO = new UserDAOSQLiteImpl();
        this.bidDAO  = new BidDAOSQLiteImpl();
    }

    public AuctionSQLiteDAOImpl(ItemDAO itemDAO, UserDAO userDAO) {
        this.itemDAO = itemDAO;
        this.userDAO = userDAO;
        this.bidDAO  = new BidDAOSQLiteImpl();
    }

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Auction auction) {
        if (auction == null) return;
        String sql =
            "INSERT OR REPLACE INTO auctions" +
            "(id, item_id, seller_id, current_price, highest_bidder_id, status, start_time, end_time)" +
            " VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, auction.getId());
            ps.setString(2, auction.getItem().getId());
            ps.setString(3, auction.getSeller().getId());
            ps.setDouble(4, auction.getCurrentPrice());
            User hb = auction.getHighestBidder();
            if (hb != null) ps.setString(5, hb.getId());
            else            ps.setNull(5, Types.VARCHAR);
            ps.setString(6, auction.getStatus().name());
            ps.setLong(7,   auction.getStartTime());
            ps.setLong(8,   auction.getEndTime());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[AuctionDAO] save lỗi: " + e.getMessage());
        }
    }

    @Override
    public List<Auction> findAll() {
        List<Auction> result = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM auctions")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Auction a = mapRow(rs);
                if (a != null) result.add(a);
            }
        } catch (SQLException e) {
            System.err.println("[AuctionDAO] findAll lỗi: " + e.getMessage());
        }
        return result;
    }

    @Override
    public Auction findById(String id) {
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM auctions WHERE id = ?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[AuctionDAO] findById lỗi: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void delete(Auction auction) {
        if (auction == null) return;
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM auctions WHERE id = ?")) {
            ps.setString(1, auction.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[AuctionDAO] delete lỗi: " + e.getMessage());
        }
    }

    private Auction mapRow(ResultSet rs) throws SQLException {
        String id       = rs.getString("id");
        Item item       = itemDAO.findById(rs.getString("item_id"));
        User seller     = userDAO.findById(rs.getString("seller_id"));

        if (item == null || !(seller instanceof Seller)) {
            System.err.println("[AuctionDAO] mapRow: thiếu item hoặc seller cho auction " + id);
            return null;
        }

        double currentPrice   = rs.getDouble("current_price");
        String hbId           = rs.getString("highest_bidder_id");
        User highestBidder    = (hbId != null && !hbId.isEmpty()) ? userDAO.findById(hbId) : null;
        AuctionStatus status  = AuctionStatus.valueOf(rs.getString("status"));
        long startTime        = rs.getLong("start_time");
        long endTime          = rs.getLong("end_time");

        Auction auction = new Auction(id, item, (Seller) seller, currentPrice,
                           highestBidder, status, startTime, endTime);
        List<BidTransaction> bids = bidDAO.findByAuctionId(id);
        bids.forEach(b -> auction.getBidHistory().add(b));
        return auction;
    }
}
