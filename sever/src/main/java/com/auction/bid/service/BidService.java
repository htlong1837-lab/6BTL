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

public class BidService {

    private final AuctionDAO auctionDAO;
    private final BidDAO bidDAO;

    private Map<String, List<String>> activeBids  = new HashMap<>();
    private Map<String, List<String>> wonAuctions = new HashMap<>();
    private Map<String, List<String>> bidHistory  = new HashMap<>();

    public BidService(AuctionDAO auctionDAO, BidDAO bidDAO) {
        this.auctionDAO = auctionDAO;
        this.bidDAO     = bidDAO;
    }

    private List<String> getList(Map<String, List<String>> map, String userId) {
        return map.computeIfAbsent(userId, k -> new ArrayList<>());
    }

    public String deposit(Bidder bidder, double amount) {
        if (amount <= 0) return "So tien nap phai lon hon 0.";
        bidder.setBalance(bidder.getBalance() + amount);
        return "Nap tien thanh cong! So du: " + bidder.getBalance() + " VND";
    }

    public String placeBid(Bidder bidder, String auctionId, double bidAmount) {
        if (bidAmount <= 0)
            return "So tien dat gia phai lon hon 0.";
        if (!bidder.hasSufficientBalance(bidAmount))
            return "So du khong du. Hien co: " + bidder.getBalance() + " VND";

        Auction auction = auctionDAO.findById(auctionId);
        if (auction == null)
            return "Phien dau gia khong ton tai.";

        boolean success = auction.placeBid(bidder, bidAmount);
        if (!success)
            return "Dat gia that bai. Gia phai cao hon gia hien tai hoac phien da dong.";

        bidDAO.save(new BidTransaction(
            bidder.getId(),
            bidder.getName(),
            auctionId,
            bidAmount,
            System.currentTimeMillis()
        ));

        List<String> active = getList(activeBids, bidder.getId());
        if (!active.contains(auctionId)) active.add(auctionId);

        return "Dat gia thanh cong! " + bidAmount + " VND";
    }

    public String withdrawBid(Bidder bidder, String auctionId) {
        List<String> active = getList(activeBids, bidder.getId());
        if (!active.contains(auctionId))
            return "Ban khong tham gia phien: " + auctionId;
        active.remove(auctionId);
        return "Da rut khoi phien: " + auctionId;
    }

    public String receiveWin(Bidder bidder, String auctionId) {
        getList(wonAuctions, bidder.getId()).add(auctionId);
        getList(activeBids,  bidder.getId()).remove(auctionId);
        return "Chuc mung " + bidder.getName() + " thang phien: " + auctionId;
    }

    public String viewActiveBids(Bidder bidder) {
        List<String> active = getList(activeBids, bidder.getId());
        if (active.isEmpty()) return "Ban chua tham gia phien nao.";
        StringBuilder sb = new StringBuilder("Phien dang tham gia\n");
        active.forEach(a -> sb.append("  | ").append(a).append("\n"));
        return sb.toString();
    }

    public String viewBidHistory(Bidder bidder) {
        List<String> history = getList(bidHistory, bidder.getId());
        if (history.isEmpty()) return "Chua co lich su dau gia.";
        StringBuilder sb = new StringBuilder("Lich su dau gia\n");
        history.forEach(r -> sb.append("  | ").append(r).append("\n"));
        return sb.toString();
    }
}