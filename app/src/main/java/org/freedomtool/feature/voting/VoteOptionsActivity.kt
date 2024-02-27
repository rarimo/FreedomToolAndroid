package org.freedomtool.feature.voting

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.data.models.VotingData
import org.freedomtool.databinding.ActivityVoteOptionsBinding
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.Navigator

class VoteOptionsActivity : BaseActivity() {

    private lateinit var binding: ActivityVoteOptionsBinding
    private lateinit var votingData: VotingData

    private var selectedOption = -1
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_vote_options
        )
        binding.lifecycleOwner = this

        val selectedOptionSc = SecureSharedPrefs.getVoteResult(this)

        votingData = intent?.getParcelableExtra(VOTING_DATA)!!

        binding.data = votingData
        if(selectedOptionSc > 0) {
            binding.firstPercentage.visibility = View.VISIBLE
            binding.secondPercentage.visibility = View.VISIBLE
            binding.thirdPercentage.visibility = View.VISIBLE
            binding.voteContainer.visibility = View.GONE

            when(selectedOptionSc) {
                1 -> {
                    binding.firstPercentage.background = resources.getDrawable(R.drawable.section_done_background)
                    binding.progress1.setIndicatorColor(resources.getColor(R.color.primary_button_color))
                }
                2 -> {
                    binding.secondPercentage.background = resources.getDrawable(R.drawable.section_done_background)
                    binding.progress2.setIndicatorColor(resources.getColor(R.color.primary_button_color))
                }
                3 -> {
                    binding.thirdPercentage.background = resources.getDrawable(R.drawable.section_done_background)
                    binding.progress3.setIndicatorColor(resources.getColor(R.color.primary_button_color))
                }
            }

            binding.progress1.setProgressCompat(24, false)
            binding.progress2.setProgressCompat(56, false)
            binding.progress3.setProgressCompat(20, false)

            initViewButtons()

            return
        }


        binding.voteBtn.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.unselected_button_color)
        initButtons()
    }

    private fun clearButtons() {
        binding.option1.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.unselected_button_color)
        binding.option2.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.unselected_button_color)
        binding.option3.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.unselected_button_color)
        binding.voteBtn.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.primary_button_color)
        binding.voteBtn.isEnabled = true
        selectedOption = -1
    }

    private fun initViewButtons() {
        clickHelper.addViews(binding.backButton)

        clickHelper.setOnClickListener {
            when(it.id) {
                binding.backButton.id -> {
                    finish()
                }
            }
        }
    }

    private fun initButtons() {
        clickHelper.addViews(
            binding.voteBtn, binding.backButton, binding.option1, binding.option2, binding.option3
        )

        clickHelper.setOnClickListener {
            when (it.id) {
                binding.voteBtn.id -> {
                    SecureSharedPrefs.saveVoteResult(this, selectedOption)
                    finish()
                    Navigator.from(this).openOptionVoting(votingData)
                }

                binding.backButton.id -> {
                    finish()
                }

                binding.option1.id -> {
                    clearButtons()
                    binding.option1.backgroundTintList =
                        ContextCompat.getColorStateList(this, R.color.primary_button_color)
                    selectedOption = 1
                }

                binding.option2.id -> {
                    clearButtons()
                    binding.option2.backgroundTintList =
                        ContextCompat.getColorStateList(this, R.color.primary_button_color)
                    selectedOption = 2
                }

                binding.option3.id -> {
                    clearButtons()
                    binding.option3.backgroundTintList =
                        ContextCompat.getColorStateList(this, R.color.primary_button_color)
                    selectedOption = 3
                }
            }
        }


    }

    companion object {
        const val VOTING_DATA = "voting_data"
    }

}