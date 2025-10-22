package com.prm392.salesapp.api;

import com.prm392.salesapp.chat.Message;
import com.prm392.salesapp.chat.SendMessageRequest;
import com.prm392.salesapp.chat.SendMessageResponse;
import com.prm392.salesapp.chat.Thread;
import com.prm392.salesapp.chat.ThreadDetailResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatApiService {

    @POST("api/chat/messages")
    Call<SendMessageResponse> sendMessage(@Header("Authorization") String token, @Body SendMessageRequest request);

    @GET("api/chat/thread")
    Call<ThreadDetailResponse> getCustomerThread(@Header("Authorization") String token);

    @GET("api/chat/threads")
    Call<List<Thread>> getThreads(@Header("Authorization") String token);

    @GET("api/chat/threads/{threadId}")
    Call<List<Message>> getAdminThreadMessages(@Header("Authorization") String token, @Path("threadId") int threadId);
}
