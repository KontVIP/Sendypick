package com.kontick.sendypick.data.user


interface User {
    fun <T> map(mapper: Mapper<T>): T


    class Base(
        private val id: String,
        private val name: String,
        private val phone: String,
        private val email: String,
        private val profilePhotoPath: String,
        private val roomCode: String
    ) : User {

        override fun <T> map(mapper: Mapper<T>): T =
            mapper.map(id, name, phone, email, profilePhotoPath, roomCode)

    }


    interface Mapper<T> {

        fun map(
            id: String,
            name: String,
            phone: String,
            email: String,
            profilePhotoPath: String,
            roomCode: String
        ): T

        class UserId : Mapper<String> {
            override fun map(
                id: String,
                name: String,
                phone: String,
                email: String,
                profilePhotoPath: String,
                roomCode: String
            ) = id
        }

        class UserFullName : Mapper<String> {
            override fun map(
                id: String,
                name: String,
                phone: String,
                email: String,
                profilePhotoPath: String,
                roomCode: String
            ) = name
        }

        class UserPhone : Mapper<String> {
            override fun map(
                id: String,
                name: String,
                phone: String,
                email: String,
                profilePhotoPath: String,
                roomCode: String
            ) = phone
        }

        class UserEmail : Mapper<String> {
            override fun map(
                id: String,
                name: String,
                phone: String,
                email: String,
                profilePhotoPath: String,
                roomCode: String
            ) = email
        }

        class UserProfilePhotoPath : Mapper<String> {
            override fun map(
                id: String,
                name: String,
                phone: String,
                email: String,
                profilePhotoPath: String,
                roomCode: String
            ) = profilePhotoPath
        }

        class RoomCode : Mapper<String> {
            override fun map(
                id: String,
                name: String,
                phone: String,
                email: String,
                profilePhotoPath: String,
                roomCode: String
            ) = roomCode
        }

    }

}