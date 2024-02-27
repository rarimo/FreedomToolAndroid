package org.freedomtool.feature.onBoarding

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.databinding.ActivityInfoBinding
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.LocalizationManager
import org.freedomtool.utils.Navigator

class InfoActivity : BaseActivity() {

    private lateinit var binding: ActivityInfoBinding
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_info)
        binding.lifecycleOwner = this
        initButtons()
    }

    private fun initButtons() {
        clickHelper.addViews(binding.buttonBack, binding.startButton, binding.changeLanguage)

        clickHelper.setOnClickListener {
            when (it.id) {
                binding.buttonBack.id -> {
                    finish()
                }

                binding.startButton.id -> {
                    SecureSharedPrefs.saveFirstLaunch(this, false)
                    Navigator.from(this).openVote()
                }

                binding.changeLanguage.id -> {
                    LocalizationManager.switchLocale(this)
                    recreate()
                }
            }
        }
    }


}