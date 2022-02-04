package com.kontick.sendypick.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserRoom(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val uid: String,
    val email: String,
    val phone: String,
    val name: String,
    val roomCode: String,
    val profilePhotoPath: String,
    val premiumStatus: Int
)