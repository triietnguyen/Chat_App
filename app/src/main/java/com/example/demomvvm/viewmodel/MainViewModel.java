package com.example.demomvvm.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demomvvm.Model.ChatMessage;
import com.example.demomvvm.Model.User;
import com.example.demomvvm.View.ChatActivity;
import com.example.demomvvm.View.MainActivity;
import com.example.demomvvm.View.ProfileActivity;
import com.example.demomvvm.View.UsersActivity;
import com.example.demomvvm.adapters.RecentConversationsAdapter;
import com.example.demomvvm.databinding.ActivityMainBinding;
import com.example.demomvvm.listeners.ConversionListener;
import com.example.demomvvm.utilities.Constants;
import com.example.demomvvm.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// MainViewModel.java
public class MainViewModel extends ViewModel {

    public void handleProfileImageClick(Context context) {
        context.startActivity(new Intent(context, ProfileActivity.class));
    }

    public void handleNewChatButtonClick(Context context) {
        context.startActivity(new Intent(context, UsersActivity.class));
    }
    public void getToken(PreferenceManager preferenceManager, Context context) {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> updateToken(token, preferenceManager, context));
    }
    private void updateToken(String token, PreferenceManager preferenceManager, Context context) {
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Unable to update token",context));
    }
    private void showToast(String message, Context context){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
