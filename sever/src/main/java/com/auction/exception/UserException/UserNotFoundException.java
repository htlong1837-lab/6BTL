package com.auction.exception.UserException;

// không thấy user ( 1 loạt mấy cái lq đến user)
public class UserNotFoundException extends UserException {
    public UserNotFoundException(String message) {
        super(message);
    }
    
}
