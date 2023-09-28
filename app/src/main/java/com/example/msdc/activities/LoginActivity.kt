@file:Suppress("DEPRECATION")

package com.example.msdc.activities

import android.util.Log
import android.util.Patterns

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.app.ProgressDialog
import android.view.WindowManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import android.text.TextUtils
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import android.content.ContentValues
import android.content.Intent
import com.example.msdc.databinding.ActivityLoginBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Objects

class LoginActivity : AppCompatActivity() {
    //view binding
    private var binding: ActivityLoginBinding? = null

    //firebase auth
    private lateinit var auth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog
    private var email = ""
    private var password = ""
    private lateinit var gsc: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Logging In")
        progressDialog.setCanceledOnTouchOutside(false)

        checkUser()

        binding!!.loginBtn.setOnClickListener { validateData() }
        binding!!.forgotTv.setOnClickListener { forgetPassword() }
        binding!!.noAccountTv.setOnClickListener { register() }
        binding!!.clearBtn.setOnClickListener { clear() }

        binding!!.googleBtn.setOnClickListener { googleSignIn() }
    }

    private fun checkUser() {
        val user = Firebase.auth.currentUser

        if (user != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun validateData() {
        email = Objects.requireNonNull(binding!!.emailEt.text).toString().trim { it <= ' ' }
        password = Objects.requireNonNull(binding!!.passwordEt.text).toString().trim { it <= ' ' }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding!!.emailEt.error = "Invalid email format !!!"
        } else if (TextUtils.isEmpty(password)) {
            binding!!.passwordEt.error = "Enter password !!!"
        } else {
            emailSignIn(email, password)
        }
    }

    private fun emailSignIn(email: String, password: String) {
        progressDialog.show()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    updateUserInfo()

                    Toast.makeText(this@LoginActivity, "LoggedIn\n$email", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this@LoginActivity, "Login Failed !!!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUserInfo() {
        val timestamp : Long = System.currentTimeMillis()
        val uid : String = auth.currentUser!!.uid

        val mHashMap = hashMapOf<String, Any>()
        mHashMap["timestamp"] = timestamp.toString()

        val database = Firebase.database
        val ref = database.getReference("Users").child(uid)

        ref.updateChildren(mHashMap)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Log.d(ContentValues.TAG, "updateUserInfo:success")

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Log.w(ContentValues.TAG, "updateUserInfo:fail", task.exception)
                }
            }
    }

    private fun register() {
        startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        finish()
    }

    private fun forgetPassword() {
        startActivity(Intent(this@LoginActivity, ForgetPasswordActivity::class.java))
        finish()
    }

    private fun clear() {
        Objects.requireNonNull(binding!!.emailEt.text?.clear())
        Objects.requireNonNull(binding!!.passwordEt.text?.clear())
    }

    private fun googleSignIn() {
        val intent = gsc.signInIntent
        startActivityForResult(intent, 100)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
                account.idToken?.let { firebaseAuthWithGoogle(it) }
            } catch (e: ApiException) {
                Log.w(ContentValues.TAG, "Google Sign In Failed:", e)
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    Log.d(ContentValues.TAG, "signInWithCredential:success")

                    saveGoogleAccount()
                } else {
                    Log.d(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun saveGoogleAccount() {
        val timestamp : Long = System.currentTimeMillis()
        val uid : String = auth.currentUser!!.uid
        val name : String = auth.currentUser!!.displayName.toString()
        val email : String = auth.currentUser!!.email.toString()
        val photoUrl : String = auth.currentUser!!.photoUrl.toString()

        val mHashMap = hashMapOf<String, Any>()

        mHashMap["uid"] = uid
        mHashMap["name"] = name
        mHashMap["email"] = email
        mHashMap["profileImage"] = photoUrl
        mHashMap["timestamp"] = timestamp.toString()

        val database = Firebase.database
        val ref = database.getReference("Users").child(uid)

        ref.setValue(mHashMap)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Log.d(ContentValues.TAG, "saveGoogleAccount:success")

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Log.w(ContentValues.TAG, "saveGoogleAccount:fail", task.exception)
                }
            }
    }
}