package com.prm392.salesapp.chat;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class SendMessageResponse {

    @SerializedName("messageId")
    private int messageId;

    @SerializedName("threadId")
    private int threadId;

    @SerializedName("senderId")
    private int senderId;

    @SerializedName("content")
    private String content;

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("isAdmin")
    private boolean isAdmin;

    public int getMessageId() {
        return messageId;
    }

    public int getThreadId() {
        return threadId;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
