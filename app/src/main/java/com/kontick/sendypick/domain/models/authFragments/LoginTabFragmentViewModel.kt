package com.kontick.sendypick.domain.models.authFragments

import androidx.lifecycle.ViewModel
import com.kontick.sendypick.data.user.UserViewModel
import com.kontick.sendypick.domain.models.authFragments.usecase.IsHasRoomUseCase
import com.kontick.sendypick.domain.models.authFragments.usecase.SignInWithEmailAndPasswordUseCase
import com.kontick.sendypick.domain.models.joinCreateFragments.usecase.WriteDataToDatabaseUseCase

class LoginTabFragmentViewModel : ViewModel() {

    fun signInWithEmailAndPassword(email: String, password: String) =
        SignInWithEmailAndPasswordUseCase(email, password).execute()

    fun writeDataToDatabase(userViewModel: UserViewModel, email: String) {
        WriteDataToDatabaseUseCase(userViewModel, email).execute()
    }

    suspend fun isHasRoom(uid: String): Boolean = IsHasRoomUseCase(uid).execute()

}