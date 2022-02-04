package com.kontick.sendypick.domain.models.joinCreateFragments.usecase

import com.kontick.sendypick.data.user.User
import com.kontick.sendypick.data.user.UserRoom
import com.kontick.sendypick.data.user.UserViewModel

class UpdateUserRoomDataUseCase(
    private val userViewModel: UserViewModel,
    private val user: User,
    private val userRoomId: Int,
    private val code: String,
    private val premiumStatus: Int
) {

    fun execute() {
        userViewModel.updateUser(UserRoom(
            userRoomId,
            user.map(User.Mapper.UserId()),
            user.map(User.Mapper.UserEmail()),
            user.map(User.Mapper.UserPhone()),
            user.map(User.Mapper.UserFullName()),
            code,
            user.map(User.Mapper.UserProfilePhotoPath()),
            premiumStatus
        ))

    }

}