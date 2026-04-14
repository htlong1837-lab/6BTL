package com.auction.item.model.Builder;

public class ElectronicsBuilder{
    public static class Builder{
        private String name;
        private String des;
        private double startPrice;
        private String category;
        private String sellerId;
        private String brand;
        private int warrantyMonths;


        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        public Builder setDes(String des) {
            this.des = des;
            return this;
        }
    
        public Builder setStartPrice(double startPrice) {
            this.startPrice = startPrice;
            return this;
        }

        public Builder setBrand(String a) {
            this.brand = a;
            return this;
        }
        
        public Builder setWarrantyMonths(int b) {
            this.warrantyMonths = b;
            return this;
        }

        public Electronics build(){
            return new Electronics(this);
        }


}