package com.auction.exception;
// gmail đăng kí tồn tại
public class DuplicateEmailException extends AuctionException {
    public DuplicateEmailException(String message) {
        super(message);
    }
    
}

