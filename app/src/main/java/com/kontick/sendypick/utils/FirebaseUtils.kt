package com.kontick.sendypick.utils

import com.google.firebase.database.FirebaseDatabase

object FirebaseUtils {

    fun getInstance() =
        FirebaseDatabase.getInstance("https://sendypick-da297-default-rtdb.europe-west1.firebasedatabase.app")

}