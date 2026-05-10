package com.auction.auction.service;

import com.auction.auction.dao.AuctionDAO;
import com.auction.auction.model.Auction;
import com.auction.auction.model.AuctionStatus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AuctionScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AuctionDAO auctionDAO;

    public AuctionScheduler(AuctionDAO auctionDAO) {
        this.auctionDAO = auctionDAO;
    }

    public void scheduleAuctionEnd(Auction auction) {
        // Poll mỗi giây để kiểm tra endTime hiện tại (có thể đã bị gia hạn bởi anti-snipe)
        ScheduledFuture<?>[] ref = new ScheduledFuture<?>[1];
        ref[0] = scheduler.scheduleWithFixedDelay(() -> {
            if (auction.getStatus() != AuctionStatus.RUNNING) {
                ref[0].cancel(false);
                return;
            }
            if (System.currentTimeMillis() >= auction.getEndTime()) {
                auction.endAuction();
                auctionDAO.save(auction);
                ref[0].cancel(false);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
}