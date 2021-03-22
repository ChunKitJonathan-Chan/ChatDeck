package com.example.chatdeck;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.example.chatdeck.adapters.DeckListAdapter;
import com.example.chatdeck.utils.mImageLoader;

import java.util.List;
//Chun Kit Jonathan Chan

public class DeckListActivity extends AppCompatActivity {

    // region Field Member
    private static final String TAG = DeckListActivity.class.getSimpleName();

    RecyclerView deckListRecyclerView;
    DeckListAdapter mAdapter;
    TextView welcomeUsername;
    ImageView welcomeAvatar;
    User user;
    mImageLoader updateAvatar;

        // For popup dialog
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;

        EditText popUpCreateDeck_deckId;
        EditText popUpCreateDeck_deckName;
        EditText popUpCreateDeck_deckDescription;
        EditText popUpCreateDeck_deckIcon;
    //  Button popUpCreateDeck_create;
    //  Button popUpCreateDeck_back;
        // end popup dialog

    // end region

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_list);

        // region Get View element
        deckListRecyclerView = findViewById(R.id.deckRecyclerView);
        welcomeUsername = findViewById(R.id.tv_welcome_username);
        welcomeAvatar = findViewById(R.id.welcome_avatar);
        popUpCreateDeck_deckId = findViewById(R.id.tv_popupDeckId);
        popUpCreateDeck_deckName = findViewById(R.id.tv_popupDeckName);
        popUpCreateDeck_deckDescription = findViewById(R.id.tv_popupDeckDescription);
        popUpCreateDeck_deckIcon = findViewById(R.id.tv_popupDeckIcon);
//        popUpCreateDeck_create = findViewById(R.id.btn_popupCreate);
//        popUpCreateDeck_back = findViewById(R.id.btn_popupBack);
        // end region

        user = CometChat.getLoggedInUser();

        // region get and display User's name and avatar
        updateAvatar = new mImageLoader(user.getAvatar(), welcomeAvatar);
        updateAvatar.execute();
        welcomeUsername.setText(user.getName());
        // end region

        getDeckList();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, DeckListActivity.class);
        context.startActivity(starter);
    }

    // region Get all groups in the app (database) and display on recyclerView
    private void getDeckList() {
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder().build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List <Group> list) {
                Log.d(TAG, "Deck list fetched successfully: " + list.size());
                updateUI(list);
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Deck list fetching failed with exception: " + e.getMessage());
            }
        });
    }

    private void updateUI(List<Group> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(DeckListActivity.this);
        deckListRecyclerView.setLayoutManager(layoutManager);
        deckListRecyclerView.setHasFixedSize(true);
        mAdapter = new DeckListAdapter(list, DeckListActivity.this);
        deckListRecyclerView.setAdapter(mAdapter);
    }
    // end region

    // region Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.decklist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_deck:
                popUpCreateDeck();
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    // Create pop up form when user clicked on menu item: create_deck
    public void popUpCreateDeck() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popUp = getLayoutInflater().inflate(R.layout.activity_popup_create_deck, null);

        dialogBuilder.setView(popUp);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    // end region
}