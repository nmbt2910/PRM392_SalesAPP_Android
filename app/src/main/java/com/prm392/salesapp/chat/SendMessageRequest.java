package com.prm392.salesapp.chat;

public class SendMessageRequest {
    private Integer threadId;
    private String content;

    public SendMessageRequest(Integer threadId, String content) {
        this.threadId = threadId;
        this.content = content;
    }

    public Integer getThreadId() {
        return threadId;
    }

    public String getContent() {
        return content;
    }
}
