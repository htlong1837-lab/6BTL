package com.auction.user.service;
//signUp, login, ban, validate

import com.auction.user.dao.UserDAO;
import com.auction.user.model.Bidder;
import com.auction.user.model.User;
import com.auction.common.until.PasswordUtil;
import com.auction.exception.UserException.*;

import javax.naming.AuthenticationException;

public class UserService {

    private UserDAO userDAO; // nhận từ bên ngoài vào

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    //=====================================================
    //                     ĐĂNG KÝ
    //=====================================================

    public String signUp(String id,String username, String email,
                         String password, String confirmPassword) throws UserException, AuthenticationException {


        //========================ID===========================
        if(id == null || id.isBlank())
            throw new InvalidDataException("ID không được để trống.") ;
        if(userDAO.existsById(id))
            throw new DuplicateDataException("ID đã tồn tại.");


        //========================USERNAME===========================

        if (username == null || username.isBlank())
            throw new InvalidDataException("Tên người dùng không được để trống.") ;

        if (userDAO.existsByUsername(username))
            throw new DuplicateDataException("Tên người dùng đã tồn tại.") ;

        //========================EMAIL===========================

        if (email == null || !email.contains("@"))
            throw new InvalidDataException("Email không hợp lệ.") ;

        if (userDAO.existsByEmail(email))
            throw new DuplicateDataException("Email đã tồn tại.");

        //========================PASSWORD===========================

        if (!PasswordUtil.isStrongPassword(password))
            throw new InvalidDataException("Mật khẩu phải có ít nhất 8 ký tự (phải bao gồm chữ in hoa,chữ in thường và số).");

        if (!password.equals(confirmPassword))
            throw new PasswordAuthenticationException("Mật khẩu xác nhận không khớp.");

        // Hash mật khẩu trước khi lưu vào database để tăng cường bảo mật
        String passwordHash = PasswordUtil.hashPassword(password);
        

        // Tạo user mới và lưu vào "database"
        User newUser        = new Bidder(id, username, email, passwordHash);
        userDAO.save(newUser);
        return "Đăng ký thành công!";
    }

    //=====================================================
    //                     ĐĂNG NHẬP
    //=====================================================
    public String login(String username, String password) throws UserException {

        User user = userDAO.findByUsername(username);

        if (user == null)
            throw new UserNotFoundException("Tài khoản không tồn tại.") ;
        if(user.isBanned() == true)
            System.out.println("Tài khoản đã bị khóa.");
        
        // Hash mật khẩu nhập vào để so sánh với hash đã lưu trong database
        String inputHash = PasswordUtil.hashPassword(password);

        if (!user.getPasswordHash().equals(inputHash))
            throw new PasswordAuthenticationException("Sai mật khẩu.");
            user.incrementFailedLoginAttempts();

        if(user.getFailedLoginAttempts() >= 5) {
            user.setBanned(true);
            System.out.println("Tài khoản đã bị khóa do quá nhiều lần đăng nhập thất bại.");
        }

        return "Đăng nhập thành công!";
    }

}