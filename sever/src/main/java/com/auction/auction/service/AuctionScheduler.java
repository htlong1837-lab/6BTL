package com.auction.auction.service;

import com.auction.auction.model.Auction;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuctionScheduler {

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void scheduleAuctionEnd(Auction auction) {

        long delay = auction.getEndTime() - System.currentTimeMillis();

        if (delay < 0) delay = 0;

        scheduler.schedule(() -> {
            auction.endAuction();
        }, delay, TimeUnit.MILLISECONDS);
    }
}