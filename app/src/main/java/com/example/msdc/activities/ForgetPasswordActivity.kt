@file:Suppress("DEPRECATION")

package com.example.msdc.activities

import java.util.Objects
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import com.example.msdc.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase

class ForgetPasswordActivity : AppCompatActivity() {
    //view binding
    private var binding: ActivityForgetPasswordBinding? = null

    //firebase auth
    private lateinit var auth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog
    private var email = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        auth = Firebase.auth

        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(binding!!.root)

        //setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        binding!!.submitBtn.setOnClickListener { validateData() }
        binding!!.forgetPasswordToolbar.setOnClickListener { onBackPressed() }
    }

    private fun validateData() {
        //get data i.e. email
        email = Objects.requireNonNull(binding!!.emailEt.text).toString().trim { it <= ' ' }

        //validate data
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter Email...", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format...", Toast.LENGTH_SHORT).show()
        } else {
            recoverPassword()
        }
    }

    private fun recoverPassword() {
        //show progress
        progressDialog.setMessage("Sending password recovery instructions to $email")
        progressDialog.show()

        //begin sending recovery
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@ForgetPasswordActivity,
                        "Instructions to reset password sent to $email",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, "Email sent.")
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@ForgetPasswordActivity,
                        "Failed to sent email",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.w(TAG, "Failed to Send Email.")
                }
            }
    }
}