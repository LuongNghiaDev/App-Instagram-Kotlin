package com.example.appinstagramremakekotlin.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appinstagramremakekotlin.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpRegister.setOnClickListener {
            createAccount()
        }

        binding.signInRegister.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
        }

    }

    private fun createAccount() {
        val username = binding.usernameRegister.text.toString()
        val fullname = binding.fullnameRegister.text.toString()
        val email = binding.emailRegister.text.toString()
        val password = binding.passRegister.text.toString()

        when{
            TextUtils.isEmpty(username) -> Toast.makeText(this, "User name is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(fullname) -> Toast.makeText(this, "Full name is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is required", Toast.LENGTH_LONG).show()

            else -> {
                val progressDiaglog = ProgressDialog(this@SignUpActivity)
                progressDiaglog.setTitle("SignUp")
                progressDiaglog.setMessage("Please wait, this may take a while...")
                progressDiaglog.setCanceledOnTouchOutside(false)
                progressDiaglog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            saveUserInfo(username, fullname, email, progressDiaglog)

                        } else {
                            val message = task.exception!!.toString()
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDiaglog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(username: String, fullname: String, email: String, progress: ProgressDialog) {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserId
        userMap["username"] = username.toLowerCase()
        userMap["fullname"] = fullname.toLowerCase()
        userMap["email"] = email
        userMap["bio"] = "Hey I am using Nghia Instagram Clone App"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/app-instagram-clone-a4853.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=ddbc15cd-fbc3-4161-bdd7-91db41aa98d5"

        usersRef.child(currentUserId).setValue(userMap)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    progress.dismiss()
                    Toast.makeText(this, "Account has been created successfully", Toast.LENGTH_LONG).show()

                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(currentUserId)
                        .child("Following").child(currentUserId)
                        .setValue(true)

                    val intent = Intent(this@SignUpActivity, MainActivity::class.java )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    val message = task.exception!!.toString()
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progress.dismiss()
                }
            }
    }


}