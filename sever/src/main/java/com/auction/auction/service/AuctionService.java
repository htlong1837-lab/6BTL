package com.auction.auction.service;

import com.auction.auction.dao.AuctionDAO;
import com.auction.auction.dao.AuctionDAOImpl;
import com.auction.auction.model.*;
import com.auction.item.model.Product.Item;
import com.auction.user.model.Seller;
import com.auction.user.model.User;

import java.util.List;

public class AuctionService {

    private AuctionDAO auctionDAO = new AuctionDAOImpl();
    private AuctionScheduler scheduler = new AuctionScheduler();
 //tạo auction
    public Auction createAuction(Item item, Seller seller, long durationMillis) {

        Auction auction = new Auction(item, seller, durationMillis);

        auction.start();
        auctionDAO.save(auction);

        scheduler.scheduleAuctionEnd(auction);

        return auction;
    }

// đặt giá
    public boolean placeBid(Auction auction, User bidder, double amount) {
        return auction.placeBid(bidder, amount);
    }

    //  lấy danh sách
    public List<Auction> getAllAuctions() {
        return auctionDAO.findAll();
    }

    //  tìm auction
    public Auction getAuctionById(String id) {
        return auctionDAO.findById(id);
    }

    //  kết thúc
    public void endAuction(Auction auction) {
        auction.endAuction();
    }

    //  xóa
    public void deleteAuction(Auction auction) {
        auctionDAO.delete(auction);
    }

    public void registerAutoBid(Auction auction, AutoBid autoBid) {
    auction.registerAutoBid(autoBid);
    }
}