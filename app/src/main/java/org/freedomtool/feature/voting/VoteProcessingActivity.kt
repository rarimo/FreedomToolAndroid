package org.freedomtool.feature.voting


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.databinding.ActivityVoteProcessingBinding
import org.freedomtool.logic.persistance.SecureSharedPrefs


class VoteProcessingActivity : BaseActivity() {

    private lateinit var binding: ActivityVoteProcessingBinding

    private var voteNumber: Int = -1
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vote_processing)
        binding.lifecycleOwner = this

        voteNumber = intent?.getIntExtra(VOTE_NUMBER, -1)!!

        initButtons()
        changeStatusView()
    }

    private fun changeStatusView() {
        val statusList = listOf(binding.option1, binding.option2, binding.option3, binding.option4)

        val handler = Handler(Looper.getMainLooper())
        val typeface = ResourcesCompat.getFont(this, R.font.roboto_mono_bold)

        var processedCount = 0

        // Iterate through the list with a delay
        for ((index, view) in statusList.withIndex()) {
            handler.postDelayed({
                // Update the view after the delay
                view.background = resources.getDrawable(R.drawable.section_done_background)
                view.text = resources.getString(R.string.done)
                view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0,0,0)
                view.typeface = typeface

                processedCount++

                if (processedCount == statusList.size) {
                    // All elements processed, do something here
                    handleEndOfHandler()
                }

            }, (index + 1) * 500L) //
        }
    }

    private fun handleEndOfHandler() {
        binding.header.text = resources.getString(R.string.submited_vote_header)
        //binding.description.text = resources.getString(R.string.submited_vote_description)
        binding.icon.setAnimation(R.raw.checkbox_succes)
        binding.icon.repeatCount = 0
        binding.icon.playAnimation()
    }


    private fun initButtons() {
        clickHelper.addViews(binding.backButton)

        clickHelper.setOnClickListener {
            when (it.id) {
                binding.backButton.id -> {
                    finish()
                }
            }
        }
    }

    companion object {
        const val VOTE_NUMBER = "VOTE_NUMBER"
    }

}