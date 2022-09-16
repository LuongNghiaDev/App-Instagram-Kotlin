package com.example.appinstagramremakekotlin.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.appinstagramremakekotlin.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpLogin.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.signInLogin.setOnClickListener {
            loginUser()
        }

    }

    private fun loginUser() {
        val email = binding.emailLogin.text.toString()
        val password = binding.passLogin.text.toString()

        when{
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is required", Toast.LENGTH_LONG).show()

            else -> {
                val progressDiaglog = ProgressDialog(this@SignInActivity)
                progressDiaglog.setTitle("Login")
                progressDiaglog.setMessage("Please wait, this may take a while...")
                progressDiaglog.setCanceledOnTouchOutside(false)
                progressDiaglog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            progressDiaglog.dismiss()

                            val intent = Intent(this@SignInActivity, MainActivity::class.java )
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            val message = task.exception!!.toString()
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                            FirebaseAuth.getInstance().signOut()
                            progressDiaglog.dismiss()
                        }
                    }
            }
        }

    }

    override fun onStart() {
        super.onStart()

        if(FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this@SignInActivity, MainActivity::class.java )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

}