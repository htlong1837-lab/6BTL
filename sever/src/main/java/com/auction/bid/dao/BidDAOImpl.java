package com.auction.bid.dao;

import com.auction.bid.model.BidTransaction;

import java.util.ArrayList;
import java.util.List;

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
        List<BidTransaction> result = new ArrayList<>();
        if (auctionId == null) return result;
        for (BidTransaction tx : store) {
            if(auctionId.equals(tx.getAuctionId())){
                result.add(tx);
            }
        }
        return result;
    }
    public List<BidTransaction> findByBidderId(String bidderId) {
        List<BidTransaction> result = new ArrayList<>();
        if (bidderId == null) return result;
        for (BidTransaction tx : store) {
            if (bidderId.equals(tx.getBidderId())) {
                result.add(tx);
            }
        }
        return result;
    }

    public BidTransaction findHighestBidByAuction(String auctionId) {
        if (auctionId == null) return null;
        BidTransaction highest = null;
        for (BidTransaction tx : store) {
            if (auctionId.equals(tx.getAuctionId())) {
                if (highest == null || tx.getAmount() > highest.getAmount()) {
                    highest = tx;
                }
            }
        }
        return highest;
    }

    public void deleteByAuctionId(String auctionId) {
        if (auctionId == null) return;
        for (int i = store.size() - 1; i >= 0; i--) {
            if (auctionId.equals(store.get(i).getAuctionId())) {
                store.remove(i);
            }
        }
    }
}
