package com.example.demomvvm.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.example.demomvvm.Model.User;
import com.example.demomvvm.View.MainActivity;
import com.example.demomvvm.adapters.UsersAdapter;
import com.example.demomvvm.databinding.ActivityUsersBinding;
import com.example.demomvvm.listeners.UserListener;
import com.example.demomvvm.utilities.Constants;
import com.example.demomvvm.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersViewModel extends ViewModel {

    public void onBack(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
