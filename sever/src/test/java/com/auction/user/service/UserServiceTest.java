package com.auction.user.service;

// JUnit 5 - thư viện để viết test
import org.junit.jupiter.api.BeforeEach;   // @BeforeEach: chạy trước mỗi test
import org.junit.jupiter.api.Test;          // @Test: đánh dấu đây là 1 test case
import org.junit.jupiter.api.DisplayName;  // @DisplayName: đặt tên dễ đọc cho test

// Các hàm kiểm tra kết quả (assert = khẳng định)
import static org.junit.jupiter.api.Assertions.*;
// assertDoesNotThrow  - kiểm tra hàm KHÔNG ném exception
// assertThrows        - kiểm tra hàm CÓ ném exception
// assertEquals        - kiểm tra 2 giá trị BẰNG nhau

import javax.naming.AuthenticationException;

import com.auction.user.service.UserService;
import com.auction.user.dao.UserDAOImpl;
import com.auction.user.model.User;
import com.auction.exception.UserException.*;

public class UserServiceTest {

    // Khai báo 2 biến sẽ dùng trong tất cả các test
    private UserService userService;   // đối tượng cần test
    private UserDAOImpl userDAO;       // "database giả" lưu trong RAM

    // @BeforeEach: hàm này chạy TRƯỚC MỖI test case
    // Mục đích: reset lại trạng thái sạch, tránh test này ảnh hưởng test kia
    @BeforeEach
    void setUp() {
        userDAO     = new UserDAOImpl();       // tạo database mới (rỗng)
        userService = new UserService(userDAO); // tạo service với database đó
    }
    @Test
    @DisplayName("Kiểm tra đăng ký thành công với thông tin hợp lệ")
    public void testSuccessfulSignUp() {
        String id = "user-001";
        String name = "Nguyen Thi Kim Ngan";
        String email = "kngan@example.com";
        String password = "Ngan123";
        String confirmPassword = "Ngan123";
        
        String result = assertDoesNotThrow(
            () -> userService.signUp(id, name, email, password, confirmPassword)
        );

        assertEquals("Đăng ký thành công!", result);
    }

    @Test
    @DisplayName("Email trùng - lỗi DuplicateDataException")
    void testSignUpwithExistingEmail() throws AuthenticationException, UserException {

        // Đăng ký lần đầu
        userService.signUp("user-001", "Nguyen Thi Kim Ngan", "same@example.com", "Ngan123", "Ngan123");

         // Đăng ký lần 2 với email trùng (khác username)
        assertThrows(DuplicateDataException.class, () -> {
            userService.signUp("user-002", "Kim Ngan", "same@example.com", "Kn123", "Kn123");
        });
    }

    @Test
    @DisplayName("Đăng ký username trùng - lỗi DuplicateUsername")
    void testSignUpwithExistingUsername() throws AuthenticationException, UserException {
        // Đăng ký lan đầu
        userService.signUp("id", "Same", "e1@gmail.com", "pass123", "pass123");

        // Đăng ký lần 2 với username trùng (khác email)
        assertThrows(DuplicateDataException.class, () -> {
            userService.signUp("id", "Same", "e2@gmail.com", "pass456", "pass456");
        });
    }

    @Test
    @DisplayName("Đăng ký mật khẩu xác nhận không khớp - lỗi PasswordAuthentication")
    void testSignUpwithPasswordMismatch() {
        assertThrows(PasswordAuthenticationException.class, () -> {
            userService.signUp("id", "Name", "email@example.com", "hihi123", "other123");
        });
    }

    @Test
    @DisplayName("Đăng ký mật khẩu yếu - lỗi InvalidData")
    void testSignUpwithWeakPassWord(){
        // Mật khẩu yếu: ít hơn 8 ký tự, hoặc không có chữ hoa, hoặc không có số
        assertThrows(InvalidDataException.class, () -> {
            userService.signUp("id", "Name", "email@example.com", "weak", "weak");
        });
    }


    @Test
    @DisplayName("Đăng ký với email không có @ - lỗi InvalidData")
    void testSignUpwithInvalidEmail() {
        assertThrows(InvalidDataException.class, () -> {
            userService.signUp("id", "Name", "invalidemail.com", "pw123", "pw123");
        });
    }


    @Test
    @DisplayName("Đăng ký với username rỗng - lỗi InvalidData")
    void testSignUpwithEmptyUsername() {
        assertThrows(InvalidDataException.class, () -> {
            userService.signUp("id", "", "email@example.com", "pw123", "pw123");
        });
    }



    // ĐĂNG NHẬP
    @Test
    @DisplayName("Đăng nhập thành công")
    void testSuccessfulLogin() throws UserException, AuthenticationException {
        // Trước tiên phải có tài khoản đã đăng ký
        userService.signUp("id", "Name", "email@example.com", "pw123", "pw123");
        // Đăng nhập thành công
        User result = assertDoesNotThrow(() -> userService.login("Name", "pw123"));

        // Kiểm tra user trả về đúng không
        assertNotNull(result);                        // phải khác null
        assertEquals("Name", result.getName());       // đúng tên
        assertEquals("email@example.com", result.getEmail()); // đúng email
    }

    @Test
    @DisplayName("Đăng nhập với tài khoản không tồn tại - lỗi UserNotFoundException")
    void testLoginWithNonExistentAccount() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.login("nonexistent@example.com", "pw123");
        });
    }

    @Test
    @DisplayName("Đăng nhập với mật khẩu sai - lỗi PasswordAuthenticationException")
    void testLoginWithWrongPassword() throws AuthenticationException, UserException {
        // Trước tiên phải có tài khoản đã đăng ký
        userService.signUp("id", "Name", "email@example.com", "pw123", "pw123");

        assertThrows(PasswordAuthenticationException.class, () -> {
            userService.login("Name", "wrongpassword");
        });
    }

}


