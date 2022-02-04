package com.kontick.sendypick.domain.models.authActivity.usecase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.kontick.sendypick.data.user.User
import com.kontick.sendypick.data.user.UserCreator

class CreateUserUseCase {

    fun execute(): Task<Void> {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val user = User.Base(
            firebaseUser.uid,
            firebaseUser.displayName!!,
            "None",
            firebaseUser.email!!,
            firebaseUser.photoUrl.toString(),
            "None"
        )
        val userCreator = UserCreator()
        return userCreator.createUser(user)
    }

}