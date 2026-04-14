package com.auction.item.model.Factory;

import com.auction.item.model.Product.Art;

public class ArtFactory implements ItemFactory {
    Item createItem(){ return new Art.Builder().setName().;}
    
}
