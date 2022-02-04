package com.kontick.sendypick.data.advertisement

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ad_table")
data class AdRoom(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val interAdCounter: Int
)