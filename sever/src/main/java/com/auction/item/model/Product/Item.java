package com.auction.item.model.Product;
import com.auction.common.model.Entity;
import java.util.ArrayList;
import java.util.List;
public abstract class Item extends Entity {
    protected String name;
    protected String des;
    protected double startPrice;
    protected String category;
    protected String sellerId;
    public Item(String id, String name, String des, double startPrice, String category, String sellerId) {
        super(id);
        this.name = name;
        this.des = des;
        this.startPrice = startPrice;
        this.category = category;
        this.sellerId = sellerId;
    }

    //getter
    public String getName() {
        return name;
    }
    public String getDes() {
        return des;

    }
    public double getStartPrice() {
        return startPrice;
    }
    public String getCategory() {
        return category;
    }
    public String getSellerId() {
        return sellerId;
    }

    //setter
    public void setName(String name) {
        this.name = name;
    }
    public void setDes(String des) {
        this.des = des;
    }
    
    public void setStartPrice(double startPrice) {
        this.startPrice = startPrice;
    }
    @Override
    public void printInfo() {
        System.out.println("Sản phẩm:" + name + "mô tả:" + des + "có giá khởi điểm" + startPrice);
    }


    
}
