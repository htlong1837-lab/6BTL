package com.auction.auction.service;

import com.auction.auction.dao.AuctionDAO;
import com.auction.auction.dao.AuctionSQLiteDAOImpl;
import com.auction.auction.model.*;
import com.auction.bid.dao.BidDAO;
import com.auction.bid.dao.BidDAOSQLiteImpl;
import com.auction.bid.model.BidTransaction;
import com.auction.exception.AutionException.AuctionNotFoundException;
import com.auction.exception.AutionException.InvalidAuctionDataException;
import com.auction.item.model.Product.Item;
import com.auction.user.dao.UserDAO;
import com.auction.user.dao.UserDAOSQLiteImpl;
import com.auction.user.model.Seller;
import com.auction.user.model.User;

import java.util.List;

public class AuctionService {

    private final AuctionDAO auctionDAO = new AuctionSQLiteDAOImpl();
    private final UserDAO    userDAO    = new UserDAOSQLiteImpl();
    private final BidDAO     bidDAO     = new BidDAOSQLiteImpl();
    private final AuctionScheduler scheduler = new AuctionScheduler(auctionDAO, userDAO, bidDAO);

    public AuctionService() {
        restoreRunningAuctions();
    }

    private void restoreRunningAuctions() {
        for (Auction a : auctionDAO.findAll()) {
            if (a.getStatus() == AuctionStatus.RUNNING) {
                if (System.currentTimeMillis() >= a.getEndTime()) {
                    // Nếu auction đã hết giờ nhưng chưa settle (server restart),
                    // dùng bids table để lấy winner chính xác thay vì dựa vào in-memory object
                    enrichHighestBidder(a);
                    a.endAuction();
                    scheduler.settle(a);
                    auctionDAO.updateStatus(a.getId(), AuctionStatus.FINISHED);
                } else {
                    scheduler.scheduleAuctionEnd(a);
                }
            }
        }
    }

    // Nạp lại highest bidder từ bảng bids nếu auction object có null (do stale read)
    private void enrichHighestBidder(Auction a) {
        if (a.getHighestBidder() != null) return;
        BidTransaction highest = bidDAO.findHighestBidByAuction(a.getId());
        if (highest == null) return;
        User winner = userDAO.findById(highest.getBidderId());
        if (winner != null) {
            // Cập nhật in-memory để settle() dùng đúng dữ liệu
            a.setHighestBidderAndPrice(winner, highest.getAmount());
        }
    }

    public Auction createAuction(Item item, Seller seller, long durationMillis)
            throws InvalidAuctionDataException {
        if (item == null)   throw new InvalidAuctionDataException("Sản phẩm không hợp lệ.");
        if (seller == null) throw new InvalidAuctionDataException("Người bán không hợp lệ.");
        if (durationMillis <= 0) throw new InvalidAuctionDataException("Thời gian phải lớn hơn 0.");

        Auction auction = new Auction(item, seller, durationMillis);
        auction.start();
        auctionDAO.save(auction);
        scheduler.scheduleAuctionEnd(auction);
        return auction;
    }

    public List<Auction> getAllAuctions() {
        return auctionDAO.findAll();
    }

    public Auction getAuctionById(String id) throws AuctionNotFoundException {
        Auction auction = auctionDAO.findById(id);
        if (auction == null) throw new AuctionNotFoundException("Không tìm thấy phiên đấu giá: " + id);
        return auction;
    }

    public void endAuction(Auction auction) {
        auction.endAuction();
    }

    public void deleteAuction(String auctionId) throws AuctionNotFoundException {
        Auction auction = auctionDAO.findById(auctionId);
        if (auction == null) throw new AuctionNotFoundException("Không tìm thấy phiên đấu giá: " + auctionId);
        // Đặt FINISHED trước để AuctionScheduler tự cancel trên tick tiếp theo
        auctionDAO.updateStatus(auctionId, AuctionStatus.FINISHED);
        bidDAO.deleteByAuctionId(auctionId);
        auctionDAO.delete(auction);
    }

    // Hủy tất cả phiên OPEN/RUNNING của seller khi bị ban
    public int cancelAuctionsBySeller(String sellerId) {
        int count = 0;
        for (Auction auction : auctionDAO.findAll()) {
            if (auction.getSeller().getId().equals(sellerId)
                    && (auction.getStatus() == AuctionStatus.OPEN
                        || auction.getStatus() == AuctionStatus.RUNNING)) {
                auctionDAO.updateStatus(auction.getId(), AuctionStatus.CANCELLED);
                count++;
                System.out.println("[Ban] Hủy phiên đấu giá: " + auction.getId()
                        + " (" + auction.getItem().getName() + ")");
            }
        }
        return count;
    }

}
