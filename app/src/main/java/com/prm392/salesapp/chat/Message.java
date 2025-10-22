package com.prm392.salesapp.chat;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("MessageID")
    private int messageId;

    @SerializedName("SenderID")
    private int senderId;

    @SerializedName("SenderName")
    private String senderName;

    @SerializedName("MessageContent")
    private String content;

    @SerializedName("CreatedAt")
    private String createdAt;

    @SerializedName("IsRead")
    private boolean isRead;

    public int getMessageId() {
        return messageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return isRead;
    }
}
