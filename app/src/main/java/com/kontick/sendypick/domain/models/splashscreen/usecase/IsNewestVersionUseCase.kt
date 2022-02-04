package com.kontick.sendypick.domain.models.splashscreen.usecase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.kontick.sendypick.utils.FirebaseUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IsNewestVersionUseCase(private val currentVersion: Int) {

    suspend fun execute() = suspendCoroutine<Boolean> { continuation ->
        FirebaseUtils.getInstance().getReference("Version")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(currentVersion == snapshot.value.toString().toInt())
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}