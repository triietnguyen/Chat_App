package com.example.demomvvm.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.example.demomvvm.Model.ChatMessage;
import com.example.demomvvm.View.ProfileActivity;
import com.example.demomvvm.View.UsersActivity;
import com.example.demomvvm.adapters.RecentConversationsAdapter;
import com.example.demomvvm.databinding.ActivityMainBinding;
import com.example.demomvvm.utilities.Constants;
import com.example.demomvvm.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
public class MainViewModel extends ViewModel {

    public void handleProfileImageClick(Context context) {
        context.startActivity(new Intent(context, ProfileActivity.class));
    }

    public void handleNewChatButtonClick(Context context) {
        context.startActivity(new Intent(context, UsersActivity.class));
    }
    public void loadUserDetails(ActivityMainBinding binding, PreferenceManager preferenceManager){
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.imgProfile.setImageBitmap(bitmap);
    }
    public void listenConversations(FirebaseFirestore database, PreferenceManager preferenceManager, EventListener<QuerySnapshot> eventListener) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
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
