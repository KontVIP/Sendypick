package com.kontick.sendypick.domain.models.joinCreateFragments.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.kontick.sendypick.data.user.UserViewModel
import com.kontick.sendypick.utils.FirebaseUtils
import com.kontick.sendypick.utils.RoomDatabaseUtils

class WriteDataToDatabaseUseCase(
    private val userViewModel: UserViewModel,
    private val email: String
) {

    fun execute() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseUtils.getInstance().getReference("Users/$uid/")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("code").exists()) {
                        if (snapshot.child("premium")
                                .exists() && snapshot.child("premium").value.toString().toInt() == 1
                        ) {
                            RoomDatabaseUtils.insertDataToDatabase(
                                userViewModel,
                                0,
                                uid,
                                email,
                                snapshot.child("phone").value.toString(),
                                snapshot.child("fullName").value.toString(),
                                snapshot.child("code").value.toString(),
                                snapshot.child("profilePhotoPath").value.toString(),
                                1
                            )
                        } else {
                            RoomDatabaseUtils.insertDataToDatabase(
                                userViewModel,
                                0,
                                uid,
                                email,
                                snapshot.child("phone").value.toString(),
                                snapshot.child("fullName").value.toString(),
                                snapshot.child("code").value.toString(),
                                snapshot.child("profilePhotoPath").value.toString(),
                                0
                            )
                        }
                    } else {
                        if (snapshot.child("premium")
                                .exists() && snapshot.child("premium").value.toString().toInt() == 1
                        ) {
                            RoomDatabaseUtils.insertDataToDatabase(
                                userViewModel,
                                0,
                                uid,
                                email,
                                snapshot.child("phone").value.toString(),
                                snapshot.child("fullName").value.toString(),
                                snapshot.child("code").value.toString(),
                                snapshot.child("profilePhotoPath").value.toString(),
                                1
                            )
                        } else {
                            RoomDatabaseUtils.insertDataToDatabase(
                                userViewModel,
                                0,
                                uid,
                                email,
                                snapshot.child("phone").value.toString(),
                                snapshot.child("fullName").value.toString(),
                                snapshot.child("code").value.toString(),
                                snapshot.child("profilePhotoPath").value.toString(),
                                0
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}