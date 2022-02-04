package com.kontick.sendypick.domain.models.joinCreateFragments.usecase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.kontick.sendypick.utils.FirebaseUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CheckRoomExistingUseCase(private val code: String) {

    suspend fun execute() = suspendCoroutine<Boolean> { continuation ->
        FirebaseUtils.getInstance().getReference("Rooms/$code")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                     continuation.resume(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

}