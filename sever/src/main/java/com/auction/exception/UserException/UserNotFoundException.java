package com.auction.exception;
// không thấy user ( 1 loạt mấy cái lq đến user)
public class UserNotFoundException extends AuctionException {
    public UserNotFoundException(String message) {
        super(message);
    }
    
}
