package com.auction.user.service;

import java.util.List;

import com.auction.auction.model.Auction;
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
        target.setBanned(banned);
        // [THÊM] Cập nhật trạng thái bị khóa vào database sau khi thay đổi
        userDAO.update(target);
        System.out.println("[Admin] Tài khoản " + target.getName() + (banned ? " đã bị khóa." : " đã được mở khóa."));
    }

     // Xóa một phiên đấu giá khỏi danh sách */
    public void removeAuction(List<Auction> auctions, Auction target) {
        if (auctions.remove(target)) {
            System.out.println("[Admin] Auction for \"" + target.getItem().getName() + "\" has been removed.");
        } else {
            System.out.println("[Admin] Auction not found.");
        }
    }
 
    // Mở lại tài khoản người dùng
    public void isActive(User user) {
        user.setBanned(false);
        System.out.println("[Admin] User \"" + user.getName() + "\" has been activated. You can now access");
    }
}
