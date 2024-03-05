package org.freedomtool.feature.voting

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.data.models.VotingData
import org.freedomtool.databinding.ActivitySignedManifestBinding
import org.freedomtool.utils.Navigator
import org.freedomtool.utils.resolveDays

class SignedManifest : BaseActivity() {

    private lateinit var binding: ActivitySignedManifestBinding
    private lateinit var voteData: VotingData

    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signed_manifest)


        voteData = intent?.getParcelableExtra(VOTE_DATA)!!
        binding.data = voteData
        binding.signedCount.text = getString(
            R.string.you_and_x_other_people_already_signed,
            voteData.votingCount.toString()
        )
        binding.dataOfVoting.text = resolveDays(this, voteData.dueDate!!)
        initButtons()
    }

    private fun initButtons() {
        clickHelper.addViews(binding.closeBtn)
        clickHelper.setOnClickListener {
            when (it.id) {
                binding.closeBtn.id -> {
                    finish()
                    Navigator.from(this).openVote()
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
        Navigator.from(this).openVote()
    }
    companion object {
        const val VOTE_DATA = "voteData"
    }
}