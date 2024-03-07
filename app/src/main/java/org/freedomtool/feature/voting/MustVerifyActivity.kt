package org.freedomtool.feature.voting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.data.models.VotingData
import org.freedomtool.databinding.ActivityMustVerifyBinding
import org.freedomtool.databinding.LayoutRequirementDeclineItemBinding
import org.freedomtool.databinding.LayoutRequirementItemBinding
import org.freedomtool.databinding.LayoutRequirementOkItemBinding
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.Navigator
import org.freedomtool.utils.calculateAge
import org.freedomtool.utils.nfc.PermissionUtil
import org.freedomtool.utils.resolveDays

class MustVerifyActivity : BaseActivity() {

    private lateinit var voteData: VotingData

    private lateinit var binding: ActivityMustVerifyBinding
    private var isAllowedToSign = true
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_must_verify)
        binding.lifecycleOwner = this

        voteData = intent?.getParcelableExtra(VOTING_DATA)!!

        //initViews()
        //isAllowedToSign()
    }

    override fun onResume() {
        clearAllRequirements()
        initViews()
        isAllowedToSign()
        initButtons()
        super.onResume()
    }

    private fun allowToUser() {
        binding.aprovedView.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.primary_button_color)
        binding.aprovedView.setTextColor(resources.getColor(R.color.black))
        binding.aprovedView.text = getString(R.string.you_can_vote)
    }

    private fun declineToUser() {
        binding.aprovedView.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.error_color)
        binding.aprovedView.setTextColor(resources.getColor(R.color.white))
        binding.aprovedView.text = getString(R.string.you_can_t_vote)
        binding.mainButton.visibility = View.INVISIBLE
    }

    private fun initViews() {
        binding.header.text = voteData.header
        binding.data = voteData
        val time = resolveDays(this, voteData.dueDate!!)
        binding.dataOfVoting.text = time

        if (voteData.requirements == null) {
            return
        }

        if (SecureSharedPrefs.getIsPassportScanned(this)) {
            handleIsPassportScanned()
            return
        }


        if (voteData.requirements?.age != null) {
            addRequirements(
                getString(
                    R.string.are_x_years_old_on_or_before_election_day,
                    voteData.requirements?.age.toString()
                )
            )
        }

        if (voteData.requirements?.getNationality() != null) {
            addRequirements(
                resources.getString(
                    R.string.is_citizen,
                    voteData.requirements!!.getNationality()
                )
            )
        }
    }

    private fun handleIsPassportScanned() {
        if (!SecureSharedPrefs.getIsPassportScanned(this))
            return

        if (voteData.requirements?.age != null) {

            val date = SecureSharedPrefs.getDateOfBirth(this)
            val age = calculateAge(date!!)

            if (age < voteData.requirements!!.age!!) {
                isAllowedToSign = false
                addDecline(
                    getString(
                        R.string.are_x_years_old_on_or_before_election_day,
                        voteData.requirements?.age.toString()
                    )
                )
            } else {
                addAccept(
                    getString(
                        R.string.are_x_years_old_on_or_before_election_day,
                        voteData.requirements?.age.toString()
                    )
                )
            }

        }

        if (voteData.requirements?.nationality!!.isNotEmpty()) {

            val issuer = SecureSharedPrefs.getIssuerAuthority(this)!!
            if (voteData.requirements!!.isInList(issuer)) {
                addAccept(
                    resources.getString(
                        R.string.is_citizen,
                        voteData.requirements!!.getNationality()
                    )
                )
            } else {
                isAllowedToSign = false
                addDecline(
                    resources.getString(
                        R.string.is_citizen,
                        voteData.requirements!!.getNationality()
                    )
                )
            }

        }
    }

    private fun initButtons() {
        clickHelper.removeOnClickListener()
        if (isAllowedToSign && SecureSharedPrefs.getIsPassportScanned(this)) {
            clickHelper.addViews(binding.mainButton, binding.backButton)
            clickHelper.setOnClickListener {
                when (it.id) {
                    binding.mainButton.id -> {
                        Navigator.from(this).openVoteProcessing(voteData)
                        finish()
                    }

                    binding.backButton.id -> {
                        finish()
                    }
                }
            }
            return
        }
        clickHelper.addViews(binding.mainButton, binding.backButton)
        clickHelper.setOnClickListener {
            when (it.id) {
                binding.mainButton.id -> {
                    requestPermissionForCamera()
                }

                binding.backButton.id -> {
                    finish()
                }
            }
        }
    }

    private fun requestPermissionForCamera() {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        val isPermissionGranted = PermissionUtil.hasPermissions(this, *permissions)
        if (!isPermissionGranted) {

            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.permission_title))
                .setMessage(resources.getString(R.string.permission_description))
                .setPositiveButton(resources.getString(R.string.button_ok)) { dialog, which ->
                    ActivityCompat.requestPermissions(
                        this,
                        permissions,
                        PermissionUtil.REQUEST_CODE_MULTIPLE_PERMISSIONS
                    )
                }
                .show()


        } else {
            Navigator.from(this).openScan()
        }
    }

    private fun addRequirements(text: String) {
        val binding: LayoutRequirementItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.layout_requirement_item,
            binding.reqContainer,
            true
        )
        binding.textContent = text
    }

    private fun clearAllRequirements() {
        binding.reqContainer.removeAllViews()
    }

    private fun addAccept(text: String) {
        val binding: LayoutRequirementOkItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.layout_requirement_ok_item,
            binding.reqContainer,
            true
        )

        binding.textContent = text
    }

    private fun isAllowedToSign() {

        if (SecureSharedPrefs.getIsPassportScanned(this)) {
            if (!voteData.isActive) {
                binding.mainButton.text = getString(R.string.sign)
                declineToUser()
                return
            }
        }

        if (!isAllowedToSign) {
            declineToUser()
            binding.mainButton.visibility = View.GONE
            return
        }
        if (SecureSharedPrefs.getIsPassportScanned(this)) {
            allowToUser()
        }

        binding.mainButton.text = getString(R.string.sign)

    }

    private fun addDecline(text: String) {
        val binding: LayoutRequirementDeclineItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.layout_requirement_decline_item,
            binding.reqContainer,
            true
        )

        binding.textContent = text
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionUtil.REQUEST_CODE_MULTIPLE_PERMISSIONS) {
            val result = grantResults[0]
            if (result == PackageManager.PERMISSION_DENIED) {
                if (!PermissionUtil.showRationale(this, permissions[0])) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.setData(uri)
                    startActivityForResult(
                        intent,
                        APP_SETTINGS_ACTIVITY_REQUEST_CODE
                    )
                } else {
                    requestPermissionForCamera()
                }
            } else if (result == PackageManager.PERMISSION_GRANTED) {
                Navigator.from(this).openScan()
            }
        }
    }

    companion object {
        const val APP_SETTINGS_ACTIVITY_REQUEST_CODE = 523
        const val VOTING_DATA = "VOTING_DATA"
    }
}