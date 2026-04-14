package com.auction.item.model.Builder;

import com.auction.item.model.Product.Art;
import com.auction.item.model.Product.Item;

public class ArtBuilder implements ItemBuilder{
    private String id;
    private String name;
    private String des;
    private double startPrice;
    private String category;
    private String sellerId;
    private String artist;
    private String medium;

    @Override
    public ArtBuilder setId(String id){
        this.id = id;
        return this;
    }

    @Override
    public ArtBuilder setName(String name){
        this.name = name;
        return this;
    }

    @Override
    public ArtBuilder setDes(String d){
        this.des = d;
        return this;
    }

    @Override
    public ArtBuilder setStartPrice(double startPrice){
        this.startPrice = startPrice;
        return this;
    }

    @Override
    public ArtBuilder setCategory(String c){
        this.category = c;
        return this;
    }

    @Override
    public ArtBuilder setSellerId(String sid){
        this.sellerId = sid;
        return this;
    }

    public ArtBuilder setArtist(String artist){
        this.artist = artist;
        return this;
    }

    public ArtBuilder setMedium(String medium){
        this.medium = medium;
        return this;
    }

    @Override
    public Item build() {
        return new Art(id, name, des, startPrice, category, sellerId, artist, medium);
    }
    
}
