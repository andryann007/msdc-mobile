package com.example.msdc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.msdc.databinding.ActivityForgetPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgetPasswordActivity extends AppCompatActivity {
    //view binding
    private ActivityForgetPasswordBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    private String email="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.submitBtn.setOnClickListener(view -> validateData());
        binding.backBtn.setOnClickListener(view -> back());
    }

    private void validateData() {
        //get data i.e. email
        email = Objects.requireNonNull(binding.emailEt.getText()).toString().trim();

        //validate data
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter Email...", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format...", Toast.LENGTH_SHORT).show();
        }
        else {
            recoverPassword();
        }
    }

    private void recoverPassword() {
        //show progress
        progressDialog.setMessage("Sending password recovery instructions to "+email);
        progressDialog.show();

        //begin sending recovery
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    //sent
                    progressDialog.dismiss();
                    Toast.makeText(ForgetPasswordActivity.this, "Instructions to reset password sent to "+email, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    //failed to send
                    progressDialog.dismiss();
                    Toast.makeText(ForgetPasswordActivity.this, "Failed to send due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    public void back(){
        startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
        finish();
    }
}