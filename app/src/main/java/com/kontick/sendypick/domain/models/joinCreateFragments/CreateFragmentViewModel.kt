package com.kontick.sendypick.domain.models.joinCreateFragments

import android.content.ClipboardManager
import androidx.lifecycle.ViewModel
import com.kontick.sendypick.domain.models.joinCreateFragments.usecase.CopyToClipboardUseCase

class CreateFragmentViewModel : ViewModel(), JoinCreateViewModelInterface {

    fun copyToClipboard(text: String, clipboardManager: ClipboardManager) {
        CopyToClipboardUseCase(text, clipboardManager).execute()
    }

}