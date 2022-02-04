package com.kontick.sendypick.domain.models.joinCreateFragments.usecase

import com.kontick.sendypick.utils.FirebaseUtils

class SetCodeToUserUseCase(private val userId: String, private val code: String) {

    fun execute() {
        FirebaseUtils.getInstance().getReference("Users/$userId/code").setValue(code)
            .addOnSuccessListener {
                FirebaseUtils.getInstance().getReference("Rooms/${code}/$userId").setValue(0)
            }
    }

}