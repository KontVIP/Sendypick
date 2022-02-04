package com.kontick.sendypick.utils

import com.kontick.sendypick.data.user.User
import com.kontick.sendypick.data.user.UserRoom
import com.kontick.sendypick.data.user.UserViewModel

object RoomDatabaseUtils {

    fun insertDataToDatabase(
        userViewModel: UserViewModel,
        id: Int,
        uid: String,
        email: String,
        phone: String,
        fullName: String,
        roomCode: String,
        profilePhotoPath: String,
        premium: Int
    ) {
        val userRoom = UserRoom(0, uid, email, phone, fullName, roomCode, profilePhotoPath, premium)
        userViewModel.addUser(userRoom)
    }

    fun insertDataToDatabase(userViewModel: UserViewModel, user: User.Base) {
        val userRoom = UserRoom(
            0,
            user.map(User.Mapper.UserId()),
            user.map(User.Mapper.UserEmail()),
            user.map(User.Mapper.UserPhone()),
            user.map(User.Mapper.UserFullName()),
            user.map(User.Mapper.RoomCode()),
            user.map(User.Mapper.UserProfilePhotoPath()),
            0
        )
        userViewModel.addUser(userRoom)
    }

}