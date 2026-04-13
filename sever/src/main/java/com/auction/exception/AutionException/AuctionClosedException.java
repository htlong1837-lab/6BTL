package com.auction.exception.AutionException;

// ném bidder đặt giá vào phiên đã đóng
public class AuctionClosedException extends AuctionException {
    public AuctionClosedException(String message) {
        super(message);
    }
    
}
