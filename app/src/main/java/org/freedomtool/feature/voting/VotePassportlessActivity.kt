package org.freedomtool.feature.voting

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.data.models.VotingData
import org.freedomtool.databinding.ActivityVotePassportlessBinding
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.Navigator

class VotePassportlessActivity : BaseActivity() {

    private lateinit var votingData: VotingData
    private lateinit var binding: ActivityVotePassportlessBinding

    private var isAlreadyVoted: Boolean = false
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vote_passportless)
        binding.lifecycleOwner = this
        votingData = intent?.getParcelableExtra(VOTING_DATA)!!

        binding.data = votingData

        initButtons()

        setPermissionsForVoting()
    }

    override fun onResume() {

        super.onResume()
        setPermissionsForVoting()
    }

    private fun setPermissionsForVoting() {
        isAlreadyVoted = SecureSharedPrefs.getVoteResult(this) > 0
        if(isAlreadyVoted) {
            declineToUser()
        }
    }

    private fun declineToUser(){

        binding.participateBtn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.unselected_button_color)
        binding.participateBtn.icon = getDrawable(R.drawable.ic_check)
        binding.participateBtn.text = resources.getText(R.string.enrolled)
        binding.participateBtn.setTextColor(resources.getColor(R.color.black))
        binding.participateBtn.iconTint = ContextCompat.getColorStateList(this, R.color.black)
    }

    private fun initButtons() {
        clickHelper.addViews(binding.participateBtn, binding.backButton)

        clickHelper.setOnClickListener {
            when(it.id) {
                binding.participateBtn.id -> {
                    Navigator.from(this).openOptionVoting(votingData)
                }

                binding.backButton.id -> {
                    finish()
                }
            }
        }
    }

    companion object {
        const val VOTING_DATA = "voting_data"
    }

}