package com.auction.auction.controller;

import com.auction.auction.model.Auction;
import com.auction.auction.service.AuctionService;
import com.auction.exception.AutionException.AuctionNotFoundException;
import com.auction.exception.AutionException.InvalidAuctionDataException;
import com.auction.item.model.Product.Item;
import com.auction.user.model.Seller;

import java.util.List;

public class AuctionController {

    private AuctionService auctionService = new AuctionService();

//tạo auction
    public Auction createAuction(Item item, Seller seller, long durationMillis)
            throws InvalidAuctionDataException {
        return auctionService.createAuction(item, seller, durationMillis);
    }

//lấy danh sách
    public List<Auction> getAllAuctions() {
        return auctionService.getAllAuctions();
    }

    //  tìm auction
    public Auction getAuctionById(String id) throws AuctionNotFoundException {
        return auctionService.getAuctionById(id);
    }

 //  kết thúc
    public void endAuction(Auction auction) {
        auctionService.endAuction(auction);
    }

    public void deleteAuction(String auctionId) throws AuctionNotFoundException {
        auctionService.deleteAuction(auctionId);
    }

    public void deleteRunningAuctionsBySeller(String sellerId) {
        auctionService.deleteRunningAuctionsBySeller(sellerId);
    }
}