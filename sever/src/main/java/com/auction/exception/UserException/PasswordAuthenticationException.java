package com.auction.exception.UserException;
//Sai xác thực mật khẩu 
public class PasswordAuthenticationException extends UserException {
    public PasswordAuthenticationException(String message){
        super(message);
    }
    
}
