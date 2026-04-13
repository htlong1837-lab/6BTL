package com.auction.exception.SystemException;
//base bắt lỗi hệ thống
public class SystemException extends Exception{
    public SystemException(String message){
        super(message);
    }
}