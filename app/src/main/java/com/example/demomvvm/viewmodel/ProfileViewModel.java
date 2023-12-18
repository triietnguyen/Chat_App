package com.example.demomvvm.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.example.demomvvm.View.MainActivity;
import com.example.demomvvm.View.ProfileActivity;
import com.example.demomvvm.View.UsersActivity;
import com.example.demomvvm.databinding.ActivityMainBinding;
import com.example.demomvvm.utilities.Constants;
import com.example.demomvvm.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

// MainViewModel.java
public class ProfileViewModel extends ViewModel {
    public void BackMain(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
