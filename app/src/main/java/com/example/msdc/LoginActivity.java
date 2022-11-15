package com.example.msdc;

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

import com.example.msdc.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    //view binding
    private ActivityLoginBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    private String email = "", password = "";

    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Logging In");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        binding.loginBtn.setOnClickListener(view -> validateData());
        binding.forgotTv.setOnClickListener(view -> forget_password());
        binding.noAccountTv.setOnClickListener(view -> register());
        binding.clearBtn.setOnClickListener(view -> clear());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        binding.googleBtn.setOnClickListener(v -> SignIn());
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void validateData() {
        email = Objects.requireNonNull(binding.emailEt.getText()).toString().trim();
        password = Objects.requireNonNull(binding.passwordEt.getText()).toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEt.setError("Invalid email format !!!");
        } else if (TextUtils.isEmpty(password)) {
            binding.passwordEt.setError("Enter password !!!");
        } else {
            firebaseLogin();
        }
    }

    private void firebaseLogin() {
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    assert firebaseUser != null;
                    String email = firebaseUser.getEmail();

                    Toast.makeText(LoginActivity.this, "LoggedIn\n" + email, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void register() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }

    private void forget_password() {
        startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
        finish();
    }

    public void clear() {
        Objects.requireNonNull(binding.emailEt.getText()).clear();
        Objects.requireNonNull(binding.passwordEt.getText()).clear();
    }

    private void SignIn() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                GoogleSignInAccount profileAccount = GoogleSignIn.getLastSignedInAccount(this);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuth.signInWithCredential(credential).addOnSuccessListener(authResult -> {
                    if (task.isSuccessful()) {
                        assert profileAccount != null;
                        String email = profileAccount.getEmail();
                        Log.d(TAG, "signInWithCredential:success");
                        HomeMenu();
                        Toast.makeText(LoginActivity.this, "LoggedIn\n" + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
                task.getResult(ApiException.class);
            } catch (ApiException e) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void HomeMenu() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
