package com.kontick.sendypick.domain.models.authActivity.usecase

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthWithGoogleAccount(private val account: GoogleSignInAccount) {

    fun execute() =
        FirebaseAuth.getInstance()
            .signInWithCredential(GoogleAuthProvider.getCredential(account!!.idToken, null))

}