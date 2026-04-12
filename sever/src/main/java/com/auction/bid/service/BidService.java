package com.auction.bid.service;
//placeBid (synchronized), validate

import com.auction.user.model.Bidder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class BidService {

    // Kho lưu trữ theo userId
    // Bidder đang tham gia phiên nào
    private Map<String, List<String>> activeBids   = new HashMap<>();
    // Các phiên đã thắng
    private Map<String, List<String>> wonAuctions  = new HashMap<>();
    // Lịch sử các lần đặt giá
    private Map<String, List<String>> bidHistories = new HashMap<>();

    // Lấy list theo userId, tạo mới nếu chưa có
    private List<String> getList(Map<String, List<String>> map, String userId) {
        return map.computeIfAbsent(userId, k -> new ArrayList<>());
    }

    // Nạp tiền vào tài khoản
    public String deposit(Bidder bidder, double amount) {
        if (amount <= 0) return "Số tiền nạp phải lớn hơn 0.";

        bidder.setBalance(bidder.getBalance() + amount);
        return "Nạp tiền thành công! Số dư: " + bidder.getBalance() + " VND";
    }

    // Đặt giá vào 1 phiên
    public String placeBid(Bidder bidder, String auctionId, double bidAmount) {
        if (bidAmount <= 0)
            return "Số tiền đặt giá phải lớn hơn 0.";
        if (!bidder.hasSufficientBalance(bidAmount))
            return "Số dư không đủ. Hiện có: " + bidder.getBalance() + " VND";

        String record = "AuctionID: " + auctionId + " | Bid: " + bidAmount;
        getList(bidHistories, bidder.getId()).add(record);

        List<String> active = getList(activeBids, bidder.getId());
        if (!active.contains(auctionId)) active.add(auctionId);

        return "Đặt giá thành công! " + record;
    }

    // Rút khỏi phiên đấu giá
    public String withdrawBid(Bidder bidder, String auctionId) {
        List<String> active = getList(activeBids, bidder.getId());
        if (!active.contains(auctionId))
            return "Bạn không tham gia phiên: " + auctionId;

        active.remove(auctionId);
        return "Đã rút khỏi phiên: " + auctionId;
    }

    // Ghi nhận thắng đấu giá
    public String receiveWin(Bidder bidder, String auctionId) {
        getList(wonAuctions, bidder.getId()).add(auctionId);
        getList(activeBids,  bidder.getId()).remove(auctionId);
        return "Chúc mừng " + bidder.getName() + " thắng phiên: " + auctionId;
    }

    // Xem các phiên đang tham gia
    public String viewActiveBids(Bidder bidder) {
        List<String> active = getList(activeBids, bidder.getId());
        if (active.isEmpty()) return "Bạn chưa tham gia phiên nào.";

        StringBuilder sb = new StringBuilder("=== Phiên đang tham gia ===\n");
        active.forEach(a -> sb.append("  • ").append(a).append("\n"));
        return sb.toString();
    }

    // Xem lịch sử đặt giá
    public String viewBidHistory(Bidder bidder) {
        List<String> history = getList(bidHistories, bidder.getId());
        if (history.isEmpty()) return "Chưa có lịch sử đấu giá.";

        StringBuilder sb = new StringBuilder("=== Lịch sử đấu giá ===\n");
        history.forEach(r -> sb.append("  • ").append(r).append("\n"));
        return sb.toString();
    }
}

