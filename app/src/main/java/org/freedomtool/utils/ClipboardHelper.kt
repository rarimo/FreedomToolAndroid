package org.freedomtool.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat

class ClipboardHelper(context: Context) {

    private val manager = ContextCompat.getSystemService(context, ClipboardManager::class.java)
        ?: throw IllegalStateException("no clipboard manager")

    fun copyText(text: String, label: String = text) {
        manager.setPrimaryClip(
            ClipData.newPlainText(
                label,
                text
            )
        )
    }

    fun pasteText(): String = manager.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
}