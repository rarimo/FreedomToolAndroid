package org.freedomtool.feature.voting

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import io.reactivex.rxkotlin.addTo
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.data.models.VotingData
import org.freedomtool.databinding.ActivityVoteProcessingBinding
import org.freedomtool.feature.onBoarding.logic.GenerateVerifiableCredential
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.Navigator
import org.freedomtool.utils.ObservableTransformers


class VoteProcessingActivity : BaseActivity() {

    private lateinit var binding: ActivityVoteProcessingBinding

    private lateinit var votingData: VotingData
    private lateinit var statusList: List<MaterialTextView>

    private lateinit var selectedContract: String
    private var isSigned = false
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vote_processing)
        binding.lifecycleOwner = this

        votingData = intent?.getParcelableExtra(VOTE_DATA)!!

        val referendumContract = intent?.getStringExtra(VOTE_REFERENDUM_CONTRACT)

        if (referendumContract != null) {
            selectedContract = referendumContract
        } else {
            selectedContract = votingData.contractAddress!!
        }


        statusList = listOf(binding.option1, binding.option2, binding.option3, binding.option4)
        initButtons()
        changeStatusView()
    }

    private fun changeStatusView() {

        GenerateVerifiableCredential().register(this, apiProvider, selectedContract, votingData.contractAddress)
            .compose(ObservableTransformers.defaultSchedulers()).subscribe({
                updateLoading(statusList[it])
            }, {


                Log.e("Error during processing", it.message.toString(), it)

                if (it.message.isNullOrEmpty()) {
                    handleUnknownError()
                    return@subscribe
                }

                if ((it.message as String).contains("no non-revoked credentials found")) {
                    handleNoneReworkedCredError()
                    return@subscribe
                }

                if ((it.message as String).contains("user already registered")) {
                    val identity = GenerateVerifiableCredential().createIdentity(
                        this, apiProvider = apiProvider
                    )!!
                    SecureSharedPrefs.saveVotedAddress(this, identity.nullifierHex, selectedContract)
                    handleAlreadyRegisteredError()
                    return@subscribe
                }

                handleUnknownError()

            }, {
                handleEndOfHandler()
            }).addTo(compositeDisposable)

    }

    private fun updateLoading(view: MaterialTextView) {

        val typeface = ResourcesCompat.getFont(this, R.font.roboto_mono_bold)
        view.background = resources.getDrawable(R.drawable.section_done_background)
        view.text = resources.getString(R.string.done)
        view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0)
        view.typeface = typeface
    }

    private fun handleAlreadyRegisteredError() {
        MaterialAlertDialogBuilder(this).setTitle(getString(R.string.you_already_registered))
            .setPositiveButton(resources.getString(R.string.button_ok)) { dialog, which ->

                Navigator.from(this).openSignedManifest(votingData)
                finish()
            }.setOnDismissListener {

                Navigator.from(this).openSignedManifest(votingData)
                finish()
            }.show()
    }

    private fun handleNoneReworkedCredError() {
        MaterialAlertDialogBuilder(this).setTitle(getString(R.string.cant_verify_multiple_device))
            .setPositiveButton(resources.getString(R.string.button_ok)) { dialog, which ->
                this.finish()
            }.setOnDismissListener {
                this.finish()
            }.show()
    }


    private fun handleUnknownError() {
        MaterialAlertDialogBuilder(this).setTitle(getString(R.string.check_back_later))
            .setPositiveButton(resources.getString(R.string.button_ok)) { dialog, which ->
                this.finish()
            }.setOnDismissListener {
                this.finish()
            }.show()
    }

    private fun handleEndOfHandler() {
        binding.header.text = resources.getString(R.string.submited_vote_header)
        //binding.description.text = resources.getString(R.string.submited_vote_description)
        binding.icon.setAnimation(R.raw.checkbox_succes)
        binding.icon.repeatCount = 0
        binding.icon.playAnimation()

        binding.separator.visibility = View.GONE
        binding.hint.visibility = View.GONE
        binding.viewPetition.visibility = View.VISIBLE
        isSigned = true
    }

    override fun onBackPressed() {
        if (isSigned) {
            finish()
            Navigator.from(this).openSignedManifest(votingData)
        } else {
            finish()
        }
    }


    private fun initButtons() {
        clickHelper.addViews(binding.backButton, binding.viewPetition)

        clickHelper.setOnClickListener {
            when (it.id) {
                binding.backButton.id -> {
                    if (isSigned) {
                        finish()
                        Navigator.from(this).openSignedManifest(votingData)
                    } else {
                        finish()
                    }
                }

                binding.viewPetition.id -> {
                    finish()
                    Navigator.from(this).openSignedManifest(votingData)
                }
            }
        }
    }

    companion object {
        const val VOTE_DATA = "VOTE_DATA"
        const val VOTE_REFERENDUM_CONTRACT = "VOTE_REFERENDUM_CONTRACT"
    }

}