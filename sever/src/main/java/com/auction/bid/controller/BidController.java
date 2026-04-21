package com.auction.bid.controller;
import com.auction.bid.service.BidService;
import com.auction.user.model.Bidder;
public class BidController {
    private final BidService bidService;
    public BidController(BidService bidService) {
        this.bidService = bidService;
    }
    public String handlePlaceBid(Bidder bidder, String auctionId, double amount) {
        return bidService.placeBid(bidder,auctionId,amount);
    }
    public String handleDeposit(Bidder bidder, double amount) {
        return bidService.deposit(bidder, amount);

    }
    public String handleWithdraw(Bidder bidder, String auctionId) {
        return bidService.withdrawBid(bidder, auctionId);
    }
    public String handleViewHistory(Bidder bidder) {
        return bidService.viewBidHistory(bidder);
    }
    public String handleViewActive(Bidder bidder) {
        return bidService.viewActiveBids(bidder);
    }


    
}
