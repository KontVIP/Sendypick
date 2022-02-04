package com.kontick.sendypick.domain.models.authActivity

import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.kontick.sendypick.data.user.UserViewModel
import com.kontick.sendypick.domain.models.authActivity.usecase.AuthWithGoogleAccount
import com.kontick.sendypick.domain.models.authActivity.usecase.CreateUserUseCase
import com.kontick.sendypick.domain.models.joinCreateFragments.usecase.WriteDataToDatabaseUseCase

class AuthActivityViewModel : ViewModel() {

    fun authWithGoogleAccount(account: GoogleSignInAccount) =
        AuthWithGoogleAccount(account).execute()

    fun createUser() = CreateUserUseCase().execute()

    fun writeUserToRoomDatabase(userViewModel: UserViewModel, email: String) {
        WriteDataToDatabaseUseCase(userViewModel, email).execute()
    }
}