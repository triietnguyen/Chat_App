package com.example.demomvvm.View;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.demomvvm.R;
import com.example.demomvvm.databinding.ActivitySignInBinding;
import com.example.demomvvm.viewmodel.SignInViewModel;
import com.example.demomvvm.utilities.Constants;
import com.example.demomvvm.utilities.PreferenceManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    private static final int RC_SIGN_IN = 1000;
    private SignInViewModel viewModel;
    GoogleSignInClient mGoogleSignInClient;
    Button signInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        viewModel = new ViewModelProvider(this).get(SignInViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        binding.setSignInViewModel(viewModel);
        binding.setLifecycleOwner(this);
        InitialGoogle();
        LiveData();
        AnhXa();
        signInButton();
    }
    private void signInButton(){
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }
    private void AnhXa(){
        // Set the dimensions of the sign-in button.
        signInButton = findViewById(R.id.btnGoogleAuth);
    }
    private void InitialGoogle(){
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void LiveData(){
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
                binding.btnSigin.setVisibility(View.VISIBLE);
            } else {
                binding.btnSigin.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    @SuppressLint("RestrictedApi")
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            PreferenceManager preferenceManager = new PreferenceManager(this);
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personPhoto = "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";
            // Signed in successfully, show authenticated UI.
            Toast.makeText(this, "Successfully", Toast.LENGTH_SHORT).show();
            loading(true);
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_EMAIL, personEmail)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                            loading(false);
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                            preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                            preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                            preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            this.startActivity(intent);
                        } else {
                            FirebaseFirestore database_1 = FirebaseFirestore.getInstance();
                            HashMap<String, Object> user = new HashMap<>();
                            user.put(Constants.KEY_NAME, personName.toString());
                            user.put(Constants.KEY_EMAIL, personEmail.toString());
                            user.put(Constants.KEY_PASSWORD, null);
                            user.put(Constants.KEY_IMAGE, personPhoto.toString());
                            database_1.collection(Constants.KEY_COLLECTION_USERS)
                                    .add(user)
                                    .addOnSuccessListener(documentReference -> {
                                        loading(false);
                                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                        preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                                        preferenceManager.putString(Constants.KEY_NAME, personName.toString());
                                        preferenceManager.putString(Constants.KEY_IMAGE, personPhoto.toString());
                                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(exception -> {
                                        loading(false);
                                        Toast.makeText(this, "Unable to sign in", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    });

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.btnSignup.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.btnSignup.setVisibility(View.VISIBLE);
        }
    }

}