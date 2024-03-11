package org.freedomtool.feature.voting

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import io.noties.markwon.Markwon
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.data.models.VotingData
import org.freedomtool.databinding.ActivityManifestSigningBinding
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
        binding.signedCount.text =
            resources.getQuantityString(R.plurals._x_people_already_signed, votingData.votingCount.toInt(), votingData.votingCount.toInt())
        initButtons()
        isActive()

        val markdown = Markwon.create(this)
        markdown.setMarkdown(binding.markdownText, votingData.description)
    }

    private fun isActive() {
        if (!votingData.isActive) {
            binding.signBtn.visibility = View.GONE
            binding.linearLayoutCompat.visibility = View.INVISIBLE
        }
    }

    private fun initButtons() {
        clickHelper.addViews(binding.cancelButton, binding.signBtn)
        clickHelper.setOnClickListener {
            when (it.id) {
                binding.cancelButton.id -> {
                    finish()
                }

                binding.signBtn.id -> {
                    Navigator.from(this).openVerificationPage(votingData)
                }
            }
        }
    }

    companion object {
        const val MANIFEST_DATA = "MANIFEST_DATA"
    }

}