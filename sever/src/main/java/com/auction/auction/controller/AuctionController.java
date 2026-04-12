package com.auction.auction.controller;

import com.auction.auction.model.Auction;
import com.auction.auction.model.AutoBid;
import com.auction.auction.service.AuctionService;
import com.auction.user.model.User;
import com.auction.user.model.Seller;
import com.auction.item.model.Item;

import java.util.List;

public class AuctionController {

    private AuctionService auctionService = new AuctionService();

//tạo auction
    public Auction createAuction(Item item, Seller seller, long durationMillis) {
        return auctionService.createAuction(item, seller, durationMillis);
    }

// đặt giá
    public boolean placeBid(Auction auction, User user, double amount) {
        return auctionService.placeBid(auction, user, amount);
    }

//lấy danh sách
    public List<Auction> getAllAuctions() {
        return auctionService.getAllAuctions();
    }

    //  tìm auction
    public Auction getAuctionById(int id) {
        return auctionService.getAuctionById(id);
    }

 //  kết thúc
    public void endAuction(Auction auction) {
        auctionService.endAuction(auction);
    }
    
    public void registerAutoBid(Auction auction, AutoBid autoBid) {
    auctionService.registerAutoBid(auction, autoBid);
    }
}