package com.example.chatdeck;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
// Chun Kit Jonathan Chan
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    // region Key for using CometCat API (retrieve from https://app.cometchat.com/app/)
    private static final String AppId = "31100158c10559e";
    private static final String Auth_Key = "d6a8ebbee65144f4833962fafb28b13aa1251e24";
    private static final String CometCat_Region = "us";
    // end region

    // region Field Member
    EditText userLoginText;
    Button userLoginButton;
    // end region

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // region Get View element
        userLoginText =(EditText)findViewById(R.id.tv_username);
        userLoginButton =(Button)findViewById(R.id.btn_signIn);
        // end region

        initCometChat();
        userLogin();
    }

    // region Initialize CometChat API
    private void initCometChat() {
        AppSettings appSettings=new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(CometCat_Region).build();

        CometChat.init(this, AppId, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                Log.d(TAG, "CometChat initialization completed successfully");
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "CometChat initialization failed with exception: " + e.getMessage());
            }
        });
    }
    // end region

    // region Handle user login
    private void userLogin() {
        userLoginButton.setOnClickListener(v -> CometChat.login(userLoginText.getText().toString(), Auth_Key, new CometChat.CallbackListener<User>()
        {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "Login Successful : " + userLoginText.getText().toString());
                // direct user to DeckListActivity
                DeckListActivity.start(MainActivity.this);
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Login Fail!");
                Toast.makeText(MainActivity.this, R.string.login_fail, Toast.LENGTH_LONG);
            }
        }));
    }
    // end region
}