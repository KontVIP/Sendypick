package com.kontick.sendypick.domain.models.splashscreen

import androidx.lifecycle.ViewModel
import com.kontick.sendypick.domain.models.splashscreen.usecase.IsNewestVersionUseCase

class SplashScreenViewModel : ViewModel() {

    suspend fun isNewestVersion(currentVersion: Int) =
        IsNewestVersionUseCase(currentVersion).execute()

}