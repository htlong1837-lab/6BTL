package com.auction.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import com.auction.bid.service.BidService;
import com.auction.user.model.Bidder;

public class BidServiceTest {

    private BidService bidService;   // service cần test
    private Bidder bidder;           // người dùng mẫu để test

    @BeforeEach
    void setUp() {
        bidService = new BidService(null, null, null);

        // Tạo một Bidder giả: id, name, email, passwordHash
        bidder = new Bidder("user-001", "TestUser", "test@x.com", "hashed");
        // Mặc định balance = 0 khi mới tạo
    }

    // ============================================================
    // NHÓM TEST: NẠP TIỀN (deposit)
    // ============================================================

    @Test
    @DisplayName("Nạp tiền hợp lệ - số dư tăng")
    void depositValidAmountIncreasesBalance() {
        // Nạp 500,000 VND
        String result = bidService.deposit(bidder, 500000);

        assertTrue(result.contains("thành công"),
            "Kết quả phải chứa 'thành công', nhưng nhận được: " + result);

        assertEquals(500000, bidder.getBalance(),
            "Số dư sau khi nạp phải là 500,000");
    }

    @Test
    @DisplayName("Nạp tiền âm - báo lỗi")
    void depositNegativeAmountReturnsError() {
        String result = bidService.deposit(bidder, -100);

        // Kết quả phải là thông báo lỗi (không chứa "thành công")
        assertFalse(result.contains("thành công"),
            "Nạp tiền âm không được thành công");
    }

    @Test
    @DisplayName("Nạp tiền bằng 0 - báo lỗi")
    void depositZeroAmountReturnsError() {
        String result = bidService.deposit(bidder, 0);
        assertFalse(result.contains("thành công"));
    }

    // ============================================================
    // NHÓM TEST: ĐẶT GIÁ (placeBid)
    // ============================================================

    @Test
    @DisplayName("Đặt giá hợp lệ - thành công")
    void placeBidSufficientBalanceReturnsSuccess() {
        // Chuẩn bị: nạp tiền trước
        bidder.setBalance(1000000);

        // Đặt giá 500,000 vào phiên "auction-001"
        String result = bidService.placeBid(bidder, "auction-001", 500000);

        assertTrue(result.contains("thành công"),
            "Đặt giá phải thành công khi có đủ số dư");
    }

    @Test
    @DisplayName("Đặt giá khi số dư không đủ - báo lỗi")
    void placeBidInsufficientBalanceReturnsError() {
        // bidder có 0 VND, nhưng muốn đặt 500,000 - không đủ
        bidder.setBalance(0);

        String result = bidService.placeBid(bidder, "auction-001", 500000);

        assertFalse(result.contains("thành công"),
            "Không được đặt giá khi số dư không đủ");
    }

    @Test
    @DisplayName("Đặt giá âm - báo lỗi")
    void placeBidNegativeAmountReturnsError() {
        bidder.setBalance(1000000);

        String result = bidService.placeBid(bidder, "auction-001", -500);

        assertFalse(result.contains("thành công"));
    }

    // ============================================================
    // NHÓM TEST: RÚT KHỎI PHIÊN (withdrawBid)
    // ============================================================

    @Test
    @DisplayName("Rút khỏi phiên đang tham gia - thành công")
    void withdrawBidActiveAuctionReturnsSuccess() {
        // Đặt giá trước - mới có trong danh sách "đang tham gia"
        bidder.setBalance(1000000);
        bidService.placeBid(bidder, "auction-001", 200000);

        // Rút khỏi phiên
        String result = bidService.withdrawBid(bidder, "auction-001");

        assertTrue(result.contains("Đã rút"),
            "Kết quả rút phải chứa 'Đã rút'");
    }

    @Test
    @DisplayName("Rút khỏi phiên chưa tham gia - báo lỗi")
    void withdrawBidNotParticipatingReturnsError() {
        // Chưa đặt giá phiên nào cả, thử rút - phải báo lỗi
        String result = bidService.withdrawBid(bidder, "auction-999");

        assertFalse(result.contains("Đã rút"),
            "Không thể rút khỏi phiên chưa tham gia");
    }

    // ============================================================
    // NHÓM TEST: XEM LỊCH SỬ (viewBidHistory)
    // ============================================================

    @Test
    @DisplayName("Xem lịch sử khi chưa đặt giá lần nào - báo trống")
    void viewBidHistoryNoHistoryReturnsEmpty() {
        String result = bidService.viewBidHistory(bidder);

        assertTrue(result.contains("Chưa có"),
            "Phải báo chưa có lịch sử khi bidder chưa đặt giá lần nào");
    }

    @Test
    @DisplayName("Xem lịch sử sau khi đã đặt giá - có kết quả")
    void viewBidHistoryAfterBidReturnsHistory() {
        bidder.setBalance(1000000);
        bidService.placeBid(bidder, "auction-001", 300000);

        String result = bidService.viewBidHistory(bidder);

        // Lịch sử phải chứa id của phiên đó
        assertTrue(result.contains("auction-001"),
            "Lịch sử phải chứa auction-001");
    }
}
