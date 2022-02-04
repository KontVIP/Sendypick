package com.kontick.sendypick.domain.models.mainActivity.usecase

import android.graphics.Bitmap
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.kontick.sendypick.data.user.User
import com.kontick.sendypick.utils.FirebaseUtils
import java.io.ByteArrayOutputStream
import java.util.*

class LoadPhotoToDatabaseUseCase(private val currentUser: User, private var bitmap: Bitmap) {

    fun execute() {
        while (bitmap.byteCount > 1500000) {
            bitmap = Bitmap.createScaledBitmap(
                bitmap,
                bitmap.width / 2,
                bitmap.height / 2,
                false
            )
        }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        val uploadTask: UploadTask
        val storageRef = FirebaseStorage.getInstance().getReference("Images")
            .child(UUID.randomUUID().toString())
        uploadTask = storageRef.putBytes(byteArray)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            if (task.isSuccessful) {
                val downloadUri = task.result //Uri of the image
                var state = 0
                FirebaseUtils.getInstance()
                    .getReference(
                        "Rooms/${currentUser.map(User.Mapper.RoomCode())}/${currentUser.map(User.Mapper.UserId())}")
                    .setValue(downloadUri.toString())
            }
        }
    }

}