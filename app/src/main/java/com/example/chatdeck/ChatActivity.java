package com.example.chatdeck;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.example.chatdeck.models.MessageRetriever;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.List;
// Chun Kit Jonathan Chan
public class ChatActivity extends AppCompatActivity {

    // Field Members
    private static final String TAG = ChatActivity.class.getSimpleName();
    MessageInput messageInputView;
    MessagesList messageListView;

    MessagesListAdapter<IMessage> messageAdapter;
    List<IMessage> messageHistorylist;

    private String mDeckID;
    private String senderId;
    private String listenerID;
    // end region

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // region Get View elements
        messageInputView = findViewById(R.id.input);
        messageListView = findViewById(R.id.messagesList);
        // end region

        Intent intent = getIntent();
        if (intent != null) {
            mDeckID = intent.getStringExtra(String.valueOf(R.string.extra_deckID));
        }

        senderId = CometChat.getLoggedInUser().getUid();
        // Required by CometChat: User must join in deck (group) to send/receive message
        joinDeck();
    }

    public static void start(Context context, String deckID) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra(String.valueOf(R.string.extra_deckID), deckID);
        context.startActivity(starter);
    }

    // Refresh the previous page (DeckListActivity) when user clicked the 'back' button
    @Override
    public void onBackPressed() {
        DeckListActivity.start(ChatActivity.this);
    }

    // region Join user to deck (group)
    private void joinDeck() {
        String password = "";
        CometChat.joinGroup(mDeckID, CometChatConstants.GROUP_TYPE_PUBLIC, password, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group joinedGroup) {
                Log.d(TAG, joinedGroup.toString());
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Group joining failed with exception: " + e.getMessage());
            }
        });
    // end region

        initMessageHistory();
        initMessageInput();
        initMessageReceive();
    }

    // region Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chatlogout:
                userLogout();
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    private void userLogout() {
        CometChat.logout(new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                Log.d(TAG, "Logout completed successfully");
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Logout failed with exception: " + e.getMessage());
            }
        });
    }
    // end region

    // region Get and display chat history
    private void initMessageHistory() {
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder().setGUID(mDeckID).build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> list) {
                messageHistorylist = new ArrayList<>();
                for (BaseMessage message : list) {
                    if (message instanceof TextMessage) {
                        Log.d(TAG, "Text message history received successfully: " + ((TextMessage) message).toString());
                        messageHistorylist.add(new MessageRetriever((TextMessage) message));
                    } else if (message instanceof MediaMessage) {
                        Log.d(TAG, "Media message history received successfully: " + ((MediaMessage) message).toString());
                    }
                }
                // method provided by ChatKit API: add message to the top of the view
                messageAdapter.addToEnd(messageHistorylist, true);
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Message history fetching failed with exception: " + e.getMessage());
            }
        });
    }
    // end region

    // region Handle message inputting and sender's avatar
    private void initMessageInput() {
        messageInputView.setInputListener(input -> {
            sendMessage(input.toString());
            return true;
        });

        /*messageInputView.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {
                sendMediaMessage();
            }
        });*/

        // region sender's avatar
        ImageLoader imageLoader = (imageView, url, payload) -> Picasso.get().load(url).into(imageView);
        messageAdapter = new MessagesListAdapter<IMessage>(senderId, imageLoader);
        messageListView.setAdapter(messageAdapter);
        // end regioin
    }
    // end region

    // region Handle message receiving
    private void initMessageReceive() {
        listenerID = "listener1";
        CometChat.addMessageListener(listenerID, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage textMessage) {
                Log.d(TAG, "Text message received successfully: " + textMessage.toString());
                addMessage(textMessage);
            }

            @Override
            public void onMediaMessageReceived(MediaMessage mediaMessage) {
                Log.d(TAG, "Media message received successfully: " + mediaMessage.toString());
            }

            @Override
            public void onCustomMessageReceived(CustomMessage customMessage) {
                Log.d(TAG, "Custom message received successfully: " + customMessage.toString());
            }
        });
    }
    // end region

    // region Handle message sending
    private void sendMessage(String message) {
        TextMessage textMessage = new TextMessage(mDeckID, message, CometChatConstants.RECEIVER_TYPE_GROUP);

        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                Log.d(TAG, "Message sent successfully: " + textMessage.toString());
                addMessage(textMessage);
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Message sending failed with exception: " + e.getMessage());
            }
        });
    }

    /*private void sendMediaMessage() {
        MediaMessage mediaMessage = new MediaMessage(mDeskID ,new File(filePath), CometChatConstants.MESSAGE_TYPE_IMAGE, CometChatConstants.RECEIVER_TYPE_USER);

        CometChat.sendMediaMessage(mediaMessage, new CometChat.CallbackListener<MediaMessage>() {
            @Override
            public void onSuccess(MediaMessage mediaMessage) {
                Log.d(TAG, "Media message sent successfully: " + mediaMessage.toString());
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Media message sending failed with exception: " + e.getMessage());
            }
        });
    }*/
    // end region

    private void addMessage(TextMessage textMessage) {
        boolean scroll = true;
        //  method provided by ChatKit API: display message at the bottom of the view
        messageAdapter.addToStart(new MessageRetriever(textMessage), scroll);
    }
}