package com.example.demomvvm.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demomvvm.View.MainActivity;
import com.example.demomvvm.utilities.Constants;
import com.example.demomvvm.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInViewModel extends ViewModel {
    public String email = "";
    public String password = "";
    private MutableLiveData<Boolean> progressBarVisible = new MutableLiveData<>(false);
    public LiveData<Boolean> getProgressBarVisible() {
        return progressBarVisible;
    }
    private MutableLiveData<Boolean> buttonVisible = new MutableLiveData<>(true);

    public LiveData<Boolean> getButtonVisible() {
        return buttonVisible;
    }

    public void onSignInButtonClick(Context context) {
        if(isValidSignInDetail(context)){
            PreferenceManager preferenceManager = new PreferenceManager(context);
            FirebaseFirestore database = FirebaseFirestore.getInstance();

            progressBarVisible.setValue(true); // Hiển thị ProgressBar
            buttonVisible.setValue(false); // Ẩn Nút Button

            database.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_EMAIL, email)
                    .whereEqualTo(Constants.KEY_PASSWORD, password)
                    .get()
                    .addOnCompleteListener(task -> {
                        progressBarVisible.setValue(false); // Ẩn ProgressBar
                        buttonVisible.setValue(true); // Hiển thị Nút Button

                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                            preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                            preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        } else {
                            showToast("Unable to sign in", context);
                        }
                    });
            }

    }

    private void showToast(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetail(Context context) {
        if (email.trim().isEmpty()) {
            showToast("Enter email", context);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Enter valid email", context);
            return false;
        } else if (password.trim().isEmpty()) {
            showToast("Enter password", context);
            return false;
        } else {
            return true;
        }
    }
}


