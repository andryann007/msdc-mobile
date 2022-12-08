package com.example.msdc.activities;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.msdc.R;
import com.example.msdc.databinding.ActivityRegisterBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    GoogleSignInClient gsc;

    //view binding
    private ActivityRegisterBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    private String username="", email="", password="", password_confirmation="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Creating your account...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.registerBtn.setOnClickListener(view -> validateData());
        binding.clearBtn.setOnClickListener(view -> clear());
        binding.backBtn.setOnClickListener(view -> back());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        binding.googleBtn.setOnClickListener(v -> SignIn());
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //got ot previous activity when back button of actionbar clicked
        return super.onSupportNavigateUp();
    }

    private void validateData() {
        username = Objects.requireNonNull(binding.nameEt.getText()).toString().trim();
        email = Objects.requireNonNull(binding.emailEt.getText()).toString().trim();
        password = Objects.requireNonNull(binding.passwordEt.getText()).toString().trim();
        password_confirmation = Objects.requireNonNull(binding.cPasswordEt.getText()).toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.setError("Invalid email format !!!");
        }
        else if(TextUtils.isEmpty(username)){
            binding.nameEt.setError("Enter Name !!!");
        }
        else if(TextUtils.isEmpty(password)){
            binding.passwordEt.setError("Enter password !!!");
        }
        else if(password.length()<8){
            binding.passwordEt.setError("Password must at least 8 characters long !!!");
        }
        else if(!password.equals(password_confirmation)){
            binding.cPasswordEt.setError("Confirmation Password must same as Password !!!");
        }
        else
        {
            firebaseSignUp();
        }
    }
    private void firebaseSignUp() {
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    progressDialog.dismiss();

                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    assert firebaseUser != null;
                    String email = firebaseUser.getEmail();

                    updateUserInfo();
                    Toast.makeText(RegisterActivity.this,"Account created\n"+email, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Saving user info...");

        //timestamp
        long timestamp = System.currentTimeMillis();

        //get current user uid, since user is registered so we can get now
        String uid = firebaseAuth.getUid();

        //setup data to add in db
        HashMap<String, Object> mHashmap = new HashMap<>();
        mHashmap.put("uid", uid);
        mHashmap.put("email", email);
        mHashmap.put("name", username);
        mHashmap.put("profileImage", R.drawable.ic_account);
        mHashmap.put("timestamp", timestamp);

        //set data to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(mHashmap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //data added to db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Account created...", Toast.LENGTH_SHORT).show();
                        //since user account is created so start dashboard of user
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //data failed adding to db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void clear(){
        Objects.requireNonNull(binding.emailEt.getText()).clear();
        Objects.requireNonNull(binding.passwordEt.getText()).clear();
    }
    public void back(){
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
    private void SignIn(){
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                GoogleSignInAccount profileAccount = GoogleSignIn.getLastSignedInAccount(this);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuth.signInWithCredential(credential).addOnSuccessListener(authResult -> {
                    if(task.isSuccessful()){
                        assert profileAccount != null;
                        String email = profileAccount.getEmail();
                        Log.d(TAG, "signInWithCredential:success");

                        saveGoogleAccount(profileAccount);
                        Toast.makeText(RegisterActivity.this, "LoggedIn\n"+email, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
                task.getResult(ApiException.class);
            } catch (ApiException e){
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveGoogleAccount(GoogleSignInAccount user) {
        progressDialog.setMessage("Saving user info...");

        //timestamp
        long timestamp = System.currentTimeMillis();

        //get current user uid, since user is registered so we can get now
        assert user != null;
        String uid = firebaseAuth.getUid();
        String name = user.getDisplayName();
        String email = user.getEmail();
        String photo = String.valueOf(user.getPhotoUrl());

        //setup data to add in db
        HashMap<String, Object> mHashmap = new HashMap<>();
        mHashmap.put("uid", uid);
        mHashmap.put("email", email);
        mHashmap.put("name", name);
        mHashmap.put("profileImage", photo);
        mHashmap.put("timestamp", timestamp);

        //set data to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(mHashmap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //data added to db
                        progressDialog.dismiss();
                        //since user account is created so start dashboard of user
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //data failed adding to db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}