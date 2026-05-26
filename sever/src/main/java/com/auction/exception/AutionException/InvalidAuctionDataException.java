package com.auction.exception.AutionException;

// Ném khi dữ liệu tạo phiên đấu giá không hợp lệ (duration <= 0, item/seller null...)
public class InvalidAuctionDataException extends AuctionException {
    public InvalidAuctionDataException(String message) {
        super(message);
    }
}
