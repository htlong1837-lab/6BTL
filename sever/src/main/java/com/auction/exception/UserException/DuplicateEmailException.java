package com.auction.exception.UserException;

// gmail đăng kí tồn tại
public class DuplicateEmailException extends UserException {
    public DuplicateEmailException(String message) {
        super(message);
    }
    
}

