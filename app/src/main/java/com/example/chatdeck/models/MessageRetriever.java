package com.example.chatdeck.models;

import androidx.annotation.Nullable;

import com.cometchat.pro.models.TextMessage;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;
import com.stfalcon.chatkit.messages.MessageInput;

import java.util.Date;
// Chun Kit Jonathan Chan

// region Interface for using ChatKit API's method to get message's information
public class MessageRetriever implements IMessage, MessageContentType.Image {

    private TextMessage message;

    public MessageRetriever(TextMessage message) {
        this.message = message;
    }

    @Override
    public String getId() {
        return message.getMuid();
    }

    @Override
    public String getText() {
        return message.getText();
    }

    @Override
    public IUser getUser() {
        return new UserRetriever(message.getSender());
    }

    @Override
    public Date getCreatedAt() {
        return new Date(message.getSentAt() * 1000);
    }

    @Nullable
    @Override
    public String getImageUrl() {
        return null;
    }
}
// end region
