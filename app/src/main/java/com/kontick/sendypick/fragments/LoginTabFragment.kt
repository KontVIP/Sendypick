package com.kontick.sendypick.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.kontick.sendypick.R
import com.kontick.sendypick.data.user.UserViewModel
import com.kontick.sendypick.databinding.FragmentLoginTabBinding
import com.kontick.sendypick.domain.models.authFragments.LoginTabFragmentViewModel
import com.kontick.sendypick.screens.ForgotPasswordActivity
import com.kontick.sendypick.screens.JoinCreateActivity
import com.kontick.sendypick.screens.MainActivity
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginTabFragment : Fragment(), View.OnClickListener {

    private lateinit var userViewModel: UserViewModel
    private var _binding: FragmentLoginTabBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginTabViewModel: LoginTabFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginTabBinding.inflate(inflater, container, false)
        val rootView: View = binding.root
        loginTabViewModel = ViewModelProvider(this).get(LoginTabFragmentViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        //Animation
        binding.emailEditText.translationY = 300f
        binding.passwordEditText.translationY = 300f
        binding.forgotPasswordTextView.translationY = 300f
        binding.loginButton.translationY = 300f
        binding.emailEditText.alpha = 0f
        binding.passwordEditText.alpha = 0f
        binding.forgotPasswordTextView.alpha = 0f
        binding.loginButton.alpha = 0f
        binding.emailEditText.animate().translationY(0f).alpha(1f).setDuration(800).setStartDelay(300).start()
        binding.passwordEditText.animate().translationY(0f).alpha(1f).setDuration(800).setStartDelay(500).start()
        binding.forgotPasswordTextView.animate().translationY(0f).alpha(1f).setDuration(800).setStartDelay(500).start()
        binding.loginButton.animate().translationY(0f).alpha(1f).setDuration(800).setStartDelay(700).start()
        Paper.init(context)
        binding.loginButton.setOnClickListener(this)
        binding.forgotPasswordTextView.setOnClickListener(this)
        return rootView
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.login_button -> {
                userLogin(
                    binding.emailEditText.text.toString().trim { it <= ' ' },
                    binding.passwordEditText.text.toString().trim { it <= ' ' }
                )
                Paper.book().write("Email", binding.emailEditText.text.toString().trim { it <= ' ' })
                Paper.book().write("Password", binding.passwordEditText.text.toString().trim { it <= ' ' })
            }
            R.id.forgot_password_text_view -> startActivity(
                Intent(
                    context,
                    ForgotPasswordActivity::class.java
                )
            )
        }
    }

    private fun userLogin(email: String, password: String) {
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
        binding.spinKit.visibility = View.VISIBLE
        loginTabViewModel.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loginTabViewModel.writeDataToDatabase(userViewModel, email)
                CoroutineScope(Dispatchers.IO).launch {
                    if (loginTabViewModel.isHasRoom(FirebaseAuth.getInstance().currentUser!!.uid)) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, getString(R.string.welcome), Toast.LENGTH_SHORT).show()
                            startActivity(Intent(context, MainActivity::class.java))
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, getString(R.string.welcome), Toast.LENGTH_SHORT).show()
                            startActivity(Intent(context, JoinCreateActivity::class.java))
                        }
                    }
                }
            } else
                Toast.makeText(context, getString(R.string.incorrect_login_or_password), Toast.LENGTH_SHORT).show()
            binding.spinKit.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
