package com.kontick.sendypick.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.kontick.sendypick.R
import com.kontick.sendypick.data.user.UserCreator
import com.kontick.sendypick.data.user.User
import com.kontick.sendypick.data.user.UserViewModel
import com.kontick.sendypick.databinding.FragmentSignupTabBinding
import com.kontick.sendypick.domain.models.authFragments.LoginTabFragmentViewModel
import com.kontick.sendypick.domain.models.authFragments.SignupTabFragmentViewModel
import com.kontick.sendypick.screens.JoinCreateActivity
import com.kontick.sendypick.utils.RoomDatabaseUtils

class SignupTabFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var signupTabViewModel: SignupTabFragmentViewModel
    private var _binding: FragmentSignupTabBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupTabBinding.inflate(inflater, container, false)
        val rootView: View = binding.root
        signupTabViewModel = ViewModelProvider(this).get(SignupTabFragmentViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        binding.registerButton.setOnClickListener {
            registerUser()
        }
        return rootView

    }

    private fun registerUser() {
        val fullName = binding.fullNameEditText.text.toString().trim { it <= ' ' }
        val phone = binding.phoneEditText.text.toString().trim { it <= ' ' }
        val email = binding.emailEditText.text.toString().trim { it <= ' ' }
        val password = binding.passwordEditText.text.toString().trim { it <= ' ' }
        if (fullName.isEmpty()) {
            binding.fullNameEditText.error = getString(R.string.write_a_name)
            binding.fullNameEditText.requestFocus()
            return
        }
        if (phone.isEmpty()) {
            binding.phoneEditText.error = getString(R.string.write_a_phone_number)
            binding.phoneEditText.requestFocus()
            return
        }
        if (email.isEmpty()) {
            binding.emailEditText.error = getString(R.string.write_an_email)
            binding.emailEditText.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = getString(R.string.incorrect_email)
            binding.emailEditText.requestFocus()
            return
        }
        if (password.isEmpty()) {
            binding.passwordEditText.error = getString(R.string.write_a_password)
            binding.passwordEditText.requestFocus()
            return
        }
        if (password.length < 6) {
            binding.passwordEditText.error = getString(R.string.min_length_is_6_symbols)
            binding.passwordEditText.requestFocus()
            return
        }
        binding.spinKit.visibility = View.VISIBLE
        signupTabViewModel.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User.Base(
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        fullName,
                        phone,
                        email,
                        "None",
                        "None"
                    )
                    signupTabViewModel.createUser(user).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            RoomDatabaseUtils.insertDataToDatabase(userViewModel, user)
                            Toast.makeText(context, "Account Created...", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(context, JoinCreateActivity::class.java))
                        } else {
                            Toast.makeText(context, "Error! Try again!", Toast.LENGTH_SHORT).show()
                            binding.spinKit.visibility = View.GONE
                        }
                    }
                } else {
                    Toast.makeText(context, "Error! Try again!", Toast.LENGTH_SHORT).show()
                    binding.spinKit.visibility = View.GONE
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}