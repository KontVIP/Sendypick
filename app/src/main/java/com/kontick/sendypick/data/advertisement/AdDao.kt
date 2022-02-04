package com.kontick.sendypick.data.advertisement

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kontick.sendypick.data.user.UserRoom

@Dao
interface AdDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createAdCounter(ad: AdRoom)

    @Update
    suspend fun updateAdCounter(ad: AdRoom)

    @Delete
    suspend fun deleteAdCounter(ad: AdRoom)

    @Query("SELECT * FROM ad_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<AdRoom>>
}