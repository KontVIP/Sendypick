package com.kontick.sendypick.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import com.kontick.sendypick.R
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.kontick.sendypick.utils.FirebaseUtils
import com.kontick.sendypick.utils.InternetConnection
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.BitmapFactory
import com.kontick.sendypick.screens.SplashScreen
import java.io.InputStream
import java.net.URL


class MainAppWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val WIDGET_CLICKED = "Clicked"
    }

    private var currentUser = FirebaseAuth.getInstance().currentUser

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val appWidgetManager1 = appWidgetManager!!
        val watchWidget = ComponentName(context!!, MainAppWidgetProvider::class.java)
        val remoteViews = RemoteViews(context.packageName, R.layout.main_widget)
        val intent = Intent(context, MainAppWidgetProvider::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        remoteViews.setOnClickPendingIntent(R.id.widget_image_view, pendingIntent)
        remoteViews.setOnClickPendingIntent(
            R.id.widget_image_view,
            getPendingSelfIntent(context, WIDGET_CLICKED)
        )
        loadPhoto(context)
        appWidgetManager1.updateAppWidget(watchWidget, remoteViews)
    }

    private fun getPendingSelfIntent(context : Context, action : String) : PendingIntent {
        val intent = Intent(context, javaClass)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val policy = ThreadPolicy.Builder().permitNetwork().build()
        StrictMode.setThreadPolicy(policy)
        super.onReceive(context, intent)
        if (WIDGET_CLICKED == intent!!.action) {
            if (InternetConnection.checkConnection(context!!)) {
                val newIntent = Intent(context, SplashScreen::class.java)
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(newIntent)
                loadPhoto(context)
            }
        }
    }

    private fun loadPhoto(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val remoteViews = RemoteViews(context.packageName, R.layout.main_widget)
        val watchWidget = ComponentName(context, MainAppWidgetProvider::class.java)
        if (currentUser != null) {
            FirebaseUtils.getInstance().getReference("Users/${currentUser!!.uid}/code")
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()) {
                            FirebaseUtils.getInstance().getReference("Rooms/${snapshot.value}").
                            addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    snapshot.children.forEach {
                                        if (it.key as String != currentUser!!.uid) {
                                            val bitmap2 = BitmapFactory.decodeStream(URL(it.value as String).content as InputStream)
                                            setBitmap(remoteViews,R.id.widget_image_view, bitmap2)
                                            appWidgetManager.updateAppWidget(watchWidget, remoteViews)
                                        }
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                }
                            })
                        } else {
                            Toast.makeText(context, "No photos", Toast.LENGTH_SHORT).show()
                            remoteViews.setImageViewResource(R.id.widget_image_view, R.drawable.logo_launcher_end)
                            appWidgetManager.updateAppWidget(watchWidget, remoteViews)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }

    private fun setBitmap(views: RemoteViews, resId: Int, bitmap: Bitmap) {
        val proxy = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(proxy)
        c.drawBitmap(bitmap, Matrix(), null)
        views.setImageViewBitmap(resId, proxy)
    }
}

