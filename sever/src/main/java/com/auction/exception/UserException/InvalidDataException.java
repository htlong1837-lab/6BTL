package com.auction.exception.UserException;
// Dữ liệu nhập vào sai
public class InvalidDataException extends UserException{
    public InvalidDataException(String message){
        super(message);
    }
    
}
