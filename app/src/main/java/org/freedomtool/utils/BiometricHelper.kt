package org.freedomtool.utils

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import org.freedomtool.R

class BiometricHelper {

    fun getPromptInfo(context: Context): BiometricPrompt.PromptInfo = // 6.
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometric_prompt_title))
            .setDescription(context.getString(R.string.biometric_prompt_description))
            .setNegativeButtonText(" ")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            )
            .build()
}