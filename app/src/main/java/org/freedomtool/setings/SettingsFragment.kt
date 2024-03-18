package org.freedomtool.setings

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import org.freedomtool.R
import org.freedomtool.base.view.BaseBottomSheetDialog
import org.freedomtool.databinding.FragmentSettingsBinding
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.BiometricHelper
import org.freedomtool.utils.LocalizationManager
import org.freedomtool.utils.Navigator

class SettingsFragment : BaseBottomSheetDialog() {

    private lateinit var binding: FragmentSettingsBinding
    lateinit var logoutCallback: () -> Unit
    private lateinit var biometricManager: BiometricManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_settings, container, false
        )

        biometricManager = BiometricManager.from(requireContext())
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            binding.switchFingerprint.isChecked =
                SecureSharedPrefs.getIsBiometricEnabled(requireContext())
        } else {
            binding.fingerprintContainer.visibility = View.GONE
        }


        initButtons()
        subscribeToChangeBiometric()

        if (!SecureSharedPrefs.getIsPassportScanned(requireContext())) {
            binding.logout.visibility = View.GONE
        }
        return binding.root
    }

    private fun initButtons() {
        clickHelper.addViews(binding.logout, binding.closeBtn)

        clickHelper.setOnClickListener {
            when (it.id) {
                binding.logout.id -> {
                    logoutCallback.invoke()
                }

                binding.closeBtn.id -> {
                    dismiss()
                }
            }
        }


        val currentLocale = LocalizationManager.getLocale(requireContext())!!.language == "ru"

        if (currentLocale) {
            binding.buttonGroupRoundSelectedButton.setPosition(0, false)
        } else {
            binding.buttonGroupRoundSelectedButton.setPosition(1, false)
        }

        binding.buttonGroupRoundSelectedButton.setOnPositionChangedListener { position ->
            val currentLocale = LocalizationManager.getLocale(requireContext())!!.language


            if (position == 0) {
                if (currentLocale == "ru") {
                    return@setOnPositionChangedListener
                }
                LocalizationManager.saveLocale(requireContext(), "ru")
            } else {
                if (currentLocale == "en")
                    return@setOnPositionChangedListener
                LocalizationManager.saveLocale(requireContext(), "en")
            }

            requireActivity().recreate()
        }
    }

    private fun subscribeToChangeBiometric() {
        binding.switchFingerprint.setOnCheckedChangeListener { compoundButton, isChecked ->
            if(isChecked) {

                instanceOfBiometricPrompt().authenticate(BiometricHelper().getPromptInfo(requireContext()))
                return@setOnCheckedChangeListener
            }

            SecureSharedPrefs.saveIsBiometricSaved(requireContext(), false)
        }
    }

    private fun instanceOfBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val callback = object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                SecureSharedPrefs.saveIsBiometricSaved(requireContext(), true)
            }

            override fun onAuthenticationFailed() {
                binding.switchFingerprint.isChecked = false
            }

            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                binding.switchFingerprint.isChecked = false
                Log.e("Fail", "$errorCode $errString")
                if (errorCode == BiometricPrompt.ERROR_LOCKOUT) {
                    toastManager.long(getString(R.string.too_many_attempts))
                }
            }
        }
        return BiometricPrompt(this, executor, callback)
    }


    companion object {
        const val TAG = "SettingsFragment"
    }

}