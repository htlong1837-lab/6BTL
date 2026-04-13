package com.auction.exception.AutionException;
//base bắt lỗi chung trong phiên đấu giá

public class AuctionException extends Exception {
    public AuctionException(String message) {
        super(message);
    }
    
}
