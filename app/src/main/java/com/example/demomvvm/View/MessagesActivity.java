package com.example.demomvvm.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demomvvm.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class MessagesActivity extends AppCompatActivity {
    ImageView img_profile;
    private GoogleSignInClient mGoogleSignInClient;;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messageslist);
        img_profile = (ImageView) findViewById(R.id.img_profile);
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessagesActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
