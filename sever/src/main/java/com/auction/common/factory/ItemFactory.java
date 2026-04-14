package com.auction.common.factory;

import com.auction.item.model.Electronics;
import com.auction.item.model.Item;
import com.auction.item.model.Art;
import com.auction.item.model.Vehicle;

public class ItemFactory {
    public static Item create(String type, String id, String name, String des, double price, String sellerId,String category, String... extra) {
        switch (type.toUpperCase()) {
            case "ELECTRONICS" :
                return new Electronics(id, name, des, price, sellerId, 
                    extra.length > 0 ? extra[0] : "không có dữu liệu", 
                    extra.length > 1 ? Integer.parseInt(extra[1]) : 12);
            case "VEHICLES"  :
                return new Vehicle(id, name, des, price, sellerId, 
                    extra.length > 0 ? extra[0] : "không có dữu liệu", 
                    extra.length > 1 ? extra[1] : "không có dữu liệu", 
                    extra.length > 2 ? Integer.parseInt(extra[2]) : 2026);
            case "ART"    :
                return new Art(id, name, des, price, sellerId, 
                    extra.length > 0 ? extra[0] : "không có dữu liệu", 
                    extra.length > 1 ? extra[1] : "không có dữu liệu");
            default:
                throw new IllegalArgumentException(
                    "Unknow item category: " + type
                );    
        }
    }
}