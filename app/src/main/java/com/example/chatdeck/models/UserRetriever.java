package com.example.chatdeck.models;

import com.cometchat.pro.models.User;
import com.stfalcon.chatkit.commons.models.IUser;
// Chun Kit Jonathan Chan

// region Interface for using ChatKit API's method to get sender/receiver's information
public class UserRetriever implements IUser {

    private User user;

    public UserRetriever (User user){
        this.user = user;
    }

    @Override
    public String getId() {
        return user.getUid();
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public String getAvatar() {
        return user.getAvatar();
    }
}
// end region
