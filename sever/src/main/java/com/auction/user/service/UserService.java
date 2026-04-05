package main.java.com.auction.user.service;
//signUp, login, ban, validate

import com.auction.user.dao.UserDAO;
import com.auction.user.model.Bidder;
import com.auction.user.model.User;
import com.auction.common.until.PasswordUtil;
import java.util.UUID;

public class UserService {

    private UserDAO userDAO; // nhận từ bên ngoài vào

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public String signUp(String username, String email,
                         String password, String confirmPassword) {

        if (username == null || username.isBlank())
            return "Tên người dùng không được để trống.";

        if (userDAO.findByUsername(username) != null)
            return "Tên người dùng đã tồn tại.";

        if (email == null || !email.contains("@"))
            return "Email không hợp lệ.";

        if (userDAO.existsByEmail(email))
            return "Email đã tồn tại.";

        if (!PasswordUtil.isStrongPassword(password))
            return "Mật khẩu phải có ít nhất 8 ký tự (hoa, thường, số).";

        if (!password.equals(confirmPassword))
            return "Mật khẩu xác nhận không khớp.";

        String id           = UUID.randomUUID().toString();
        String passwordHash = PasswordUtil.hashPassword(password);
        User newUser        = new Bidder(id, username, email, passwordHash);

        userDAO.save(newUser);
        return "Đăng ký thành công!";
    }

    public String login(String username, String password) {

        User user = userDAO.findByUsername(username);

        if (user == null)
            return "Tài khoản không tồn tại.";

        String inputHash = PasswordUtil.hashPassword(password);

        if (!user.getPasswordHash().equals(inputHash))
            return "Sai mật khẩu.";

        return "Đăng nhập thành công!";
    }
}