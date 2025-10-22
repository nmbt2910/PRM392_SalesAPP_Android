package com.prm392.salesapp.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.prm392.salesapp.R;
import com.prm392.salesapp.viewmodel.ChatViewModel;

import java.util.ArrayList;

public class ChatFragment extends Fragment implements ThreadAdapter.OnThreadClickListener {

    private ChatViewModel chatViewModel;
    private RecyclerView threadsRecyclerView;
    private RecyclerView messagesRecyclerView;
    private ThreadAdapter threadAdapter;
    private MessageAdapter messageAdapter;
    private ProgressBar progressBar;
    private Group chatViewGroup;
    private TextInputEditText messageInputEditText;
    private Button sendButton;
    private TextView welcomeMessageTextView;
    private AppBarLayout appBarLayout;
    private MaterialToolbar toolbar;
    private TextView toolbarTitle;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;

    private String authToken;
    private boolean isAdmin;
    private int currentUserId;
    private Integer threadId = null;

    private OnBackPressedCallback onBackPressedCallback;
    private boolean isNavigatingBack = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SalesAppPrefs", Context.MODE_PRIVATE);
        authToken = sharedPreferences.getString("AUTH_TOKEN", null);
        isAdmin = sharedPreferences.getBoolean("IS_ADMIN", false);
        currentUserId = sharedPreferences.getInt("USER_ID", -1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        threadsRecyclerView = view.findViewById(R.id.threads_recycler_view);
        messagesRecyclerView = view.findViewById(R.id.messages_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        chatViewGroup = view.findViewById(R.id.chat_view_group);
        messageInputEditText = view.findViewById(R.id.message_input_edit_text);
        sendButton = view.findViewById(R.id.send_button);
        welcomeMessageTextView = view.findViewById(R.id.welcome_message_text_view);
        appBarLayout = view.findViewById(R.id.app_bar_layout);
        toolbar = view.findViewById(R.id.toolbar);
        toolbarTitle = view.findViewById(R.id.toolbar_title);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        searchView = view.findViewById(R.id.search_view);

        threadsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        threadAdapter = new ThreadAdapter(new ArrayList<>(), this);
        threadsRecyclerView.setAdapter(threadAdapter);

        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageAdapter = new MessageAdapter(new ArrayList<>(), currentUserId);
        messagesRecyclerView.setAdapter(messageAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        observeViewModel();

        if (isAdmin) {
            toolbarTitle.setText("Chats");
            toolbar.setNavigationIcon(null);
            appBarLayout.setVisibility(View.VISIBLE);
            chatViewGroup.setVisibility(View.GONE);
            threadsRecyclerView.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.VISIBLE);
            chatViewModel.fetchThreads(authToken);
        } else {
            appBarLayout.setVisibility(View.GONE);
            threadsRecyclerView.setVisibility(View.GONE);
            chatViewGroup.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
            chatViewModel.fetchCustomerThread(authToken);
        }

        sendButton.setOnClickListener(v -> {
            String message = messageInputEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                chatViewModel.sendMessage(authToken, threadId, message);
                messageInputEditText.setText("");
            }
        });

        toolbar.setNavigationOnClickListener(v -> {
            handleAdminBackNavigation();
        });

        onBackPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                handleAdminBackNavigation();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);

        swipeRefreshLayout.setOnRefreshListener(() -> chatViewModel.fetchThreads(authToken));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                threadAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdmin) {
            chatViewModel.fetchThreads(authToken);
        }
    }

    private void handleAdminBackNavigation() {
        if (isNavigatingBack) return;

        isNavigatingBack = true;
        hideKeyboard();
        Animation slideOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
        chatViewGroup.startAnimation(slideOut);
        slideOut.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {
                chatViewGroup.setVisibility(View.GONE);
                toolbarTitle.setText("Chats");
                toolbar.setNavigationIcon(null);
                threadsRecyclerView.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
                chatViewModel.fetchThreads(authToken);
                Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
                threadsRecyclerView.startAnimation(slideIn);
                onBackPressedCallback.setEnabled(false);
                isNavigatingBack = false;
            }
            @Override public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void observeViewModel() {
        chatViewModel.getThreads().observe(getViewLifecycleOwner(), threads -> {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            if (threads != null && !threads.isEmpty()) {
                threadsRecyclerView.setVisibility(View.VISIBLE);
                threadAdapter.updateThreads(threads);
            }
        });

        chatViewModel.getCustomerThread().observe(getViewLifecycleOwner(), response -> {
            progressBar.setVisibility(View.GONE);
            if (response.isNewUser()) {
                welcomeMessageTextView.setVisibility(View.VISIBLE);
                messagesRecyclerView.setVisibility(View.GONE);
                messageAdapter.updateMessages(new ArrayList<>());
                threadId = null;
            } else {
                welcomeMessageTextView.setVisibility(View.GONE);
                messagesRecyclerView.setVisibility(View.VISIBLE);
                threadId = response.getThreadId();
                messageAdapter.updateMessages(response.getMessages());
                messagesRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });

        chatViewModel.getAdminThreadMessages().observe(getViewLifecycleOwner(), messages -> {
            progressBar.setVisibility(View.GONE);
            if (messages != null) {
                messageAdapter.updateMessages(messages);
                messagesRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);

                Animation slideOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
                threadsRecyclerView.startAnimation(slideOut);
                slideOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {
                        threadsRecyclerView.setVisibility(View.GONE);
                        searchView.setVisibility(View.GONE);
                        toolbarTitle.setText("Thread");
                        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                        chatViewGroup.setVisibility(View.VISIBLE);
                        onBackPressedCallback.setEnabled(true);
                        Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
                        chatViewGroup.startAnimation(slideIn);
                    }
                    @Override public void onAnimationRepeat(Animation animation) {}
                });
            }
        });

        chatViewModel.getSendMessageResponse().observe(getViewLifecycleOwner(), sendMessageResponse -> {
            welcomeMessageTextView.setVisibility(View.GONE);
            messagesRecyclerView.setVisibility(View.VISIBLE);
            if (threadId == null) {
                threadId = sendMessageResponse.getThreadId();
            }
            if (isAdmin) {
                chatViewModel.fetchAdminThreadMessages( authToken, threadId);
            } else {
                chatViewModel.fetchCustomerThread(authToken);
            }
        });

        chatViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onThreadClick(Thread thread) {
        this.threadId = thread.getThreadId();
        chatViewModel.fetchAdminThreadMessages(authToken, threadId);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
