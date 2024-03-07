package org.freedomtool.feature.voting.logic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.freedomtool.data.models.VotingData
import org.freedomtool.databinding.LayoutCardManifestBinding
import org.freedomtool.databinding.LayoutCardVotingBinding
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.BaseAdapter
import org.freedomtool.utils.ClickHelper
import org.freedomtool.utils.Navigator
import org.freedomtool.utils.resolveDays

class VoteAdapter(
    val clickHelper: ClickHelper,
    val navigator: Navigator,
    val secureSharedPreferences: SecureSharedPrefs
) : BaseAdapter<VotingData, RecyclerView.ViewHolder>() {

    private val manifestType = 1
    private val voteType = 2
    override fun getItemViewType(position: Int): Int {
        if (getItems()[position].isManifest) {
            return manifestType
        }
        return voteType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            manifestType -> {
                val binding = LayoutCardManifestBinding.inflate(inflater, parent, false)
                ManifestViewHolder(binding)
            }

            voteType -> {
                val binding = LayoutCardVotingBinding.inflate(inflater, parent, false)
                VoteViewHolder(binding)
            }

            else -> {
                throw IllegalStateException("Unknown vote type")
            }
        }

    }


    override fun getItemCount(): Int {
        return getItems().size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            manifestType -> {
                val manifestHolder = holder as ManifestViewHolder
                manifestHolder.bind(getItem(position))
            }

            voteType -> {
                val voteHolder = holder as VoteViewHolder
                voteHolder.bind(getItem(position))
            }
        }
    }

    inner class VoteViewHolder(val binding: LayoutCardVotingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var data: VotingData
        private var daysLeft: Int = 0
        private val context = binding.root.context

        init {
            binding.root.setOnClickListener(::onClick)
        }

        private fun onClick(view: View) {
            clickHelper.canInvokeClick(::onClickAllowed)
        }

        private fun onClickAllowed() {
            if (data.isPassportRequired) {
                if (!secureSharedPreferences.getIsPassportScanned(context)) {
                    navigator.openVerificationPage(data)
                    return
                }
            }
            navigator.openVotePage(data)

        }

        fun bind(voteData: VotingData) {
            data = voteData
            val time = resolveDays(context, data.dueDate!!)
            binding.date = time
            binding.data = voteData
        }
    }

    inner class ManifestViewHolder(val binding: LayoutCardManifestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private lateinit var data: VotingData
        private val context = binding.root.context

        init {
            binding.root.setOnClickListener(::onClick)
        }

        private fun onClick(view: View) {
            clickHelper.canInvokeClick(::onClickAllowed)
        }

        private fun onClickAllowed() {
            if (SecureSharedPrefs.checkIsVoted(context, data.contractAddress!!)) {
                navigator.openSignedManifest(data)
                return
            }
            navigator.openManifestPage(data)
        }

        fun bind(voteData: VotingData) {
            data = voteData
            binding.data = voteData
            val time = resolveDays(context, data.dueDate!!)
            binding.date = time
        }
    }


}