package com.auction.auction.service;

import com.auction.auction.dao.AuctionDAO;
import com.auction.auction.model.Auction;
import com.auction.auction.model.AuctionStatus;
import com.auction.user.dao.UserDAO;
import com.auction.user.model.User;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AuctionScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AuctionDAO auctionDAO;
    private final UserDAO userDAO;

    public AuctionScheduler(AuctionDAO auctionDAO, UserDAO userDAO) {
        this.auctionDAO = auctionDAO;
        this.userDAO    = userDAO;
    }

    public void scheduleAuctionEnd(Auction auction) {
        ScheduledFuture<?>[] ref = new ScheduledFuture<?>[1];
        ref[0] = scheduler.scheduleWithFixedDelay(() -> {
            if (auction.getStatus() != AuctionStatus.RUNNING) {
                ref[0].cancel(false);
                return;
            }
            if (System.currentTimeMillis() >= auction.getEndTime()) {
                auction.endAuction();
                settle(auction);
                auctionDAO.save(auction);
                ref[0].cancel(false);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    /** Chuyển tiền từ người thắng sang seller khi phiên kết thúc. */
    public void settle(Auction auction) {
        User winner = auction.getHighestBidder();
        if (winner == null) return;
        double price = auction.getCurrentPrice();
        winner.setBalance(winner.getBalance() - price);
        auction.getSeller().setBalance(auction.getSeller().getBalance() + price);
        userDAO.update(winner);
        userDAO.update(auction.getSeller());
        System.out.println("[Settle] " + winner.getName() + " → " + auction.getSeller().getName()
                           + " : " + price + " VND");
    }
}