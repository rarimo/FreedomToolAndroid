package org.freedomtool.feature.voting

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.data.models.VotingData
import org.freedomtool.databinding.ActivityManifestSigningBinding
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.Navigator
import org.freedomtool.utils.resolveDays

class ManifestSigning : BaseActivity() {

    private lateinit var binding: ActivityManifestSigningBinding
    private lateinit var votingData: VotingData
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manifest_signing)
        binding.lifecycleOwner = this
        votingData = intent?.getParcelableExtra(MANIFEST_DATA)!!

        binding.data = votingData
        binding.date.text = resolveDays(this, votingData.dueDate!!)
        binding.signedCount.text = getString(R.string._x_people_already_signed, votingData.votingCount.toString())
        initButtons()
    }

    private fun initButtons() {
        clickHelper.addViews(binding.cancelButton, binding.signBtn)
        clickHelper.setOnClickListener {
            when(it.id) {
                binding.cancelButton.id -> {
                    finish()
                }
                binding.signBtn.id -> {

//                    if(SecureSharedPrefs.getIsPassportScanned(this)) {
//                        Navigator.from(this).openVoteProcessing(votingData)
//                        finish()
//                        return@setOnClickListener
//                    }

                    Navigator.from(this).openVerificationPage(votingData)
                }
            }
        }
    }

    companion object {
        const val MANIFEST_DATA = "MANIFEST_DATA"
    }

}