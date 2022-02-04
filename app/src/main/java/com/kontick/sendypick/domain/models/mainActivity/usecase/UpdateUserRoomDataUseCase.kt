package com.kontick.sendypick.domain.models.mainActivity.usecase

import com.kontick.sendypick.data.user.User
import com.kontick.sendypick.data.user.UserRoom
import com.kontick.sendypick.data.user.UserViewModel

class UpdateUserRoomDataUseCase(
    private val userViewModel: UserViewModel,
    private val currentUser: User,
    private val userRoomId: Int
) {

    fun execute() {
        userViewModel.updateUser(
            UserRoom(
                userRoomId,
                currentUser.map(User.Mapper.UserId()),
                currentUser.map(User.Mapper.UserEmail()),
                currentUser.map(User.Mapper.UserPhone()),
                currentUser.map(User.Mapper.UserFullName()),
                currentUser.map(User.Mapper.RoomCode()),
                currentUser.map(User.Mapper.UserProfilePhotoPath()),
                1
            )
        )
    }

}