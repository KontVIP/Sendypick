package com.kontick.sendypick.domain.models.authFragments

import androidx.lifecycle.ViewModel
import com.kontick.sendypick.data.user.User
import com.kontick.sendypick.data.user.UserCreator
import com.kontick.sendypick.domain.models.authFragments.usecase.CreateUserWithEmailAndPasswordUseCase

class SignupTabFragmentViewModel : ViewModel() {

    fun createUserWithEmailAndPassword(email: String, password: String) =
        CreateUserWithEmailAndPasswordUseCase(email, password).execute()

    fun createUser(user: User) = UserCreator().createUser(user)
}