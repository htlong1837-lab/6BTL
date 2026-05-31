package com.auction.bid.service;
import com.auction.auction.dao.AuctionDAO;
import com.auction.auction.model.Auction;
import com.auction.bid.dao.BidDAO;
import com.auction.bid.model.BidTransaction;
import com.auction.exception.AutionException.AuctionClosedException;
import com.auction.exception.AutionException.BidTooLowException;
import com.auction.user.model.Bidder;

import java.util.concurrent.locks.ReentrantLock;

public class BidService {

    private final AuctionDAO auctionDAO;
    private final BidDAO bidDAO;
    private final BidLockManager lockManager;

    public BidService(AuctionDAO auctionDAO, BidDAO bidDAO, BidLockManager lockManager) {
        this.auctionDAO  = auctionDAO;
        this.bidDAO      = bidDAO;
        this.lockManager = lockManager;
    }

    public String deposit(Bidder bidder, double amount) {
        if (amount <= 0) return "Số tiền nạp phải lớn hơn 0.";
        bidder.setBalance(bidder.getBalance() + amount);
        return "Nạp tiền thành công! Số dư: " + bidder.getBalance() + " VND";
    }

    public String placeBid(Bidder bidder, String auctionId, double bidAmount) {
        ReentrantLock lock = lockManager.getLock(auctionId);
        lock.lock();
        try {
            if (bidAmount <= 0)
                return "Số tiền đặt giá phải lớn hơn 0.";
            if (!bidder.hasSufficientBalance(bidAmount))
                return "Số dư không đủ. Hiện có: " + bidder.getBalance() + " VND";

            Auction auction = auctionDAO.findById(auctionId);
            if (auction == null)
                return "Phiên đấu giá không tồn tại.";

            try {
                auction.placeBid(bidder, bidAmount);
            } catch (AuctionClosedException | BidTooLowException e) {
                return e.getMessage();
            }

            bidDAO.save(new BidTransaction(
                bidder.getId(),
                bidder.getName(),
                auctionId,
                bidAmount,
                System.currentTimeMillis()
            ));
            auctionDAO.save(auction);

            return "Đặt giá thành công! " + bidAmount + " VND";
        } finally {
            lock.unlock();
        }
    }

}