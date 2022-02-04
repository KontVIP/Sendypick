package com.kontick.sendypick.domain.models.authFragments.usecase

import com.google.firebase.auth.FirebaseAuth

class CreateUserWithEmailAndPasswordUseCase(
    private val email: String,
    private val password: String
) {

    fun execute() = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)

}