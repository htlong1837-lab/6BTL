package com.auction.exception;
// ko đủ lúa
public class InsufficientBalanceException extends AuctionException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
    
}
