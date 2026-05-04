package com.auction.client.model;

import java.util.List;

public class FakeDataHelper {

    public static List<UserItem> makeUsers() {
        long now = System.currentTimeMillis();
        return List.of(
            new UserItem("U001", "nguyen_van_a",  "nguyenvana@gmail.com",  "Bidder", "ACTIVE"),
            new UserItem("U002", "tran_thi_b",    "tranthib@gmail.com",    "Seller", "ACTIVE"),
            new UserItem("U003", "le_van_c",      "levanc@gmail.com",      "Bidder", "BANNED"),
            new UserItem("U004", "pham_thi_d",    "phamthid@gmail.com",    "Seller", "ACTIVE"),
            new UserItem("U005", "hoang_van_e",   "hoangvane@gmail.com",   "Bidder", "ACTIVE"),
            new UserItem("U006", "do_thi_f",      "dothif@gmail.com",      "Bidder", "BANNED"),
            new UserItem("U007", "vu_van_g",      "vuvang@gmail.com",      "Seller", "ACTIVE"),
            new UserItem("U008", "dang_thi_h",    "dangthih@gmail.com",    "Bidder", "ACTIVE")
        );
    }

    public static List<SellerProduct> makeSellerProducts() {
        long now = System.currentTimeMillis();
        return List.of(
            new SellerProduct("SP001", "iPhone 15 Pro Max",
                "Mới 100%, còn seal, màu titan tự nhiên",
                25_000_000, now + 3 * 3_600_000L),
            new SellerProduct("SP002", "Laptop Dell XPS 15",
                "Core i7, 16GB RAM, 512GB SSD",
                18_500_000, now + 5 * 3_600_000L),
            new SellerProduct("SP003", "Đồng hồ Seiko Prospex",
                "Automatic, dây da, chống nước 200m",
                7_200_000, now + 2 * 3_600_000L),
            new SellerProduct("SP004", "Máy ảnh Sony A7III",
                "Body only, đã qua sử dụng ít, còn bảo hành",
                30_000_000, now + 8 * 3_600_000L),
            new SellerProduct("SP005", "Tai nghe Sony WH-1000XM5",
                "Chống ồn chủ động, mới 99%",
                5_500_000, now + 1 * 3_600_000L)
        );
    }

    public static List<Auction> makeAuctions() {
        long now = System.currentTimeMillis();
        return List.of(
            new Auction("A001", "iPhone 15 Pro Max",  25_000_000, now + 3 * 3_600_000L, "tran_thi_b"),
            new Auction("A002", "Laptop Dell XPS 15", 18_500_000, now + 5 * 3_600_000L, "tran_thi_b"),
            new Auction("A003", "Đồng hồ Seiko",       7_200_000, now + 2 * 3_600_000L, "pham_thi_d"),
            new Auction("A004", "Máy ảnh Sony A7III",  30_000_000, now + 8 * 3_600_000L, "vu_van_g"),
            new Auction("A005", "Tai nghe Sony XM5",    5_500_000, now + 1 * 3_600_000L, "pham_thi_d"),
            new Auction("A006", "MacBook Pro M3",       45_000_000, now + 6 * 3_600_000L, "tran_thi_b"),
            new Auction("A007", "Samsung Galaxy S24",   20_000_000, now + 4 * 3_600_000L, "vu_van_g")
        );
    }
}
