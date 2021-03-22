package com.example.chatdeck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;

public class CreateDeckActivity extends AppCompatActivity {

    private static final String TAG = CreateDeckActivity.class.getSimpleName();
    Button btn_create;
    Button btn_back;
    EditText tv_deckId;
    EditText tv_deckName;
    EditText tv_deckDescription;
    EditText tv_deckIcon;

    String deck_ID;
    String deck_name;
    String deck_description;
    String deck_iconUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deck);

        Intent intent = getIntent();

        // region Get View elements
        btn_create = findViewById(R.id.btn_popupCreate);
        btn_back = findViewById(R.id.btn_popupBack);
        tv_deckId = findViewById(R.id.tv_popupDeckId);
        tv_deckName = findViewById(R.id.tv_popupDeckName);
        tv_deckDescription = findViewById(R.id.tv_popupDeckDescription);
        tv_deckIcon = findViewById(R.id.tv_popupDeckIcon);
        // end region

        btn_create.setOnClickListener(v -> createNewDeck());

        btn_back.setOnClickListener(v -> finish());

    }

    private void createNewDeck() {
        String password = "";

        deck_ID = tv_deckId.getText().toString();
        deck_name = tv_deckName.getText().toString();
        deck_description = tv_deckDescription.getText().toString();
        deck_iconUrl = tv_deckIcon.getText().toString();

        if(deck_ID.isEmpty() || deck_name.isEmpty()) {
            Toast.makeText(this, getString(R.string.warn_cannotempty), Toast.LENGTH_LONG).show();
        }
        else {
            Group deck = new Group(deck_ID, deck_name, CometChatConstants.GROUP_TYPE_PUBLIC, password);
            CometChat.createGroup(deck, new CometChat.CallbackListener<Group>() {
                @Override
                public void onSuccess(Group group) {
                    Log.d(TAG, "Group created successfully: " + group.toString());
                    DeckListActivity.start(CreateDeckActivity.this);
                }
                @Override
                public void onError(CometChatException e) {
                    Log.d(TAG, "Group creation failed with exception: " + e.getMessage());
                }
            });
        }
    }
}