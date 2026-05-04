package com.auction.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.auction.auction.dao.AuctionDAO;
import com.auction.bid.dao.BidDAO;
import com.auction.bid.service.BidLockManager;
import com.auction.bid.service.BidService;
import com.auction.user.model.Bidder;

public class BidServiceTest {

    private BidService bidService;   // service cần test
    private Bidder bidder;           // người dùng mẫu để test

    @BeforeEach

    void setUp() {
        // Tạo các "database giả" và lock manager để khởi tạo BidService
        // có thể dùng mock hoặc fake nếu muốn
        AuctionDAO auctionDAO = mock(AuctionDAO.class); // giả định có lớp mock này
        BidDAO bidDAO = mock(BidDAO.class);
        BidLockManager lockManager = new BidLockManager();
        bidService = new BidService(auctionDAO, bidDAO, lockManager);

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
    @DisplayName("Nạp tiền bằng 0 - báo lỗi")
    void depositZeroAmountReturnsError() {
        String result = bidService.deposit(bidder, 0);
        assertFalse(result.contains("thành công"));
    }

    // ============================================================
    // NHÓM TEST: ĐẶT GIÁ (placeBid)
    // ============================================================

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

}
