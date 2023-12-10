package com.example.demomvvm.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.demomvvm.R;
import com.example.demomvvm.databinding.ActivitySignInBinding;
import com.example.demomvvm.viewmodel.SignInViewModel;
import com.example.demomvvm.utilities.Constants;
import com.example.demomvvm.utilities.PreferenceManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    FirebaseAuth auth;
    FirebaseDatabase database;
    private GoogleSignInClient mGoogleSigninClient;
    int RC_SIGN_IN = 20;
    private SignInViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
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
    }

//    private void ConnectFirebase() {
//        auth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail().build();
//        mGoogleSigninClient = GoogleSignIn.getClient(this, gso);
//        if (auth.getCurrentUser() != null) {
//            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }
//
//
//
//    private void addDataToFirestore(){
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("first_name", "Chirag");
//        data.put("last_name", "Kachhadiva");
//        database.collection("users")
//                .add(data)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(getApplicationContext(), "Data Inserted", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(exception -> {
//                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//
//    }
//    private void googleSignIn() {
//        Intent intent = mGoogleSigninClient.getSignInIntent();
//        startActivityForResult(intent, RC_SIGN_IN);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuth(account.getIdToken());
//            } catch (Exception e) {
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(SignInActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void firebaseAuth(String idToken) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//        auth.signInWithCredential(credential)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            FirebaseUser user = auth.getCurrentUser();
//                            HashMap<String, Object> map = new HashMap<>();
//                            map.put("id", user.getUid());
//                            map.put("name", user.getDisplayName());
//                            map.put("profile", user.getPhotoUrl().toString());
//
//                            database.getReference().child("user").child(user.getUid()).setValue(map);
//                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//                            startActivity(intent);
//                        } else {
//                            Toast.makeText(SignInActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                });
//    }
}