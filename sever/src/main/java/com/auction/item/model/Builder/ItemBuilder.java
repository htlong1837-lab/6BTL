package com.auction.item.model.Builder;

public interface ItemBuilder {
    ItemBuilder setName(String name);
    ItemBuilder setDes(String des);
    ItemBuilder setStartPrice(double price);
    ItemBuilder setSellerId(String sellerid);
    
}
