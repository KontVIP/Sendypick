package com.kontick.sendypick.domain.models.mainActivity

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.kontick.sendypick.data.user.User
import com.kontick.sendypick.data.user.UserViewModel
import com.kontick.sendypick.domain.models.mainActivity.usecase.LoadPhotoToDatabaseUseCase
import com.kontick.sendypick.domain.models.mainActivity.usecase.UpdateUserRoomDataUseCase

class MainActivityViewModel : ViewModel() {

    fun loadPhotoToDatabase(currentUser: User, bitmap: Bitmap) {
        LoadPhotoToDatabaseUseCase(currentUser, bitmap).execute()
    }

    fun updateUserRoomData(userViewModel: UserViewModel, currentUser: User, userRoomId: Int) {
        UpdateUserRoomDataUseCase(userViewModel, currentUser, userRoomId).execute()
    }

}