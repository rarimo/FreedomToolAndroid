package org.freedomtool.feature.onBoarding

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.freedomtool.R
import org.freedomtool.base.view.BaseFragment
import org.freedomtool.databinding.FragmentResultDataPassportBinding
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.Navigator
import org.freedomtool.utils.nfc.ImageUtil
import org.freedomtool.utils.nfc.model.EDocument
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ResultDataPassportFragment : BaseFragment() {


    private lateinit var binding: FragmentResultDataPassportBinding
    var eDocumentData: EDocument? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_result_data_passport, container, false
        )

        val image: Bitmap = ImageUtil.scaleImage(eDocumentData!!.personDetails!!.faceImage!!)!!

        binding.viewPhoto.setImageBitmap(image)

        binding.name.text = eDocumentData!!.personDetails!!.name

        if (eDocumentData!!.personDetails!!.gender == "MALE") {
            binding.gender.text = resources.getString(R.string.male)
        } else {
            binding.gender.text = resources.getString(R.string.female)
        }

        binding.documentNumber.text = eDocumentData!!.personDetails!!.serialNumber
        binding.dateOfExpiry.text = eDocumentData!!.personDetails!!.expiryDate
        binding.dateOfBirth.text = eDocumentData!!.personDetails!!.birthDate
        binding.nationality.text = eDocumentData!!.personDetails!!.nationality

        SecureSharedPrefs.saveDateOfBirth(
            requireContext(),
            eDocumentData!!.personDetails!!.birthDate!!
        )
        SecureSharedPrefs.saveIssuerAuthority(
            requireContext(),
            eDocumentData!!.personDetails!!.issuerAuthority!!
        )

        if (checkExpiryDate(eDocumentData!!.personDetails!!.expiryDate!!)) {
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.expiry_date_error))
                .setPositiveButton(resources.getString(R.string.button_ok)) { dialog, which ->

                    requireActivity().finish()
                }.setOnDismissListener {

                    requireActivity().finish()
                }
                .show()
            binding.confirmButton.visibility = View.INVISIBLE
        }


        initButtons()
        return binding.root
    }

    private fun checkExpiryDate(expiryDate: String): Boolean {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val parsedExpiryDate = LocalDate.parse(expiryDate, formatter)
        return parsedExpiryDate.isBefore(currentDate)
    }

    private fun initButtons() {
        clickHelper.addViews(binding.confirmButton)
        clickHelper.setOnClickListener {
            when (it.id) {
                binding.confirmButton.id -> {
                    Navigator.from(this).openConfirmation(eDocumentData!!)
                    requireActivity().finish()
                }
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ResultDataPassportFragment()
    }
}