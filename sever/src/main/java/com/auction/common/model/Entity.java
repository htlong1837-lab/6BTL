package com.auction.common.model;
public abstract class Entity {
    // bố của id
    protected String id;
    public Entity(String id) {
        this.id = id;
    
    }
    public String getId() {
        return id;
    }
    public abstract void printInfo();
    @Override
    public String toString(){
        return "Entity[id =" + id +"]";
    }
    

}