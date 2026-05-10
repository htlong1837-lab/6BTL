package com.auction.user.service;
//signUp, login, ban, validate

import com.auction.user.dao.UserDAO;
import com.auction.user.model.Bidder;
import com.auction.user.model.Seller;
import com.auction.user.model.User;
import com.auction.common.util.PasswordUtil;
import com.auction.exception.UserException.*;

import java.util.UUID;

import javax.naming.AuthenticationException;

public class UserService {

    private UserDAO userDAO; // nhận từ bên ngoài vào

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    //=====================================================
    //                     ĐĂNG KÝ
    //=====================================================

    public String signUp(String username,
                         String password, String confirmPassword, String role) throws UserException, AuthenticationException {


        //========================ID===========================
        String id = UUID.randomUUID().toString();

        //========================USERNAME===========================

        if (username == null || username.isBlank())
            throw new InvalidDataException("Tên người dùng không được để trống.") ;

        if (userDAO.existsByUsername(username))
            throw new DuplicateDataException("Tên người dùng đã tồn tại.") ;

        //========================PASSWORD===========================

        if (!PasswordUtil.isStrongPassword(password))
            throw new InvalidDataException("Mật khẩu phải có ít nhất 8 ký tự (phải bao gồm chữ in hoa,chữ in thường và số).");

        if (!password.equals(confirmPassword))
            throw new PasswordAuthenticationException("Mật khẩu xác nhận không khớp.");

        //========================ROLE===========================

        if (role == null || role.isBlank())
            throw new InvalidDataException("Vai trò không được để trống.");
        String passwordHash = PasswordUtil.hash(password);
        if (role.equals("Bidder")) {
            User newUser = new Bidder(id, username, passwordHash, role);
            userDAO.save(newUser);
        } else if (role.equals("Seller")){
            User newUser = new Seller(id, username, passwordHash, role);
            userDAO.save(newUser);
        }

        return "Đăng ký thành công!";
    }

    //=====================================================
    //                     ĐĂNG NHẬP
    //=====================================================
    public User login(String username, String password) throws UserException {

        User user = userDAO.findByUsername(username);

        if (user == null)
            throw new UserNotFoundException("Tài khoản không tồn tại.");

        // [SỬA] Throw exception khi tài khoản bị khóa thay vì chỉ println rồi vẫn tiếp tục
        if (user.isBanned())
            throw new UserNotFoundException("Tài khoản đã bị khóa do đăng nhập sai quá nhiều lần.");

        String inputHash = PasswordUtil.hash(password);

        if (!user.getPasswordHash().equals(inputHash)) {
            // [SỬA] Throw exception khi sai mật khẩu - trước đây code trả về user luôn, không báo lỗi
            throw new PasswordAuthenticationException("Sai mật khẩu ");
        }
        return user;
    }

}