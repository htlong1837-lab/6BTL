package com.auction.user.test;

// JUnit 5 - thư viện để viết test
import org.junit.jupiter.api.BeforeEach;   // @BeforeEach: chạy trước mỗi test
import org.junit.jupiter.api.Test;          // @Test: đánh dấu đây là 1 test case
import org.junit.jupiter.api.DisplayName;  // @DisplayName: đặt tên dễ đọc cho test

// Các hàm kiểm tra kết quả (assert = khẳng định)
import static org.junit.jupiter.api.Assertions.*;
// assertDoesNotThrow  - kiểm tra hàm KHÔNG ném exception
// assertThrows        - kiểm tra hàm CÓ ném exception
// assertEquals        - kiểm tra 2 giá trị BẰNG nhau

import com.auction.user.dao.UserDAOImpl;
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


    // ĐĂNG KÝ

    @Test
    @DisplayName("Kiểm tra đăng ký thành công với thông tin hợp lệ")
    public void testSuccessfulSignUp() {
        String name = "Nguyen Thi Kim Ngan";
        String email = "kngan@example.com";
        String password = "Ngan123";
        String confirmPassword = "Ngan123";
        
        String result = assertDoesNotThrow(
            () -> userService.signUp(name, email, password, confirmPassword)
        );

        assertEquals("Đăng ký thành công!", result);
    }

    @Test
    @DisplayName("Email trùng - lỗi DuplicateEmailException")
    void testSignUpwithExistingEmail() throws DuplicateEmailException {

        // Đăng ký lần đầu
        userService.signUp("Nguyen Thi Kim Ngan", "same@example.com", "Ngan123", "Ngan123");

         // Đăng ký lần 2 với email trùng (khác username)
        assertThrows(DuplicateEmailException.class, () -> {
            userService.signUp("Kim Ngan", "same@example.com", "Kn123", "Kn123");
        });
    }

    @Test
    @DisplayName("Đăng ký username trùng - lỗi DuplicateUsername")
    void testSignUpwithExistingUsername() throws DuplicateUsernameException {
        // Đăng ký lan đầu
        userService.signUp("Same", "e1@gmail.com", "pass123", "pass123");

        // Đăng ký lần 2 với username trùng (khác email)
        assertThrows(DuplicateUsernameException.class, () -> {
            userService.signUp("Same", "e2@gmail.com", "pass456", "pass456");
        });
    }

    @Test
    @DisplayName("Đăng ký mật khẩu xác nhận không khớp - lỗi PasswordAuthentication")
    void testSignUpwithPasswordMismatch() {
        assertThrows(PasswordAuthenticationException.class, () -> {
            userService.signUp("Name", "email@example.com", "hihi123", "other123");
        });
    }

    @Test
    @DisplayName("Đăng ký mật khẩu yếu - lỗi InvalidData")
    void testSignUpwithWeakPassWord(){
        // Mật khẩu yếu: ít hơn 8 ký tự, hoặc không có chữ hoa, hoặc không có số
        assertThrows(InvalidDataException.class, () -> {
            userService.signUp("Name", "email@example.com", "weak", "weak");
        });
    }


    @Test
    @DisplayName("Đăng ký với email không có @ - lỗi InvalidData")
    void testSignUpwithInvalidEmail() {
        assertThrows(InvalidDataException.class, () -> {
            userService.signUp("Name", "invalidemail.com", "pw123", "pw123");
        });
    }


    @Test
    @DisplayName("Đăng ký với username rỗng - lỗi InvalidData")
    void testSignUpwithEmptyUsername() {
        assertThrows(InvalidDataException.class, () -> {
            userService.signUp("",
        })
    }



    // ĐĂNG NHẬP
    @Test
    @DisplayName("Đăng nhập thành công")


    @Test
    @DisplayName("Đăng nhập với tài khoản không tồn tại - lỗi UserNotFoundException")
    

    @Test
    @DisplayName("Đăng nhập với mật khẩu sai - lỗi PasswordAuthenticationException")


}


