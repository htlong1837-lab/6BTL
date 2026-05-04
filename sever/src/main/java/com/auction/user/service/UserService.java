package com.auction.user.service;
//signUp, login, ban, validate

import com.auction.user.dao.UserDAO;
import com.auction.user.model.Bidder;
import com.auction.user.model.Seller;
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

        if (email == null || !email.endsWith("@gmail.com")              // email phải kết thúc bằng "@gmail.com"
            || email.indexOf("@") == 0                                     // email không được bắt đầu bằng "@" 
            || email.contains("..")                                          // email không được chứa ".." liên tiếp
            || email.indexOf("@") != email.lastIndexOf("@")          // email chỉ được chứa 1 ký tự "@"
            || !Character.isLetterOrDigit(email.charAt(0))       // email phải bắt đầu bằng chữ cái hoặc số
            || email.length() > 30 
            || email.length() < 5) {        
            throw new InvalidDataException("Email không hợp lệ.") ;}

        if (userDAO.existsByEmail(email))
            throw new DuplicateDataException("Email đã tồn tại.");

        //========================PASSWORD===========================

        if (!PasswordUtil.isStrongPassword(password))
            throw new InvalidDataException("Mật khẩu phải có ít nhất 8 ký tự (phải bao gồm chữ in hoa,chữ in thường và số).");

        if (!password.equals(confirmPassword))
            throw new PasswordAuthenticationException("Mật khẩu xác nhận không khớp.");

        // Hash mật khẩu trước khi lưu vào database để tăng cường bảo mật
        String passwordHash = PasswordUtil.hashPassword(password);
        

        // Tạo user mới và lưu vào "database" và mặc định là Bidder.
        User newUser        = new Bidder(id, username, email, passwordHash);
        userDAO.save(newUser);
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

        String inputHash = PasswordUtil.hashPassword(password);

        if (!user.getPasswordHash().equals(inputHash)) {
            user.incrementFailedLoginAttempts();
            userDAO.update(user);
            if (user.getFailedLoginAttempts() >= 5) {
                user.setBanned(true);
                userDAO.update(user);
            }
            // [SỬA] Throw exception khi sai mật khẩu - trước đây code trả về user luôn, không báo lỗi
            throw new PasswordAuthenticationException("Sai mật khẩu. Còn " + (5 - user.getFailedLoginAttempts()) + " lần thử.");
        }

        // [SỬA] Reset số lần thất bại khi đăng nhập thành công - trước đây nằm trong block sai mật khẩu nên không bao giờ chạy
        if (user.getFailedLoginAttempts() > 0) {
            user.resetFailedLoginAttempts();
            userDAO.update(user);
        }

        return user;
    }

   //=====================================================
    //                     REGISTER ROLE
    //=====================================================
    public String registerAsSeller(String name, String shopname) throws UserException {
        User user = userDAO.findByUsername(name);
        if (user == null) {
            throw new UserNotFoundException("Tài khoản không tồn tại!");
        }
        if (user instanceof Bidder) {
            Seller seller = new Seller(user.getId(), user.getName(), user.getEmail(), user.getPasswordHash());
            seller.setShopName(shopname);
            userDAO.update(seller);
        }
        return "Đăng ký Seller thành công!";

    }
}