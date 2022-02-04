package com.kontick.sendypick.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.kontick.sendypick.R
import com.kontick.sendypick.adapters.LoginAdapter
import com.kontick.sendypick.data.user.UserViewModel
import com.kontick.sendypick.databinding.ActivityAuthBinding
import com.kontick.sendypick.domain.models.authActivity.AuthActivityViewModel


class AuthActivity : AppCompatActivity() {

    var doubleBackToExitPressedOnce = false
    private val context: Context = this
    private var googleSignInClient: GoogleSignInClient? = null
    private lateinit var adView: AdView
    private lateinit var userViewModel: UserViewModel
    private lateinit var binding: ActivityAuthBinding
    private lateinit var authActivityViewModel: AuthActivityViewModel

    companion object {
        private const val RC_SIGN_IN = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authActivityViewModel = ViewModelProvider(this).get(AuthActivityViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.sign_in)))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.sign_up)))
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = LoginAdapter(supportFragmentManager, this, binding.tabLayout.tabCount)
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.fabGoogle.translationY = 300f
        binding.tabLayout.translationY = 300f
        binding.fabGoogle.alpha = 0f
        binding.fabGoogle.animate().translationY(0f).alpha(1000f).setStartDelay(400).start()
        binding.tabLayout.animate().translationY(0f).alpha(1000f).setStartDelay(600).start()
        binding.fabGoogle.setOnClickListener {
            val intent: Intent = googleSignInClient!!.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
        initAdMob()
    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT)
            .show()
        val thread: Thread = object : Thread() {
            override fun run() {
                sleep(2000)
                doubleBackToExitPressedOnce = false
            }
        }
        thread.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val accountTask: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = accountTask.getResult(
                    ApiException::class.java
                )
                authActivityViewModel.authWithGoogleAccount(account)
                    .addOnSuccessListener { result ->
                        authActivityViewModel.writeUserToRoomDatabase(userViewModel, FirebaseAuth.getInstance().currentUser!!.email!!)
                        authActivityViewModel.createUser().addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(context, "Account Created...", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(context, JoinCreateActivity::class.java))
                            } else {
                                Toast.makeText(context, "Error! Try again!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            adView.resume()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            adView.pause()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            adView.destroy()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun initAdMob() {
        MobileAds.initialize(this)
        adView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
}