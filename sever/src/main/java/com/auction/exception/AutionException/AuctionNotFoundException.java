package com.auction.exception.AutionException;

// Ném khi không tìm thấy phiên đấu giá theo ID
public class AuctionNotFoundException extends AuctionException {
    public AuctionNotFoundException(String message) {
        super(message);
    }
}
