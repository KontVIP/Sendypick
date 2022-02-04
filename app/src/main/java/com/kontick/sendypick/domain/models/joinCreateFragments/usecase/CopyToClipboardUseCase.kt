package com.kontick.sendypick.domain.models.joinCreateFragments.usecase

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

class CopyToClipboardUseCase(private val text: String, private val clipboardManager: ClipboardManager) {

    fun execute() {
        val clipData = ClipData.newPlainText("text", text)
        clipboardManager.setPrimaryClip(clipData)
    }

}