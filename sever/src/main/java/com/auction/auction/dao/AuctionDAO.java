package com.auction.auction.dao;

import com.auction.auction.model.Auction;
import java.util.List;

public interface AuctionDAO {
     
    void save(Auction auction); // lưu trữ 
    List<Auction> findAll();
    Auction findById(String id);
    void delete(Auction auction);
    
}
