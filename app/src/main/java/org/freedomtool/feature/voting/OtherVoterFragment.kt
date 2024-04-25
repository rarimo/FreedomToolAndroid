package org.freedomtool.feature.voting

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.freedomtool.R
import org.freedomtool.base.view.BaseBottomSheetDialog
import org.freedomtool.databinding.FragmentOtherVoterBinding
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.Navigator


class OtherVoterFragment : BaseBottomSheetDialog() {


    private lateinit var binding: FragmentOtherVoterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_other_voter, container, false
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        initButtons()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
        }

    }

    private fun initButtons() {
        clickHelper.addViews(binding.closeBtn, binding.nextVoterButton, binding.cancelButton)

        clickHelper.setOnClickListener {
            when(it.id) {
                binding.cancelButton.id -> {
                    dismiss()
                }
                binding.closeBtn.id -> {
                    dismiss()
                }

                binding.nextVoterButton.id -> {
                    dismiss()
                    SecureSharedPrefs.clearAllData(requireContext())
                    requireActivity().finish()
                    Navigator.from(requireContext()).openScan()
                }
            }
        }
    }

}