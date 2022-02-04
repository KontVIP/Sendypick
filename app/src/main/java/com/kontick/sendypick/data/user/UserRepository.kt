package com.kontick.sendypick.data.user

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {

    val readAllData: LiveData<List<UserRoom>> = userDao.readAllData()

    suspend fun addUser (user: UserRoom) {
        userDao.addUser(user)
    }

    suspend fun updateUser(userRoom : UserRoom) {
        userDao.updateUser(userRoom)
    }

    suspend fun deleteUser(userRoom: UserRoom) {
        userDao.deleteUser(userRoom)
    }

    suspend fun deleteAllUsers() {
        userDao.deleteAllUsers()
    }
}