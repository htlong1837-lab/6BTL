package com.auction.auction.dao;

import com.auction.auction.model.Auction;
import java.util.List;

import com.auction.auction.model.AuctionStatus;

public interface AuctionDAO {

    void save(Auction auction);
    void updateStatus(String id, AuctionStatus status);
    List<Auction> findAll();
    Auction findById(String id);
    List<Auction> findBySellerId(String sellerId);
    void delete(Auction auction);

}
