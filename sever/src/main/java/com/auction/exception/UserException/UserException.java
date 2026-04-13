package com.auction.exception.UserException;
// base bắt lỗi người dùng
public class UserException extends Exception{
    public UserException(String message){
        super(message);
    }
}