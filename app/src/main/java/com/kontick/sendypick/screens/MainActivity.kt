package com.kontick.sendypick.screens

import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.PurchaseInfo
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.kontick.sendypick.R
import com.kontick.sendypick.data.advertisement.AdRoom
import com.kontick.sendypick.data.advertisement.AdViewModel
import com.kontick.sendypick.data.user.User
import com.kontick.sendypick.data.user.UserViewModel
import com.kontick.sendypick.databinding.ActivityMainBinding
import com.kontick.sendypick.domain.models.mainActivity.MainActivityViewModel
import com.kontick.sendypick.utils.BitmapUtil
import com.kontick.sendypick.utils.FirebaseUtils
import com.kontick.sendypick.widgets.MainAppWidgetProvider


class MainActivity : AppCompatActivity(), BillingProcessor.IBillingHandler {

    private var doubleBackToExitPressedOnce = false
    private var isSending = false
    private var thumbnail: Bitmap? = null
    private lateinit var toast: Toast
    private lateinit var imageUri: Uri
    private lateinit var currentUser: User
    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var adViewModel: AdViewModel
    private var adCounterState = 0
    private var adRoomId = -1
    private var userRoomId = -1
    private var interAd: InterstitialAd? = null
    private var premiumStatus = 0
    private var bp: BillingProcessor? = null
    private lateinit var mainActivityViewModel: MainActivityViewModel

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
        private const val REQUEST_WRITE_PERMISSION = 777
        private const val REQUEST_READ_PERMISSION = 778
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        bp = BillingProcessor.newBillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApeZdc5SXkbQygFzeTYPvi06Ltr6ASi4WikGfFOjuNGVl5czeQHko8uzyajlcNNYNFNtwfWiVfDvBFm3kr6uk7r+qUE/CFk/C9IyS0n6IwaCo7pZZ6KiPevKkGN4VtfCrg8t6GBL1BOMEAPciX4VOH1pS2QcyUOUAqAvvTVLlX6xBy+FxkapKmAAtzJzeepa1kIJR06yT+gztB7WauMUaQwxBRCQfh9QepJFvRcLCqJj7i4rqLTjF61qnDUMaFHRDkEG7y8rVV4blPgNZ5znIzfCNIgwd1gKwegMrotjCB0Efl1+YZDpxnljd0wL0W8hUGxKO0No7f8M826JyxPGInwIDAQAB", this)
        bp?.initialize()

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        adViewModel = ViewModelProvider(this).get(AdViewModel::class.java)
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT)

        val intent = Intent(this, MainAppWidgetProvider::class.java)
        intent.action = "android.appwidget.action.APPWIDGET_UPDATE"
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
            ComponentName(application, MainAppWidgetProvider::class.java)
        )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)

        userViewModel.readAllData.observe(this, androidx.lifecycle.Observer { usersRoom ->
            if (usersRoom.isNotEmpty()) {
                    userRoomId = usersRoom[0].id
                    currentUser = User.Base(
                        usersRoom[0].uid,
                        usersRoom[0].name,
                        usersRoom[0].phone,
                        usersRoom[0].email,
                        usersRoom[0].profilePhotoPath,
                        usersRoom[0].roomCode
                    )
                    if (premiumStatus == 0) initAdMob()
            }
        })
        adViewModel.readAllData.observe(this, androidx.lifecycle.Observer { counter ->
            if (counter.isEmpty()) adViewModel.createAdCounter(AdRoom(0, 0))
            else {
                adRoomId = counter[0].id
                adCounterState = counter[0].interAdCounter
            }
        })

        binding.photoImageView.setOnClickListener {
            try {
                dispatchTakePictureIntent()
            } catch (e: Exception) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        mutableListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE).toTypedArray(),
                        REQUEST_WRITE_PERMISSION
                    )
                }
            }
        }

        binding.sendLayout.setOnClickListener {
            if (thumbnail != null) {
                if (!isSending) {
                    if (adCounterState == 0) {
                        adViewModel.updateAdCounter(AdRoom(adRoomId, 1))
                        startLoadingToDatabase()
                    } else {
                        if (premiumStatus == 1) {
                            startLoadingToDatabase()
                        } else {
                            adViewModel.updateAdCounter(AdRoom(adRoomId, 0))
                            showInterAd()
                        }
                    }
                }
            } else {
                toast.setText(getString(R.string.make_a_photo_first))
                toast.duration = Toast.LENGTH_SHORT
                toast.show()
            }
        }

        binding.galleryImageView.setOnClickListener {
            try {
                val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, REQUEST_IMAGE_PICK)
            } catch (e: Exception) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE).toTypedArray(),
                        REQUEST_READ_PERMISSION
                    )
                }
            }
        }

        binding.saveImageView.setOnClickListener {
            if (thumbnail != null) {
                BitmapUtil.saveBitmapToGallery(thumbnail!!)
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
            } else {
                toast.setText(getString(R.string.make_a_photo_first))
                toast.duration = Toast.LENGTH_SHORT
                toast.show()
            }
        }

        binding.rotateImageView.setOnClickListener {
            if (thumbnail != null) {
                thumbnail = BitmapUtil.getRotatedBitmap(thumbnail!!, -90f)
                binding.photoImageView.setImageBitmap(thumbnail)
                toast.setText(getString(R.string.rotated))
                toast.duration = Toast.LENGTH_SHORT
                toast.show()
            } else {
                toast.setText(getString(R.string.make_a_photo_first))
                toast.duration = Toast.LENGTH_SHORT
                toast.show()
            }
        }
        if(bp?.listOwnedProducts()!!.isNotEmpty()) {
            consumeProduct()
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                toast.setText("No permission!")
                toast.show()
            }
        } else if (requestCode == REQUEST_READ_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, REQUEST_IMAGE_PICK)
            } else {
                toast.setText("No permission!")
                toast.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
        loadInterAd()
    }


    override fun onPause() {
        super.onPause()
        binding.adView.pause()
    }


    override fun onDestroy() {
        bp?.release()
        super.onDestroy()
        binding.adView.destroy()
    }

    private fun initAdMob() {
        MobileAds.initialize(this)
        binding.adView.loadAd(AdRequest.Builder().build())
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            thumbnail = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            while (thumbnail!!.byteCount > 2000000) {
                thumbnail = Bitmap.createScaledBitmap(
                    thumbnail!!,
                    thumbnail!!.width / 2,
                    thumbnail!!.height / 2,
                    false
                )
            }
            binding.photoImageView.setImageBitmap(thumbnail)
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            thumbnail = MediaStore.Images.Media.getBitmap(contentResolver, data?.data)
            while (thumbnail!!.byteCount > 2000000) {
                thumbnail = Bitmap.createScaledBitmap(
                    thumbnail!!,
                    thumbnail!!.width / 2,
                    thumbnail!!.height / 2,
                    false
                )
            }
            binding.photoImageView.setImageBitmap(thumbnail)
        }
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
                    sleep(2000)
                    doubleBackToExitPressedOnce = false
                }
            }
            thread.start()
    }

    private fun loadInterAd() {
        InterstitialAd.load(this,
            "ca-app-pub-8736365556541110/5542702227", AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interAd = null
                }
                override fun onAdLoaded(ad: InterstitialAd) {
                    interAd = ad
                }
            })
    }

    private fun showInterAd() {
        if (interAd != null) {
            interAd?.fullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        startLoadingToDatabase()
                        interAd = null
                        loadInterAd()
                    }
                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        startLoadingToDatabase()
                        interAd = null
                        loadInterAd()
                    }
                    override fun onAdShowedFullScreenContent() {
                        interAd = null
                        loadInterAd()
                    }
                }
            interAd?.show(this)
        } else startLoadingToDatabase()
    }

    private fun startLoadingToDatabase() {
        mainActivityViewModel.loadPhotoToDatabase(
            currentUser,
            BitmapUtil.getRoundedCornerBitmap(BitmapUtil.centerCropBitmap(thumbnail!!), 100))
    }

    override fun onProductPurchased(productId: String, details: PurchaseInfo?) {
        Toast.makeText(this, getString(R.string.purchased_successfully), Toast.LENGTH_SHORT).show()
        consumeProduct()
    }
    override fun onPurchaseHistoryRestored() {
    }
    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Toast.makeText(this, "Error ${error?.localizedMessage}, code $errorCode", Toast.LENGTH_LONG).show()
    }
    override fun onBillingInitialized() {
    }

    private fun consumeProduct() {
        bp?.consumePurchaseAsync(
            "disable_ads_2",
            object : BillingProcessor.IPurchasesResponseListener {
                override fun onPurchasesSuccess() {
                    FirebaseUtils.getInstance()
                        .getReference("Users/${currentUser.map(User.Mapper.UserId())}/premium")
                        .setValue(1)
                    mainActivityViewModel.updateUserRoomData(userViewModel, currentUser, userRoomId)
                }
                override fun onPurchasesError() {
                }
            })
    }

}