package com.auction.bid.service;

import com.auction.auction.dao.AuctionDAO;
import com.auction.auction.model.Auction;
import com.auction.bid.dao.BidDAO;
import com.auction.bid.model.BidTransaction;
import com.auction.user.model.Bidder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class BidService {

    private final AuctionDAO auctionDAO;
    private final BidDAO bidDAO;
    private final BidLockManager lockManager;

    private Map<String, List<String>> activeBids  = new HashMap<>();
    private Map<String, List<String>> wonAuctions = new HashMap<>();
    private Map<String, List<String>> bidHistory  = new HashMap<>();

    public BidService(AuctionDAO auctionDAO, BidDAO bidDAO, BidLockManager lockManager) {
        this.auctionDAO  = auctionDAO;
        this.bidDAO      = bidDAO;
        this.lockManager = lockManager;
    }

    private List<String> getList(Map<String, List<String>> map, String userId) {
        return map.computeIfAbsent(userId, k -> new ArrayList<>());
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

            boolean success = auction.placeBid(bidder, bidAmount);
            if (!success)
                return "Đặt giá thất bại. Giá phải cao hơn giá hiện tại hoặc phiên đã đóng.";

            bidDAO.save(new BidTransaction(
                bidder.getId(),
                bidder.getName(),
                auctionId,
                bidAmount,
                System.currentTimeMillis()
            ));

            List<String> active = getList(activeBids, bidder.getId());
            if (!active.contains(auctionId)) active.add(auctionId);

            return "Đặt giá thành công! " + bidAmount + " VND";
        } finally {
            lock.unlock();
        }
    }

    public String withdrawBid(Bidder bidder, String auctionId) {
        List<String> active = getList(activeBids, bidder.getId());
        if (!active.contains(auctionId))
            return "Bạn không tham gia phiên: " + auctionId;
        active.remove(auctionId);
        return "Đã rút khỏi phiên: " + auctionId;
    }

    public String receiveWin(Bidder bidder, String auctionId) {
        getList(wonAuctions, bidder.getId()).add(auctionId);
        getList(activeBids,  bidder.getId()).remove(auctionId);
        return "Chúc mừng " + bidder.getName() + " thắng phiên: " + auctionId;
    }

    public String viewActiveBids(Bidder bidder) {
        List<String> active = getList(activeBids, bidder.getId());
        if (active.isEmpty()) return "Bạn chưa tham gia phiên nào.";
        StringBuilder sb = new StringBuilder("Phiên đang tham gia\n");
        active.forEach(a -> sb.append("  | ").append(a).append("\n"));
        return sb.toString();
    }

    public String viewBidHistory(Bidder bidder) {
        List<String> history = getList(bidHistory, bidder.getId());
        if (history.isEmpty()) return "Chưa có lịch sử đấu giá.";
        StringBuilder sb = new StringBuilder("Lịch sử đấu giá\n");
        history.forEach(r -> sb.append("  | ").append(r).append("\n"));
        return sb.toString();
    }
}