package com.auction.exception.UserException;
// tên bị trùng
public class DuplicateUsernameException extends UserException {
    public DuplicateUsernameException (String message){
        super(message);
    }
    
}
