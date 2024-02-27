package org.freedomtool.feature.voting

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.data.models.VotingData
import org.freedomtool.databinding.ActivityManifestSigningBinding

class ManifestSigning : BaseActivity() {

    private lateinit var binding: ActivityManifestSigningBinding
    private lateinit var votingData: VotingData
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manifest_signing)
        binding.lifecycleOwner = this
        votingData = intent?.getParcelableExtra(MANIFEST_DATA)!!

        binding.data = votingData
        initButtons()
    }

    private fun initButtons() {
        clickHelper.addViews(binding.cancelButton)
        clickHelper.setOnClickListener {
            when(it.id) {
                binding.cancelButton.id -> {
                    finish()
                }
            }
        }
    }

    companion object {
        const val MANIFEST_DATA = "MANIFEST_DATA"
    }

}