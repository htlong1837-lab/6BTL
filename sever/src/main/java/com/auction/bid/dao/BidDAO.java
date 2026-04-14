package com.auction.bid.dao;

import com.auction.bid.model.BidTransaction;
import java.util.List;

public interface BidDAO {

    /** Lưu một lần đặt giá */
    void save(BidTransaction transaction);

    /** Lấy tất cả lịch sử đặt giá */
    List<BidTransaction> findAll();

    /** Lấy tất cả lần đặt giá trong một phiên đấu giá */
    List<BidTransaction> findByAuctionId(String auctionId);

    /** Lấy toàn bộ lịch sử đặt giá của một bidder */
    List<BidTransaction> findByBidderId(String bidderId);

    /** Lấy lần đặt giá cao nhất trong một phiên (dùng cho logic xác định người thắng) */
    BidTransaction findHighestBidByAuction(String auctionId);

    /** Xóa toàn bộ bid trong một phiên (dọn dẹp khi phiên kết thúc) */
    void deleteByAuctionId(String auctionId);
}
