package com.kontick.sendypick.data.advertisement

import androidx.lifecycle.LiveData

class AdRepository(private val adDao: AdDao) {

    val readAllData: LiveData<List<AdRoom>> = adDao.readAllData()

    suspend fun createAdCounter(ad: AdRoom) {
        adDao.createAdCounter(ad)
    }

    suspend fun updateAdCounter(ad: AdRoom) {
        adDao.updateAdCounter(ad)
    }

    suspend fun deleteAdCounter(ad: AdRoom) {
        adDao.deleteAdCounter(ad)
    }


}