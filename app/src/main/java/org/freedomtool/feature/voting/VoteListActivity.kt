package org.freedomtool.feature.voting

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.rxkotlin.addTo
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.data.datasource.api.VotingProvider
import org.freedomtool.data.models.VotingData
import org.freedomtool.databinding.ActivityVoteListBinding
import org.freedomtool.feature.onBoarding.logic.GenerateVerifyableCredenial
import org.freedomtool.feature.voting.logic.VoteAdapter
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.LocalizationManager
import org.freedomtool.utils.Navigator
import org.freedomtool.utils.ObservableTransformers
import org.freedomtool.utils.unSafeLazy


class VoteListActivity : BaseActivity() {

    private lateinit var binding: ActivityVoteListBinding
    private lateinit var voteList: List<VotingData>

    private val voteAdapter by unSafeLazy {
        VoteAdapter(clickHelper, Navigator.from(this), SecureSharedPrefs)
    }

    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vote_list)
        binding.lifecycleOwner = this

        voteList = VotingProvider.getVotes(this)

        voteAdapter.addAll(voteList)
        val manager = LinearLayoutManager(this)

        binding.recyclerViewVote.adapter = voteAdapter
        binding.recyclerViewVote.layoutManager = manager


        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.primary_button_color)

        if (!SecureSharedPrefs.getPassportData(this)) {
            binding.clearAllData.visibility = View.GONE
        }

        initButtons()
    }

    private fun initButtons() {
        clickHelper.addViews(
            binding.changeLanguage,
            binding.clearAllData,
            binding.querry,
            binding.vote
        )
        clickHelper.setOnClickListener {
            when (it.id) {

                binding.clearAllData.id -> {
                    clearAllData()
                }

                binding.changeLanguage.id -> {
                    LocalizationManager.switchLocale(this)
                    recreate()
                }

                binding.querry.id -> {
                    GenerateVerifyableCredenial().signToVoting(this, apiProvider)
                        .compose(ObservableTransformers.defaultSchedulersCompletable())
                        .subscribe({
                            Log.i("Successfully", "nice")
                        }, {
                            throw it
                        }).addTo(compositeDisposable)
                }

                binding.vote.id -> {
                    GenerateVerifyableCredenial().vote(this, apiProvider, "0")
                        .compose(ObservableTransformers.defaultSchedulersCompletable())
                        .subscribe({
                            Log.i("Successfully", "nice")
                        }, {
                            throw it
                        }).addTo(compositeDisposable)
                }
            }
        }

        binding.buttonGroupRoundSelectedButton.setOnPositionChangedListener {
            if (it == 0) {
                voteAdapter.clear()
                voteAdapter.addAll(voteList)
                binding.noPollsText.visibility = View.GONE
                return@setOnPositionChangedListener
            }
            binding.noPollsText.visibility = View.VISIBLE
            voteAdapter.clear()
        }

    }


    private fun clearAllData() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_all_data_header))
            .setMessage(resources.getString(R.string.delete_all_data_message))
            .setPositiveButton(resources.getString(R.string.button_ok)) { _, _ ->
                SecureSharedPrefs.clearAllData(this)
                recreate()
            }
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}
