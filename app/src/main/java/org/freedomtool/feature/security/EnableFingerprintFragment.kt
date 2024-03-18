package org.freedomtool.feature.security

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import org.freedomtool.R
import org.freedomtool.base.view.BaseFragment
import org.freedomtool.databinding.FragmentEnableFingerprintBinding
import org.freedomtool.feature.onBoarding.NfcScanPassportFragment
import org.jmrtd.lds.icao.MRZInfo

class EnableFingerprintFragment : BaseFragment() {

    private lateinit var binding: FragmentEnableFingerprintBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_enable_fingerprint, container, false
        )


        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            EnableFingerprintFragment()
    }
}