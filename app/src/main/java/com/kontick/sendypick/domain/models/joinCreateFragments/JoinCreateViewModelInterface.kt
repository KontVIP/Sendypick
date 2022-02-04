package com.kontick.sendypick.domain.models.joinCreateFragments

import com.kontick.sendypick.data.user.User
import com.kontick.sendypick.data.user.UserViewModel
import com.kontick.sendypick.domain.models.joinCreateFragments.usecase.SetCodeToUserUseCase
import com.kontick.sendypick.domain.models.joinCreateFragments.usecase.UpdateUserRoomDataUseCase

interface JoinCreateViewModelInterface {

    fun setUserRoomCode(userId: String, code: String) {
        SetCodeToUserUseCase(userId, code).execute()
    }

    fun addCodeToUserRoomData(userViewModel: UserViewModel,
                              user: User,
                              userRoomId: Int,
                              code: String,
                              premiumStatus: Int) {
        UpdateUserRoomDataUseCase(userViewModel, user, userRoomId, code, premiumStatus).execute()
    }

}