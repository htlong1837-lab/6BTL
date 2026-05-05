package com.auction.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import javax.naming.AuthenticationException;
import com.auction.user.dao.UserDAOImpl;
import com.auction.user.model.User;
import com.auction.exception.UserException.*;

public class UserServiceTest {

    private UserService userService;
    private UserDAOImpl userDAO;

    @BeforeEach
    void setUp() {
        userDAO     = new UserDAOImpl();
        userService = new UserService(userDAO);
    }

    // ============================================================
    // NHÓM TEST: ĐĂNG KÝ (signUp)
    // ============================================================

    @Test
    @DisplayName("Đăng ký thành công với thông tin hợp lệ")
    public void testSuccessfulSignUp() {
        String result = assertDoesNotThrow(
            () -> userService.signUp("user-001", "Nguyen Thi Kim Ngan", "Ngan1234", "Ngan1234", "Bidder")
        );
        assertEquals("Đăng ký thành công!", result);
    }

    @Test
    @DisplayName("Username trùng - lỗi DuplicateDataException")
    void testSignUpwithExistingUsername() throws AuthenticationException, UserException {
        userService.signUp("id1", "Same", "Pass1234", "Pass1234", "Bidder");

        assertThrows(DuplicateDataException.class, () -> {
            userService.signUp("id2", "Same", "Pass4567", "Pass4567", "Bidder");
        });
    }

    @Test
    @DisplayName("Đăng ký mật khẩu xác nhận không khớp - lỗi PasswordAuthentication")
    void testSignUpwithPasswordMismatch() {
        assertThrows(PasswordAuthenticationException.class, () -> {
            userService.signUp("id", "Name", "Hihi1234", "Other1234", "Bidder");
        });
    }

    @Test
    @DisplayName("Đăng ký mật khẩu yếu - lỗi InvalidData")
    void testSignUpwithWeakPassWord() {
        assertThrows(InvalidDataException.class, () -> {
            userService.signUp("id", "Name", "weak", "weak", "Bidder");
        });
    }

    @Test
    @DisplayName("Đăng ký với username rỗng - lỗi InvalidData")
    void testSignUpwithEmptyUsername() {
        assertThrows(InvalidDataException.class, () -> {
            userService.signUp("id", "", "Pass1234", "Pass1234", "Bidder");
        });
    }

    // ============================================================
    // NHÓM TEST: ĐĂNG NHẬP (login)
    // ============================================================

    @Test
    @DisplayName("Đăng nhập thành công")
    void testSuccessfulLogin() throws UserException, AuthenticationException {
        userService.signUp("id", "Name", "Password1", "Password1", "Bidder");

        User result = assertDoesNotThrow(() -> userService.login("Name", "Password1"));

        assertNotNull(result);
        assertEquals("Name", result.getName());
    }

    @Test
    @DisplayName("Đăng nhập với tài khoản không tồn tại - lỗi UserNotFoundException")
    void testLoginWithNonExistentAccount() {
        assertThrows(UserNotFoundException.class, () -> {
            userService.login("nonexistent", "Password1");
        });
    }

    @Test
    @DisplayName("Đăng nhập với mật khẩu sai - lỗi PasswordAuthenticationException")
    void testLoginWithWrongPassword() throws AuthenticationException, UserException {
        userService.signUp("id", "Name", "Password1", "Password1", "Bidder");

        assertThrows(PasswordAuthenticationException.class, () -> {
            userService.login("Name", "wrongpassword");
        });
    }
}
