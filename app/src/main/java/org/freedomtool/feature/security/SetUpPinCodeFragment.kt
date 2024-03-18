package org.freedomtool.feature.security

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import org.freedomtool.R
import org.freedomtool.base.view.BaseFragment
import org.freedomtool.databinding.FragmentSetUpPinCodeBinding

class SetUpPinCodeFragment : BaseFragment() {

    private lateinit var binding: FragmentSetUpPinCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_set_up_pin_code, container, false
        )

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SetUpPinCodeFragment()
    }
}