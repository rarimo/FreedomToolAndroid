package org.freedomtool.feature.security

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.databinding.ActivityCreateCredentialBinding
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.BiometricHelper
import org.freedomtool.utils.Navigator

private enum class State {
    FINGERPRINT, PIN_CODE
}

class CreateCredentialActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateCredentialBinding

    private lateinit var biometricManager: BiometricManager

    private lateinit var state: State
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_credential)

        biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate(BIOMETRIC_STRONG) == BIOMETRIC_SUCCESS) {
            setFingerprintFragment()
        } else {
            setPinCodeFragment()
        }

        initButtons()
    }


    private fun setFingerprintFragment() {
        state = State.FINGERPRINT
        val fragment = EnableFingerprintFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(binding.container.id, fragment).commit()
    }

    private fun setPinCodeFragment() {
        state = State.PIN_CODE
        val fragment = SetUpPinCodeFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(binding.container.id, fragment).commit()
        binding.laterButton.visibility = View.GONE
        binding.mainButton.text =  getString(R.string.enter)
    }


    private fun initButtons() {
        clickHelper.addViews(binding.laterButton, binding.mainButton)

        clickHelper.setOnClickListener {
            when (it.id) {
                binding.mainButton.id -> {
                    if (state == State.FINGERPRINT) {
                        instanceOfBiometricPrompt().authenticate(BiometricHelper().getPromptInfo(this))
                    }
                    if (state == State.PIN_CODE) {
                        finish()
                        Navigator.from(this).openCreatePinCode()
                    }

                }

                binding.laterButton.id -> {
                    if (state == State.FINGERPRINT) {
                        setPinCodeFragment()
                    }
                }
            }
        }
    }


    private fun instanceOfBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(this) // 2.
        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                SecureSharedPrefs.saveIsBiometricSaved(this@CreateCredentialActivity, true)
                setPinCodeFragment()
            }

            override fun onAuthenticationFailed() {

            }

            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {

                Log.e("Fail", "$errorCode $errString")
                if (errorCode == BiometricPrompt.ERROR_LOCKOUT) {
                    toastManager.long(getString(R.string.too_many_attempts))
                }
            }
        }
        return BiometricPrompt(this, executor, callback)
    }




}