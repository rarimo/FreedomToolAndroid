package org.freedomtool.feature.security

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatImageView
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.databinding.ActivityCheckPinCodeBinding
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.BiometricHelper
import org.freedomtool.utils.Navigator
import org.freedomtool.utils.TimerManager

class CheckPinCodeActivity : BaseActivity() {

    private lateinit var binding: ActivityCheckPinCodeBinding
    private lateinit var disabledDot: Drawable
    private lateinit var activeDotDrawable: Drawable

    private lateinit var biometricManager: BiometricManager
    private var isBiometricEnabled: Boolean = false

    private var pinCode: String = ""
        set(value) {
            if (value.length <= dotArray.size) {
                field = value
            }
        }


    private var tryCounter = 0
    private lateinit var buttonArray: Array<MaterialButton>
    private lateinit var dotArray: Array<AppCompatImageView>
    private lateinit var animation: Animation
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_pin_code)
        binding.activity = this
        disabledDot = resources.getDrawable(R.drawable.ic_disabled_dot)
        activeDotDrawable = resources.getDrawable(R.drawable.ic_active_dot)
        animation = AnimationUtils.loadAnimation(this, R.anim.shake)
        initDotArray()
        initKeyBoard()


        biometricManager = BiometricManager.from(this)


        isBiometricEnabled = SecureSharedPrefs.getIsBiometricEnabled(this)
        if(!isBiometricEnabled) {
            binding.fingerprintButton.visibility = View.INVISIBLE
        }

        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS && isBiometricEnabled) {
            instanceOfBiometricPrompt().authenticate(BiometricHelper().getPromptInfo(this))
        }
    }

    private fun instanceOfBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(this) // 2.
        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                Navigator.from(this@CheckPinCodeActivity).openVote()
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

    private fun initDotArray() {
        dotArray = arrayOf(
            binding.dot1,
            binding.dot2,
            binding.dot3,
            binding.dot4
        )
    }


    private fun initKeyBoard() {
        buttonArray = arrayOf(
            binding.pinKey0,
            binding.pinKey1,
            binding.pinKey2,
            binding.pinKey3,
            binding.pinKey4,
            binding.pinKey5,
            binding.pinKey6,
            binding.pinKey7,
            binding.pinKey8,
            binding.pinKey9,
            binding.pinKey0,
            binding.fingerprintButton
        )
    }

    fun onKeyBoardClick(view: View) {
        when (view.id) {
            binding.pinKey0.id -> {
                setPinCodeInput("0")
            }

            binding.pinKey1.id -> {
                setPinCodeInput("1")

            }

            binding.pinKey2.id -> {
                setPinCodeInput("2")

            }

            binding.pinKey3.id -> {
                setPinCodeInput("3")

            }

            binding.pinKey4.id -> {
                setPinCodeInput("4")

            }

            binding.pinKey5.id -> {
                setPinCodeInput("5")

            }

            binding.pinKey6.id -> {
                setPinCodeInput("6")

            }

            binding.pinKey7.id -> {
                setPinCodeInput("7")
            }

            binding.pinKey8.id -> {
                setPinCodeInput("8")
            }

            binding.pinKey9.id -> {
                setPinCodeInput("9")
            }

            binding.pinDelete.id -> {
                dpopLastPinChar()
            }

            binding.fingerprintButton.id -> {
                instanceOfBiometricPrompt().authenticate(BiometricHelper().getPromptInfo(this))
            }
        }

        checkState()

    }

    private fun checkState() {
        if (dotArray.size == pinCode.length) {
            if (SecureSharedPrefs.checkPinCode(this, pinCode)) {
                Navigator.from(this).openVote()
            } else {
                cleanDots()
                binding.dotsContainer.startAnimation(animation)
                tryCounter++
                pinCode = ""
                if (tryCounter >= 3) {
                    buttonArray.map {
                        it.isEnabled = false
                    }

                    TimerManager {
                        buttonArray.map {
                            it.isEnabled = true
                        }
                    }.startTimer(1000 * 30)


                }
            }
        }
    }

    private fun updateProgress(size: Int) {
        cleanDots()
        for (i in 0..<size) {
            dotArray[i].setImageDrawable(activeDotDrawable)
        }
    }

    private fun cleanDots() {
        dotArray.map {
            it.setImageDrawable(disabledDot)
        }
    }

    private fun dpopLastPinChar() {
        pinCode = pinCode.dropLast(1)
        updateProgress(pinCode.length)
    }

    private fun setPinCodeInput(character: String) {
        pinCode = pinCode.plus(character)
        updateProgress(pinCode.length)
    }

}