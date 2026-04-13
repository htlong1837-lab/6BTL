package com.auction.common.observer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuctionEventManager {

    // Singleton
    private static final AuctionEventManager INSTANCE = new AuctionEventManager();

    // auctionId -> danh sách listener đang theo dõi phiên đó
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<AuctionListener>> listeners
            = new ConcurrentHashMap<>();

    // Thread pool riêng để notify async — không block luồng đấu giá
    private final ExecutorService notifyExecutor = Executors.newCachedThreadPool();

    private AuctionEventManager() {}

    public static AuctionEventManager getInstance() {
        return INSTANCE;
    }

    //Client đăng ký theo dõi 1 phiên đấu giá 
    public void subscribe(String auctionId, AuctionListener listener) {
        listeners.computeIfAbsent(auctionId, k -> new CopyOnWriteArrayList<>())
                 .add(listener);
    }

    // Client hủy theo dõi 
    public void unsubscribe(String auctionId, AuctionListener listener) {
        CopyOnWriteArrayList<AuctionListener> list = listeners.get(auctionId);
        if (list != null) {
            list.remove(listener);
        }
    }

    //Server gọi hàm này mỗi khi có sự kiện — notify async tất cả listener 
    public void publish(String auctionId, AuctionEvent event) {
        CopyOnWriteArrayList<AuctionListener> list = listeners.get(auctionId);
        if (list == null || list.isEmpty()) return;

        for (AuctionListener listener : list) {
            notifyExecutor.submit(() -> {
                try {
                    listener.onEvent(event);
                } catch (Exception e) {
                    System.err.println("[EventManager] Listener error: " + e.getMessage());
                }
            });
        }
    }

    // Dọn listener khi phiên đấu giá kết thúc 
    public void clearListeners(String auctionId) {
        listeners.remove(auctionId);
    }

    public void shutdown() {
        notifyExecutor.shutdown();
    }
}
