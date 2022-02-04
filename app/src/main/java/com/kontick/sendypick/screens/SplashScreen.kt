package com.kontick.sendypick.screens

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kontick.sendypick.R
import com.kontick.sendypick.data.user.UserViewModel
import com.kontick.sendypick.domain.models.splashscreen.SplashScreenViewModel
import com.kontick.sendypick.utils.InternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    private var context: Context = this
    private lateinit var userViewModel: UserViewModel
    private lateinit var splashScreenViewModel: SplashScreenViewModel

    companion object {
        const val CURRENT_VERSION = 21
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        splashScreenViewModel = ViewModelProvider(this).get(SplashScreenViewModel::class.java)
        val builder = AlertDialog.Builder(context)
            if (InternetConnection.checkConnection(context)) {
                userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
                CoroutineScope(Dispatchers.IO).launch {
                    if (splashScreenViewModel.isNewestVersion(CURRENT_VERSION)) {
                        builder.setTitle(getString(R.string.available_new_version))
                        builder.setMessage(R.string.update_the_app)
                        builder.setNeutralButton(R.string.update) { _, _ ->
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                            } catch (e: ActivityNotFoundException) {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                            }
                        }
                        builder.setPositiveButton(getString(R.string.continue_s)) { _, _ ->
                            makeAuth()
                        }
                        val alert = builder.create()
                        alert.show()
                    } else {
                        makeAuth()
                    }
                }
            } else {
                Toast.makeText(context, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show()
                builder.setTitle(R.string.no_internet_connection)
                builder.setMessage(getString(R.string.try_to_refresh_the_app))
                builder.setNeutralButton(
                    getString(R.string.refresh)
                ) { _, _ -> triggerRebirth(context) }
                val alert = builder.create()
                alert.show()
            }
    }

    private fun triggerRebirth(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }

    private fun makeAuth() {
        userViewModel.readAllData.observe(this, Observer { usersRoom ->
            if (usersRoom.isNotEmpty()) {
                if (usersRoom[0].roomCode == "None") {
                    startActivity(Intent(context, JoinCreateActivity::class.java))
                } else {
                    startActivity(Intent(context, MainActivity::class.java))
                }
            } else {
                startActivity(Intent(context, AuthActivity::class.java))
            }
        })
    }

}