@file:Suppress("DEPRECATION")

package com.example.msdc.activities

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.msdc.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Objects

class RegisterActivity : AppCompatActivity() {
    private lateinit var gsc: GoogleSignInClient

    private var binding: ActivityRegisterBinding? = null

    private lateinit var auth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog
    private var username = ""
    private var email = ""
    private var password = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth

        binding = ActivityRegisterBinding.inflate(layoutInflater)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(binding!!.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Creating your account...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding!!.registerBtn.setOnClickListener { validateData() }
        binding!!.clearBtn.setOnClickListener { clear() }

        binding!!.googleBtn.setOnClickListener { googleSignIn() }
        binding!!.registerToolbar.setOnClickListener { onBackPressed() }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //got ot previous activity when back button of actionbar clicked
        return super.onSupportNavigateUp()
    }

    private fun validateData() {
        username = Objects.requireNonNull(binding!!.nameEt.text).toString().trim { it <= ' ' }
        email = Objects.requireNonNull(binding!!.emailEt.text).toString().trim { it <= ' ' }
        password = Objects.requireNonNull(binding!!.passwordEt.text).toString().trim { it <= ' ' }

        val passwordConfirmation = Objects.requireNonNull(
            binding!!.cPasswordEt.text
        ).toString().trim { it <= ' ' }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding!!.emailEt.error = "Invalid email format !!!"
        } else if (TextUtils.isEmpty(username)) {
            binding!!.nameEt.error = "Enter Name !!!"
        } else if (TextUtils.isEmpty(password)) {
            binding!!.passwordEt.error = "Enter password !!!"
        } else if (password.length < 8) {
            binding!!.passwordEt.error = "Password must at least 8 characters long !!!"
        } else if (password != passwordConfirmation) {
            binding!!.cPasswordEt.error = "Confirmation Password must same as Password !!!"
        } else {
            emailSignUp(email, password)
        }
    }

    private fun emailSignUp(email: String, password: String) {
        progressDialog.show()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if(task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this@RegisterActivity, "Account created\n$email", Toast.LENGTH_SHORT)
                        .show()

                    saveEmailAccount()
                    sendEmailVerification()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this@RegisterActivity, "Register Failed !!!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveEmailAccount() {
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
                    Log.d(ContentValues.TAG, "saveEmailAccount:success")

                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                } else {
                    Log.w(ContentValues.TAG, "saveEmailAccount:fail", task.exception)
                }
            }
    }

    private fun sendEmailVerification(){
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Email Verification Link Sent, Please Verify !!!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Email Verification Link Not Sent !!!", Toast.LENGTH_SHORT).show()
                }
            }
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

                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                } else {
                    Log.w(ContentValues.TAG, "saveGoogleAccount:fail", task.exception)
                }
            }
    }
}