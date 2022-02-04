package com.kontick.sendypick.data.user

import com.google.android.gms.tasks.Task
import com.kontick.sendypick.utils.FirebaseUtils

class UserCreator {

    fun createUser(user : User): Task<Void> {
        val userInfo = UserInfo(
            user.map(User.Mapper.UserFullName()),
            user.map(User.Mapper.UserEmail()),
            user.map(User.Mapper.UserPhone()),
            user.map(User.Mapper.UserProfilePhotoPath())
        )
        return FirebaseUtils.getInstance().getReference("Users").child(user.map(User.Mapper.UserId()))
            .setValue(userInfo)
    }

    private data class UserInfo(
        val fullName: String,
        val email: String,
        val phone: String,
        val profilePhotoPath: String
    )
}