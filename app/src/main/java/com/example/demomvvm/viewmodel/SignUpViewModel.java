package com.example.demomvvm.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demomvvm.View.MainActivity;
import com.example.demomvvm.View.SignInActivity;
import com.example.demomvvm.View.SignUpActivity;
import com.example.demomvvm.utilities.Constants;
import com.example.demomvvm.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpViewModel extends ViewModel {
    public String name = "" ;
    public String email = "";
    public String password = "";
    public String confirm_password = "";
    private MutableLiveData<Boolean> progressBarVisible = new MutableLiveData<>(false);
    public LiveData<Boolean> getProgressBarVisible() {
        return progressBarVisible;
    }
    private MutableLiveData<Boolean> buttonVisible = new MutableLiveData<>(true);

    public LiveData<Boolean> getButtonVisible() {
        return buttonVisible;
    }
    public void onSignInButtonClick(Context context){
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
    }

    private void showToast(String message, Context context){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public void setImage(ActivityResultLauncher<Intent> pickImage) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
    }
    private boolean isValidSignUpDetails(Context context,String encodedImage ){
        if(encodedImage == null){
            showToast("Select profile image",context);
            return false;
        }
        else if (name.toString().trim().isEmpty()) {
            showToast("Enter name",context);
            return false;
        } else if (email.toString().trim().isEmpty()) {
            showToast("Enter email",context);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()) {
            showToast("Enter valid email",context);
            return false;
        } else if (password.toString().trim().isEmpty()) {
            showToast("Enter password",context);
            return false;
        } else if (confirm_password.toString().trim().isEmpty()) {
            showToast("Confirm your password",context);
            return false;
        } else if (!password.toString().equals(confirm_password.toString())) {
            showToast("Password & confirm password must be same",context);
            return false;
        }
        else {
            return true;
        }
    }
    public void onSignUpButtonClick(Context context, String encodedImage){
        if(isValidSignUpDetails(context, encodedImage)){
            loading(true);
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            PreferenceManager preferenceManager = new PreferenceManager(context);
            HashMap<String, Object> user = new HashMap<>();
            user.put(Constants.KEY_NAME, name.toString());
            user.put(Constants.KEY_EMAIL, email.toString());
            user.put(Constants.KEY_PASSWORD, password.toString());
            user.put(Constants.KEY_IMAGE, encodedImage);
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .add(user)
                    .addOnSuccessListener(documentReference -> {
                        loading(false);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                        preferenceManager.putString(Constants.KEY_NAME, name.toString());
                        preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
                        Intent intent = new Intent(context, SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    })
                    .addOnFailureListener(exception ->{
                        loading(false);
                        showToast(exception.getMessage(), context);
                    });
        }
    }
    private void loading(Boolean isLoading){
        if(isLoading){
            progressBarVisible.setValue(true); // Hiển thị ProgressBar
            buttonVisible.setValue(false); // Ẩn Nút Button
        }else{
            progressBarVisible.setValue(false); // Ẩn ProgressBar
            buttonVisible.setValue(true); // Hiển thị Nút Button
        }
    }


}


