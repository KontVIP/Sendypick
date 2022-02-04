package com.kontick.sendypick.data.advertisement

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<AdRoom>>
    private val repository: AdRepository

    init {
        val adDao = AdDatabase.getDatabase(application).adDao()
        repository = AdRepository(adDao)
        readAllData = repository.readAllData
    }

    fun createAdCounter(ad: AdRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createAdCounter(ad)
        }
    }

    fun updateAdCounter(ad: AdRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAdCounter(ad)
        }
    }

    fun deleteAdCounter(ad: AdRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAdCounter(ad)
        }
    }

}