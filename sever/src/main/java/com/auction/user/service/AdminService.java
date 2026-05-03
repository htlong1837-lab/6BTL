package com.auction.user.service;

import com.auction.exception.UserException.UserException;
import com.auction.exception.UserException.UserNotFoundException;
import com.auction.user.dao.UserDAO;
import com.auction.user.model.Admin;
import com.auction.user.model.User;

public class AdminService {

    private final Admin admin;
    private final UserDAO userDAO;

    // [SỬA] Thêm constructor nhận Admin và UserDAO - trước đây admin field = null nên permission check luôn fail
    public AdminService(Admin admin, UserDAO userDAO) {
        this.admin   = admin;
        this.userDAO = userDAO;
    }

    // Khóa hoặc mở khóa tài khoản user
    public void banUser(User target, boolean banned) throws UserException {
        // [SỬA] Kiểm tra admin != null thay vì instanceof (instanceof luôn false khi null)
        if (admin == null) {
            throw new UserNotFoundException("Chỉ Admin mới có quyền khóa tài khoản.");
        }
        target.setBanned(banned);
        // [THÊM] Cập nhật trạng thái bị khóa vào database sau khi thay đổi
        userDAO.update(target);
        System.out.println("[Admin] Tài khoản " + target.getName() + (banned ? " đã bị khóa." : " đã được mở khóa."));
    }

    // [THÊM] Lấy thông tin user theo username - Admin cần xem danh sách user
    public User getUser(String username) throws UserException {
        User user = userDAO.findByUsername(username);
        if (user == null)
            throw new UserNotFoundException("Không tìm thấy tài khoản: " + username);
        return user;
    }
}
