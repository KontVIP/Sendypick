package com.kontick.sendypick.domain.models.joinCreateFragments

import androidx.lifecycle.ViewModel
import com.kontick.sendypick.domain.models.joinCreateFragments.usecase.CheckRoomExistingUseCase

class JoinFragmentViewModel: ViewModel(), JoinCreateViewModelInterface {

    suspend fun isRoomExist(roomCode: String) = CheckRoomExistingUseCase(roomCode).execute()

}