package com.auction.exception.AutionException;

// giá thấp hơn hiện tại
public class BidTooLowException extends AuctionException {
    public BidTooLowException(String message) {
        super(message);
    }
    
}