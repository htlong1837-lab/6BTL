package main.java.com.auction.auction.dao;

import main.java.com.auction.auction.model.Auction;
import java.util.List;

public interface AuctionDAO {
     
    void save(Auction auction);
    List<Auction> findAll();

    Auction findById(int index);
    void delete(Auction auction);
    
}
