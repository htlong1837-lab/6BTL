package com.auction.auction.dao;

import com.auction.auction.model.Auction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuctionDAOImpl implements AuctionDAO {

    private final Map<String, Auction> store = new HashMap<>();

    @Override
    public void save(Auction auction) {
        store.put(auction.getId(), auction);
    }

    @Override
    public List<Auction> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Auction findById(String id) {
        return store.get(id);
    }

    @Override
    public void delete(Auction auction) {
        store.remove(auction.getId());
    }
}
   