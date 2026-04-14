package com.auction.item.model.Product;
class Art extends Item {
    private String artist;
    private String medium;
    public Art(String id, String name, String des, double startPrice, String category, String sellerId, String artist, String medium) {
        super(id, name, des, startPrice, category, sellerId);
        this.artist = artist;
        this.medium = medium;
    }
    // getter
    public String getArtist() {
        return artist;
    }
    public String getMedium() {
        return medium;
    }

    //setter
    void setArtist(String newname) {
        this.artist = newname;
    }
    void setMedium(String newmedium) {
        this.medium = newmedium;
    }
    @Override
    public void printInfo() {
        System.out.println("Artist :" + artist);
        System.out.println("Medium :" + medium);
    }
}