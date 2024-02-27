package org.freedomtool.feature.intro


import android.os.Bundle
import androidx.databinding.DataBindingUtil
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.databinding.ActivityStartBinding
import org.freedomtool.utils.LocalizationManager
import org.freedomtool.utils.Navigator

class StartActivity : BaseActivity() {

    private lateinit var binding: ActivityStartBinding

    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start)
        binding.lifecycleOwner = this

        initButton()
    }

    private fun initButton() {
        clickHelper.addViews(binding.changeLanguage, binding.start)

        clickHelper.setOnClickListener {
            when (it.id) {
                binding.changeLanguage.id -> {
                    LocalizationManager.switchLocale(context = this)
                    recreate()
                }

                binding.start.id -> {
                    finish()
                    Navigator.from(this).openIntro()
                }

            }
        }
    }

}