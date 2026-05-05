package com.auction.item.model.Builder;

import com.auction.item.model.Product.Item;

public interface ItemBuilder {
    ItemBuilder setId(String id);
    ItemBuilder setName(String name);
    ItemBuilder setDes(String des);
    ItemBuilder setStartPrice(double price);
    ItemBuilder setCategory(String category);
    ItemBuilder setSellerId(String sellerId);
    Item build();
}
