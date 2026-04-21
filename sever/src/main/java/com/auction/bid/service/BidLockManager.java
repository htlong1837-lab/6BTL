package com.auction.bid.service;
import com.auction.bid.service.BidService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Map;
public class BidLockManager {
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();
    public BidLockManager(String auctionId, )
    


}
