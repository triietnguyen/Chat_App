package com.example.demomvvm.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.demomvvm.R;
import com.example.demomvvm.databinding.ActivitySignUpBinding;
import com.example.demomvvm.utilities.Constants;
import com.example.demomvvm.utilities.PreferenceManager;
import com.example.demomvvm.viewmodel.SignInViewModel;
import com.example.demomvvm.viewmodel.SignUpViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    public String encodedImage;
    private SignUpViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        binding.setSignUpViewModel(viewModel);
        binding.setLifecycleOwner(this);
        preferenceManager = new PreferenceManager(getApplicationContext());
        // Theo dõi LiveData để cập nhật trạng thái của ProgressBar
        viewModel.getProgressBarVisible().observe(this, isVisible -> {
            if (isVisible) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });

        // Theo dõi LiveData để cập nhật trạng thái của Nút Button
        viewModel.getButtonVisible().observe(this, isVisible -> {
            if (isVisible) {
                binding.btnSignup.setVisibility(View.VISIBLE);
            } else {
                binding.btnSignup.setVisibility(View.INVISIBLE);
            }
        });
        setListeners();
    }

    private void setListeners() {
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidSignUpDetails()){
                    viewModel.onSignUpButtonClick(getApplicationContext(),encodedImage);
                }
            }
        });
        binding.layoutImage.setOnClickListener(v -> viewModel.setImage(pickImage));
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewHeight, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private boolean isValidSignUpDetails(){
        if(encodedImage == null){
            showToast("Select profile image");
            return false;
        }
        else if (viewModel.name.toString().trim().isEmpty()) {
            showToast("Enter name");
            return false;
        } else if (viewModel.email.toString().trim().isEmpty()) {
            showToast("Enter email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(viewModel.email.toString()).matches()) {
            showToast("Enter valid email");
            return false;
        } else if (viewModel.password.toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        } else if (viewModel.confirm_password.toString().trim().isEmpty()) {
            showToast("Confirm your password");
            return false;
        } else if (!viewModel.password.toString().equals(viewModel.confirm_password.toString())) {
            showToast("Password & confirm password must be same");
            return false;
        }
        else {
            return true;
        }
    }
}