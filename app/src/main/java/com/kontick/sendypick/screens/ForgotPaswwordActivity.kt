package com.kontick.sendypick.screens

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.kontick.sendypick.R
import com.kontick.sendypick.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.resetPasswordButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim { it <= ' ' }
            if (email.isEmpty()) {
                binding.emailEditText.error = getString(R.string.write_an_email)
                binding.emailEditText.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailEditText.error = getString(R.string.incorrect_email)
                binding.emailEditText.requestFocus()
                return@setOnClickListener
            }
            binding.progressBar.visibility = View.VISIBLE
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        getString(R.string.details_sent_to_email),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Error! Try again!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.progressBar.visibility = View.GONE
            } }
    }

}