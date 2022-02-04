package com.kontick.sendypick.domain.models.authFragments.usecase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.kontick.sendypick.utils.FirebaseUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IsHasRoomUseCase(private val userId: String) {
    
    suspend fun execute() = suspendCoroutine<Boolean> { continuatuin ->
        FirebaseUtils.getInstance().getReference("Users/$userId/code")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                   continuatuin.resume(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
    
}