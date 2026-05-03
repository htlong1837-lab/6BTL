package com.auction.user.controller;

// [SỬA] Dùng UserDAOSQLiteImpl thay vì UserDAOImpl để dữ liệu được lưu vào DB thực sự
import com.auction.user.dao.UserDAOSQLiteImpl;
import com.auction.user.model.User;
import com.auction.user.service.UserService;
import com.auction.exception.UserException.UserException;

public class UserController {

    // [SỬA] Dùng SQLite DAO thay vì in-memory DAO - dữ liệu user tồn tại sau khi server restart
    private final UserService userService = new UserService(new UserDAOSQLiteImpl());

    // [SỬA] Sửa params: trước đây nhận User object nhưng User không có raw password
    // RequestRouter sẽ extract các field này từ JSON payload của request
    public boolean createAccount(String id, String username, String email,
                                  String password, String confirmPassword) {
        try {
            userService.signUp(id, username, email, password, confirmPassword);
            return true;
        } catch (Exception e) {
            System.out.println("[UserController] Lỗi đăng ký: " + e.getMessage());
            return false;
        }
    }

    // [SỬA] Trả về User object thay vì boolean để client nhận được thông tin user sau khi login
    // Trả về null khi đăng nhập thất bại (exception được bắt và log)
    public User loginAccount(String username, String password) {
        try {
            return userService.login(username, password);
        } catch (UserException e) {
            System.out.println("[UserController] Lỗi đăng nhập: " + e.getMessage());
            return null;
        }
    }

    // [THÊM] Chuyển đổi quyền Bidder → Seller khi user muốn bán hàng
    public boolean registerAsSeller(String username, String shopName) {
        try {
            userService.registerAsSeller(username, shopName);
            return true;
        } catch (UserException e) {
            System.out.println("[UserController] Lỗi đăng ký Seller: " + e.getMessage());
            return false;
        }
    }
}
