package com.auction.item.model.Factory;
import com.auction.item.model.Builder.ArtBuilder;
import com.auction.item.model.Product.Item;
public class ArtFactory implements ItemFactory {
    private String id;
    private String name;
    private String des;
    private double startPrice;
    private String category;
    private String sellerId;
    private String artist;
    private String medium;
    public ArtFactory(String id, String name, String des, double startPrice, String category, String sellerId, String artist, String medium) {
        this.id = id;
        this.name = name;
        this.des = des;
        this.startPrice = startPrice;
        this.category = category;
        this.sellerId = sellerId;
        this.artist = artist;
        this.medium = medium;

    }
    public Item createItem() {
        return new ArtBuilder()
            .setId(id)
            .setName(name)
            .setDes(des)
            .setStartPrice(startPrice)
            .setCategory(category)
            .setSellerId(sellerId)
            .setArtist(artist)
            .setMedium(medium)
            .build();

    }
}
