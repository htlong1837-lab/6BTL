package main.java.com.auction.auction.dao;

import main.java.com.auction.auction.model.Auction;

import java.util.ArrayList;
import java.util.List;

public class AuctionDAOImpl implements AuctionDAO {

    private List<Auction> auctions = new ArrayList<>();

    @Override
    public void save(Auction auction) {
        auctions.add(auction);
    }

    @Override
    public List<Auction> findAll() {
        return auctions;
    }

    @Override
    public Auction findById(int index) {
        if (index >= 0 && index < auctions.size()) {
            return auctions.get(index);
        }
        return null;
    }

    @Override
    public void delete(Auction auction) {
        auctions.remove(auction);
    }
}