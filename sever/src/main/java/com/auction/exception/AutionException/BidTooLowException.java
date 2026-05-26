package com.auction.exception.AutionException;

// Ném khi bidder đặt giá thấp hơn hoặc bằng giá hiện tại
public class BidTooLowException extends AuctionException {
    public BidTooLowException(String message) {
        super(message);
    }
}
