package com.auction.item.model.Product;
import java.util.List;
import com.auction.item.model.Product.Item;
public class Art extends Item {
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
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public void setMedium(String medium) {
        this.medium = medium;
    }
//duyệt hợp lệ
    @Override
    public boolean isApproved() {
        super.isApproved();
        if (artist == null || artist.isEmpty()) {
            return false;
        }
        if (medium == null || medium.isEmpty()) {
            return false;
        }
        return true;
    }
//chỉnh sửa khi duyệt lỗi
   
    public void editItemError( Art item){
        super.editItemError(item);
        if (item.artist == null || item.artist.isEmpty()) {
            this.artist = item.artist;
        }
        if (item.medium == null || item.medium.isEmpty()) {
            this.medium = item.medium;
        }

    }



    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("Artist : " + artist);
        System.out.println("Medium : " + medium);
    }
}