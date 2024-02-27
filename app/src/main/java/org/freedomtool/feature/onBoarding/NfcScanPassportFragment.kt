package org.freedomtool.feature.onBoarding


import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import org.freedomtool.R
import org.freedomtool.base.view.BaseFragment
import org.freedomtool.databinding.FragmentNfcScanPassportBinding
import org.jmrtd.lds.icao.MRZInfo


class NfcScanPassportFragment : BaseFragment() {

    private lateinit var binding: FragmentNfcScanPassportBinding
    private lateinit var adapter: NfcAdapter
    private lateinit var passportNumber: String
    private lateinit var expirationDate: String
    private lateinit var birthDate: String


    fun setLoading(){
        binding.imagePreview.visibility = View.INVISIBLE
        binding.loader.visibility = View.VISIBLE
        binding.nfcTipScanning.text = getString(R.string.please_wait)

    }
    fun setScanMe() {
        binding.imagePreview.visibility = View.VISIBLE
        binding.loader.visibility = View.INVISIBLE
        binding.imagePreview.background = (resources.getDrawable(R.drawable.gif_scan_nfc))
        binding.nfcTipScanning.text = getString(R.string.nfc_scan_tip)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_nfc_scan_passport, container, false
        )

        val mrzInfo = (arguments?.getSerializable(MRZ_INFO_PARAMS) as? MRZInfo)
            ?: throw IllegalStateException("${this::class.java.simpleName}: No secret key found")

        setMrzData(mrzInfo)
        return binding.root
    }

    private fun setMrzData(mrzInfo: MRZInfo) {
        adapter = NfcAdapter.getDefaultAdapter(requireContext())

        passportNumber = mrzInfo.documentNumber
        expirationDate = mrzInfo.dateOfExpiry
        birthDate = mrzInfo.dateOfBirth
    }

    companion object {

        const val MRZ_INFO_PARAMS = "MRZ_INFO_PARAMS"
        @JvmStatic
        fun newInstance(mrzInfo: MRZInfo) =
            NfcScanPassportFragment().also {
                it.arguments = bundleOf(MRZ_INFO_PARAMS to mrzInfo)
            }
    }
}