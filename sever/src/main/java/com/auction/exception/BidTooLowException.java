package com.auction.exception;
// giá thấp hơn hiện tại
public class BidTooLowException extends AuctionException {
    public BidTooLowException(String message) {
        super(message);
    }
    
}