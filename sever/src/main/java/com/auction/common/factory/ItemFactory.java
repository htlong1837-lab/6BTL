package com.auction.common.factory;

import com.auction.item.model.Product.Electronics;
import com.auction.item.model.Product.Item;
import com.auction.item.model.Product.Art;
import com.auction.item.model.Product.Vehicle;

public class ItemFactory {
    public static Item create(String type, String id, String name, String des, double price, String sellerId,String category, String... extra) {
        switch (type.toUpperCase()) {
            case "ELECTRONICS" :
                return new Electronics(id, name, des, price, category, sellerId,
                    extra.length > 0 ? extra[0] : "không có dữu liệu",
                    extra.length > 1 ? Integer.parseInt(extra[1]) : 12);
            case "VEHICLES"  :
                return new Vehicle(id, name, des, price, category, sellerId,
                    extra.length > 0 ? extra[0] : "không có dữu liệu",
                    extra.length > 1 ? extra[1] : "không có dữu liệu",
                    extra.length > 2 ? Integer.parseInt(extra[2]) : 2026);
            case "ART"    :
                return new Art(id, name, des, price, category, sellerId,
                    extra.length > 0 ? extra[0] : "không có dữu liệu",
                    extra.length > 1 ? extra[1] : "không có dữu liệu");
            default:
                throw new IllegalArgumentException(
                    "Unknown item category: " + type
                );    
        }
    }
}