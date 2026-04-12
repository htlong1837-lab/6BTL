
package com.auction.auction.model;

import com.auction.user.model.User;

public class AutoBid {

    private User user;
    private double maxBid;
    private double increment;

    public AutoBid(User user, double maxBid, double increment) {
        this.user = user;
        this.maxBid = maxBid;
        this.increment = increment;
    }

    public User getUser() {
        return user;
    }

    public double getMaxBid() {
        return maxBid;
    }

    public double getIncrement() {
        return increment;
    }
}
