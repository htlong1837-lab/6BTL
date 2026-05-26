package com.auction.auction.service;

import com.auction.auction.dao.AuctionDAO;
import com.auction.auction.model.Auction;
import com.auction.auction.model.AuctionStatus;
import com.auction.bid.dao.BidDAO;
import com.auction.bid.model.BidTransaction;
import com.auction.user.dao.UserDAO;
import com.auction.user.model.User;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AuctionScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AuctionDAO auctionDAO;
    private final UserDAO    userDAO;
    private final BidDAO     bidDAO;

    public AuctionScheduler(AuctionDAO auctionDAO, UserDAO userDAO, BidDAO bidDAO) {
        this.auctionDAO = auctionDAO;
        this.userDAO    = userDAO;
        this.bidDAO     = bidDAO;
    }

    public void scheduleAuctionEnd(Auction initialAuction) {
        String auctionId = initialAuction.getId();
        ScheduledFuture<?>[] ref = new ScheduledFuture<?>[1];
        ref[0] = scheduler.scheduleWithFixedDelay(() -> {
            Auction auction = auctionDAO.findById(auctionId);
            // Nếu null do lỗi DB tạm thời, thử lại tick sau — không cancel
            if (auction == null) return;
            if (auction.getStatus() != AuctionStatus.RUNNING) {
                ref[0].cancel(false);
                return;
            }
            if (System.currentTimeMillis() >= auction.getEndTime()) {
                // Đọc lại lần nữa để lấy highest_bidder mới nhất trước khi settle
                Auction fresh = auctionDAO.findById(auctionId);
                if (fresh == null || fresh.getStatus() != AuctionStatus.RUNNING) {
                    ref[0].cancel(false);
                    return;
                }
                fresh.endAuction();
                settle(fresh);
                // Chỉ cập nhật status, KHÔNG overwrite current_price và highest_bidder_id
                // vì BidService đã lưu đúng rồi — tránh ghi đè null lên dữ liệu đúng
                auctionDAO.updateStatus(auctionId, AuctionStatus.FINISHED);
                ref[0].cancel(false);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void settle(Auction auction) {
        User winner = auction.getHighestBidder();

        // Fallback: nếu auction object có highestBidder = null (do stale read),
        // tra trực tiếp bảng bids để tìm người đặt giá cao nhất
        if (winner == null && bidDAO != null) {
            BidTransaction highest = bidDAO.findHighestBidByAuction(auction.getId());
            if (highest != null) {
                winner = userDAO.findById(highest.getBidderId());
            }
        }

        if (winner == null) {
            System.out.println("[Settle] Không có người đặt giá cho phiên: " + auction.getId());
            return;
        }

        double price = auction.getCurrentPrice();
        // Nếu currentPrice là giá khởi điểm (chưa cập nhật do stale read), lấy từ bids
        if (bidDAO != null) {
            BidTransaction highest = bidDAO.findHighestBidByAuction(auction.getId());
            if (highest != null && highest.getAmount() > price) {
                price = highest.getAmount();
            }
        }

        winner.setBalance(winner.getBalance() - price);
        auction.getSeller().setBalance(auction.getSeller().getBalance() + price);
        userDAO.update(winner);
        userDAO.update(auction.getSeller());
        System.out.println("[Settle] " + winner.getName() + " → " + auction.getSeller().getName()
                           + " : " + price + " VND");
    }
}
