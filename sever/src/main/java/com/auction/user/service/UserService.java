package com.auction.user.service;
//signUp, login, ban, validate

import com.auction.user.dao.UserDAO;
import com.auction.user.model.Bidder;
import com.auction.user.model.User;
import com.auction.common.until.PasswordUtil;
import com.auction.exception.UserException.DuplicateEmailException;
import com.auction.exception.UserException.DuplicateUsernameException;
import com.auction.exception.UserException.InvalidDataException;
import com.auction.exception.UserException.PasswordAuthenticationException;
import com.auction.exception.UserException.UserException;
import com.auction.exception.UserException.UserNotFoundException;

import java.util.UUID;

import javax.naming.AuthenticationException;

public class UserService {

    private UserDAO userDAO; // nhận từ bên ngoài vào

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public String signUp(String username, String email,
                         String password, String confirmPassword) throws UserException, AuthenticationException {

        if (username == null || username.isBlank())
            throw new InvalidDataException("Tên người dùng không được để trống.") ;

        if (userDAO.findByUsername(username) != null)
            throw new DuplicateUsernameException("Tên người dùng đã tồn tại.") ;

        if (email == null || !email.contains("@"))
            throw new InvalidDataException("Email không hợp lệ.") ;

        if (userDAO.existsByEmail(email))
            throw new DuplicateEmailException("Email đã tồn tại.");

        if (!PasswordUtil.isStrongPassword(password))
            throw new InvalidDataException("Mật khẩu phải có ít nhất 8 ký tự (phải bao gồm chữ in hoa,chữ in thường và số).");

        if (!password.equals(confirmPassword))
            throw new PasswordAuthenticationException("Mật khẩu xác nhận không khớp.");

        String id           = UUID.randomUUID().toString();
        String passwordHash = PasswordUtil.hashPassword(password);
        User newUser        = new Bidder(id, username, email, passwordHash);

        userDAO.save(newUser);
        return "Đăng ký thành công!";
    }

    public String login(String username, String password) throws UserException {

        User user = userDAO.findByUsername(username);

        if (user == null)
            throw new UserNotFoundException("Tài khoản không tồn tại.") ;

        String inputHash = PasswordUtil.hashPassword(password);

        if (!user.getPasswordHash().equals(inputHash))
            throw new PasswordAuthenticationException("Sai mật khẩu.");

        return "Đăng nhập thành công!";
    }
   
}