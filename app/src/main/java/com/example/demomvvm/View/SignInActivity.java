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

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        Button signInButton = findViewById(R.id.btnGoogleAuth);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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