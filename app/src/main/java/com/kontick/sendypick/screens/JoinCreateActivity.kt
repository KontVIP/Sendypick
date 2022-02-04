package com.kontick.sendypick.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.kontick.sendypick.R
import com.kontick.sendypick.data.user.User
import com.kontick.sendypick.data.user.UserViewModel
import com.kontick.sendypick.fragments.JoinOrCreateFragment
import java.lang.Exception

class JoinCreateActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false
    private lateinit var adView: AdView
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_create)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.join_create_frame, JoinOrCreateFragment.newInstance()).commit()

        userViewModel.readAllData.observe(this, androidx.lifecycle.Observer { usersRoom ->
            if (usersRoom.isNotEmpty()) {
                if (usersRoom[0].premiumStatus == 0) {
                    initAdMob()
                }
            }
        })
    }


    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onPause() {
        super.onPause()
        adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }

    private fun initAdMob() {
        MobileAds.initialize(this)
        adView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun onBackPressed() {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                finishAffinity()
                return
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show()
            val thread: Thread = object : Thread() {
                override fun run() {
                    try {
                        sleep(2000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    doubleBackToExitPressedOnce = false
                }
            }
            thread.start()
        }
}