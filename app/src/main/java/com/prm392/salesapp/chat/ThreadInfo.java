
package com.prm392.salesapp.chat;

import com.google.gson.annotations.SerializedName;

public class ThreadInfo {
    @SerializedName("ThreadID")
    private int threadId;

    private String customerName;

    @SerializedName("CustomerID")
    private int customerId;

    public int getThreadId() {
        return threadId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getCustomerId() {
        return customerId;
    }
}
