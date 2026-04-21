package com.auction.exception.UserException;
// tên, id, email bị trùng
public class DuplicateDataException extends UserException {
    public DuplicateDataException (String message){
        super(message);
    }
    
}
