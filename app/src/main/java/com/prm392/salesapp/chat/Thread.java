package com.prm392.salesapp.chat;

import com.google.gson.annotations.SerializedName;

public class Thread {
    @SerializedName("ThreadID")
    private int threadId;

    @SerializedName("CustomerID")
    private int customerId;

    @SerializedName("CustomerName")
    private String customerName;

    @SerializedName("LastMessageAt")
    private String lastMessageAt;

    @SerializedName("IsUnread")
    private boolean isUnread;

    @SerializedName("LastMessage")
    private String lastMessage;

    public int getThreadId() {
        return threadId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getLastMessageAt() {
        return lastMessageAt;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
