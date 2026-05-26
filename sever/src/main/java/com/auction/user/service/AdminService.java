package com.auction.user.service;

import com.auction.exception.UserException.UserException;
import com.auction.exception.UserException.UserNotFoundException;
import com.auction.user.dao.UserDAO;
import com.auction.user.model.Admin;
import com.auction.user.model.User;

public class AdminService {

    private final Admin admin;
    private final UserDAO userDAO;

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
        if (target instanceof Admin) {
            throw new UserNotFoundException("Không có quyền ban Admin");
        }
        target.setBanned(banned);
        // [THÊM] Cập nhật trạng thái bị khóa vào database sau khi thay đổi
        userDAO.update(target);
        System.out.println("[Admin] Tài khoản " + target.getName() + (banned ? " đã bị khóa." : " đã được mở khóa."));
    }

}
