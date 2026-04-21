package com.auction.bid.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class BidLockManager {

    // Mỗi auctionId có 1 lock riêng
    // ConcurrentHashMap vì map này cũng bị nhiều thread truy cập cùng lúc
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    // Lấy lock của auction, nếu chưa có thì tự tạo mới
    public ReentrantLock getLock(String auctionId) {
        return locks.computeIfAbsent(auctionId, k -> new ReentrantLock());
    }

    // Gọi khi auction kết thúc để dọn dẹp
    public void removeLock(String auctionId) {
        locks.remove(auctionId);
    }
}
