package com.auction.auction.service;

import com.auction.auction.dao.AuctionDAO;
import com.auction.auction.dao.AuctionSQLiteDAOImpl;
import com.auction.auction.model.*;
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
    private final AuctionScheduler scheduler = new AuctionScheduler(auctionDAO, userDAO);

    public AuctionService() {
        restoreRunningAuctions();
    }

    private void restoreRunningAuctions() {
        for (Auction a : auctionDAO.findAll()) {
            if (a.getStatus() == com.auction.auction.model.AuctionStatus.RUNNING) {
                if (System.currentTimeMillis() >= a.getEndTime()) {
                    a.endAuction();
                    scheduler.settle(a);
                    auctionDAO.save(a);
                } else {
                    scheduler.scheduleAuctionEnd(a);
                }
            }
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
    public void placeBid(Auction auction, User bidder, double amount)
            throws com.auction.exception.AutionException.AuctionClosedException,
                   com.auction.exception.AutionException.BidTooLowException {
        auction.placeBid(bidder, amount);
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

}