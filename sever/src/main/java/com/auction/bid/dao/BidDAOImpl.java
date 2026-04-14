package com.auction.bid.dao;

import com.auction.bid.model.BidTransaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory implementation của BidDAO.
 * Lưu trữ bằng List vì BidTransaction không có id riêng —
 * một phiên có thể có nhiều lần đặt giá từ cùng một bidder.
 */
public class BidDAOImpl implements BidDAO {

    private final List<BidTransaction> store = new ArrayList<>();

    public void save(BidTransaction transaction) {
        if (transaction == null) return;
        store.add(transaction);
    }

    @Override
    public List<BidTransaction> findAll() {
        return new ArrayList<>(store);
    }

    public List<BidTransaction> findByAuctionId(String auctionId) {
        if (auctionId == null) return new ArrayList<>();
        return store.stream()
                .filter(tx -> auctionId.equals(tx.getAuctionId()))
                .collect(Collectors.toList());
    }

    public List<BidTransaction> findByBidderId(String bidderId) {
        if (bidderId == null) return new ArrayList<>();
        return store.stream()
                .filter(tx -> bidderId.equals(tx.getBidderId()))
                .collect(Collectors.toList());
    }

    public BidTransaction findHighestBidByAuction(String auctionId) {
        if (auctionId == null) return null;
        return store.stream()
                .filter(tx -> auctionId.equals(tx.getAuctionId()))
                .max(Comparator.comparingDouble(BidTransaction::getAmount))
                .orElse(null);
    }

    public void deleteByAuctionId(String auctionId) {
        if (auctionId == null) return;
        store.removeIf(tx -> auctionId.equals(tx.getAuctionId()));
    }
}
