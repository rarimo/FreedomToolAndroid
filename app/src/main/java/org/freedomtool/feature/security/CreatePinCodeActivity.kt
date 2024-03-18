package org.freedomtool.feature.security

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.databinding.ActivityCreatePinCodeBinding
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.Navigator

class CreatePinCodeActivity : BaseActivity() {

    private lateinit var binding: ActivityCreatePinCodeBinding

    private var isChecking: Boolean = false
    private var pinCode: String = ""
        set(value) {
            if (value.length <= dotArray.size) {
                field = value
            }
        }

    private var checkPinCode: String = ""
        set(value) {
            if (value.length <= dotArray.size) {
                field = value
            }
        }

    private lateinit var buttonArray: Array<MaterialButton>
    private lateinit var dotArray: Array<AppCompatImageView>

    private lateinit var disabledDot: Drawable
    private lateinit var activeDotDrawable: Drawable
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_pin_code)
        binding.activity = this

        disabledDot = resources.getDrawable(R.drawable.ic_disabled_dot)
        activeDotDrawable = resources.getDrawable(R.drawable.ic_active_dot)
        initDotArray()
        initKeyBoard()
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
        }

        checkState()

    }

    private fun checkState() {
        if (!isChecking)
            if (pinCode.length >= dotArray.size) {
                isChecking = true
                cleanDots()
                binding.header.text = getString(R.string.repeat_passcode)
            }
        if (isChecking) {
            if (checkPinCode.length >= dotArray.size) {
                if (checkPinCode == pinCode) {
                    SecureSharedPrefs.savePinCode(this, pinCode)
                    SecureSharedPrefs.saveFirstLaunch(this, false)
                    finish()
                    Navigator.from(this).openVote()
                } else {
                    restartState()
                }
            }
        }
    }

    private fun restartState() {
        MaterialAlertDialogBuilder(this).setTitle("PassCode don't match")
            .setMessage("Please try again").setPositiveButton("") { _, _ ->
                isChecking = false
                binding.header.text = getString(R.string.enter_passcode)
                pinCode = ""
                checkPinCode = ""
                cleanDots()
            }.setOnDismissListener {
                isChecking = false
                binding.header.text = getString(R.string.enter_passcode)
                pinCode = ""
                checkPinCode = ""
                cleanDots()
            }.create()
            .show()

    }

    private fun setPinCodeInput(character: String) {
        if (!isChecking) {
            pinCode = pinCode.plus(character)
            updateProgress(pinCode.length)
            return
        }
        checkPinCode = checkPinCode.plus(character)
        updateProgress(checkPinCode.length)
    }

    private fun dpopLastPinChar() {
        if (!isChecking) {
            pinCode = pinCode.dropLast(1)
            updateProgress(pinCode.length)
            return
        }

        updateProgress(checkPinCode.length)
        checkPinCode = checkPinCode.dropLast(1)
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

}