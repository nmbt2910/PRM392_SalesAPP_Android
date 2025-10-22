package com.prm392.salesapp.chat;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// This class represents the response for a customer fetching their thread.
public class ThreadDetailResponse {

    @SerializedName("isNewUser")
    private boolean isNewUser;

    @SerializedName("message")
    private String message;

    @SerializedName("threadID")
    private Integer threadId;

    @SerializedName("messages")
    private List<Message> messages;

    public boolean isNewUser() {
        return isNewUser;
    }

    public String getMessage() {
        return message;
    }

    public Integer getThreadId() {
        return threadId;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
