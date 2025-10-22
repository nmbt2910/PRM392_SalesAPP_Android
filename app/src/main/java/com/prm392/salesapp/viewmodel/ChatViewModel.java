package com.prm392.salesapp.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.prm392.salesapp.api.ChatApiService;
import com.prm392.salesapp.chat.Message;
import com.prm392.salesapp.chat.SendMessageRequest;
import com.prm392.salesapp.chat.SendMessageResponse;
import com.prm392.salesapp.chat.Thread;
import com.prm392.salesapp.chat.ThreadDetailResponse;
import com.prm392.salesapp.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatViewModel extends ViewModel {

    private static final String TAG = "ChatViewModel";
    private final ChatApiService chatApiService;
    private final MutableLiveData<List<Thread>> threads = new MutableLiveData<>();
    private final MutableLiveData<ThreadDetailResponse> customerThread = new MutableLiveData<>();
    private final MutableLiveData<List<Message>> adminThreadMessages = new MutableLiveData<>();
    private final MutableLiveData<SendMessageResponse> sendMessageResponse = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ChatViewModel() {
        chatApiService = RetrofitClient.getChatApi();
    }

    public LiveData<List<Thread>> getThreads() {
        return threads;
    }

    public LiveData<ThreadDetailResponse> getCustomerThread() {
        return customerThread;
    }

    public LiveData<List<Message>> getAdminThreadMessages() {
        return adminThreadMessages;
    }

    public LiveData<SendMessageResponse> getSendMessageResponse() {
        return sendMessageResponse;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void fetchThreads(String token) {
        chatApiService.getThreads("Bearer " + token).enqueue(new Callback<List<Thread>>() {
            @Override
            public void onResponse(Call<List<Thread>> call, Response<List<Thread>> response) {
                if (response.isSuccessful()) {
                    threads.setValue(response.body());
                } else {
                    String errorMsg = "Error fetching threads: " + response.code() + " " + response.message();
                    Log.e(TAG, errorMsg);
                    error.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<List<Thread>> call, Throwable t) {
                Log.e(TAG, "Failed to fetch threads", t);
                error.setValue("Failed to fetch threads: " + t.getMessage());
            }
        });
    }

    public void fetchAdminThreadMessages(String token, int threadId) {
        chatApiService.getAdminThreadMessages("Bearer " + token, threadId).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful()) {
                    adminThreadMessages.setValue(response.body());
                } else {
                    String errorMsg = "Error fetching thread messages: " + response.code() + " " + response.message();
                    Log.e(TAG, errorMsg);
                    error.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.e(TAG, "Failed to fetch thread messages", t);
                error.setValue("Failed to fetch thread messages: " + t.getMessage());
            }
        });
    }

    public void fetchCustomerThread(String token) {
        chatApiService.getCustomerThread("Bearer " + token).enqueue(new Callback<ThreadDetailResponse>() {
            @Override
            public void onResponse(Call<ThreadDetailResponse> call, Response<ThreadDetailResponse> response) {
                if (response.isSuccessful()) {
                    customerThread.setValue(response.body());
                } else {
                    String errorMsg = "Error fetching customer thread: " + response.code() + " " + response.message();
                    Log.e(TAG, errorMsg);
                    error.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ThreadDetailResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch customer thread", t);
                error.setValue("Failed to fetch customer thread: " + t.getMessage());
            }
        });
    }

    public void sendMessage(String token, Integer threadId, String content) {
        chatApiService.sendMessage("Bearer " + token, new SendMessageRequest(threadId, content)).enqueue(new Callback<SendMessageResponse>() {
            @Override
            public void onResponse(Call<SendMessageResponse> call, Response<SendMessageResponse> response) {
                if (response.isSuccessful()) {
                    sendMessageResponse.setValue(response.body());
                } else {
                    String errorMsg = "Error sending message: " + response.code() + " " + response.message();
                    Log.e(TAG, errorMsg);
                    error.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<SendMessageResponse> call, Throwable t) {
                Log.e(TAG, "Failed to send message", t);
                error.setValue("Failed to send message: " + t.getMessage());
            }
        });
    }
}
