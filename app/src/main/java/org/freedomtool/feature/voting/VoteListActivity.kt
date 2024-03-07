package org.freedomtool.feature.voting

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.rxkotlin.addTo
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.data.datasource.api.VotingProvider
import org.freedomtool.data.models.VotingData
import org.freedomtool.databinding.ActivityVoteListBinding
import org.freedomtool.feature.voting.logic.VoteAdapter
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.LocalizationManager
import org.freedomtool.utils.Navigator
import org.freedomtool.utils.ObservableTransformers
import org.freedomtool.utils.unSafeLazy


class VoteListActivity : BaseActivity() {

    private lateinit var binding: ActivityVoteListBinding

    private var voteList: List<VotingData> = listOf()
    private var voteListEnded: List<VotingData> = listOf()

    private val voteAdapter by unSafeLazy {
        VoteAdapter(clickHelper, Navigator.from(this), SecureSharedPrefs)
    }

    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vote_list)
        binding.lifecycleOwner = this

        if (savedInstanceState != null) {
            restoreFromMemory(savedInstanceState)
        } else {
            subscribeToVotes()
        }

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.primary_button_color)



        binding.recyclerViewVote.adapter = voteAdapter
        val manager = LinearLayoutManager(this)
        binding.recyclerViewVote.layoutManager = manager

        initButtons()
    }

    override fun onResume() {
        if (!SecureSharedPrefs.getIsPassportScanned(this)) {
            binding.clearAllData.visibility = View.GONE
            binding.separator.visibility = View.GONE
        }else {
            binding.clearAllData.visibility = View.VISIBLE
            binding.separator.visibility = View.VISIBLE
        }
        super.onResume()
    }

    private fun subscribeToVotes() {

        VotingProvider.getVotes(apiProvider)
            .compose(ObservableTransformers.defaultSchedulersSingle())
            .doOnSuccess {
                binding.loader.visibility = View.GONE
            }
            .doOnSubscribe {
                binding.loader.visibility = View.VISIBLE
            }.subscribe({
                voteList = it.first
                voteListEnded = it.second
                voteAdapter.addAll(voteList)

            }, {
                subscribeToVotes()
            }).addTo(compositeDisposable)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val gson = Gson()
        val json = gson.toJson(voteList)
        val json_ended = gson.toJson(voteListEnded)
        outState.putString(SAVED_VOTES_LIST, json)
        outState.putString(SAVED_VOTES_LIST_ENDED, json_ended)
        Log.i("Save state", "HERE")
    }

    private fun restoreFromMemory(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val json = savedInstanceState.getString(SAVED_VOTES_LIST)
        val jsonEnded = savedInstanceState.getString(SAVED_VOTES_LIST_ENDED)
        val type = object : TypeToken<List<VotingData>>() {}.type
        if (json!!.isNotEmpty()) {
            Log.i("Restore state", "HERE")
            val gson = Gson()
            voteList = gson.fromJson(json, type)
            voteListEnded = gson.fromJson(jsonEnded, type)
            voteAdapter.clear()
            voteAdapter.addAll(voteList)
            return
        }
        Log.i("Unluck", "Unluck")
        subscribeToVotes()
    }

    private fun initButtons() {
        clickHelper.addViews(
            binding.changeLanguage,
            binding.clearAllData,
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
            }
        }

        binding.buttonGroupRoundSelectedButton.setOnPositionChangedListener {
            if (it == 0) {
                voteAdapter.clear()
                voteAdapter.addAll(voteList)
                //binding.noPollsText.visibility = View.GONE
                return@setOnPositionChangedListener
            }
            //binding.noPollsText.visibility = View.VISIBLE
            voteAdapter.clear()
            voteAdapter.addAll(voteListEnded)
        }

    }


    private fun clearAllData() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_all_data_header))
            .setMessage(resources.getString(R.string.delete_all_data_message))
            .setPositiveButton(resources.getString(R.string.button_ok)) { _, _ ->
                compositeDisposable.clear()
                SecureSharedPrefs.clearAllData(this)
                recreate()
            }
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private companion object {
        const val SAVED_VOTES_LIST = "saved_votes_list"
        const val SAVED_VOTES_LIST_ENDED = "saved_votes_list_ended"
    }

}
