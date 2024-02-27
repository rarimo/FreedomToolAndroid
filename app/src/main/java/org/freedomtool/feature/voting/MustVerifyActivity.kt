package org.freedomtool.feature.voting

import android.Manifest
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.data.models.VotingData
import org.freedomtool.databinding.ActivityMustVerifyBinding
import org.freedomtool.databinding.LayoutRequirementItemBinding
import org.freedomtool.utils.Navigator
import org.freedomtool.utils.daysBetween
import org.freedomtool.utils.nfc.PermissionUtil
import java.util.Date

class MustVerifyActivity : BaseActivity() {

    private lateinit var voteData: VotingData

    private lateinit var binding: ActivityMustVerifyBinding
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_must_verify)
        binding.lifecycleOwner = this

        voteData = intent?.getParcelableExtra(VOTING_DATA)!!

        initViews()
        initButtons()
    }

    private fun initViews(){
        binding.header.text = voteData.header
        binding.data = voteData
        binding.dataOfVoting.text = getString(R.string.begins_in_x_days, daysBetween(Date(voteData.dueDate!! * 1000)).toString() )

        if(voteData.requirements == null)
        {
            return
        }

        if (voteData.requirements?.age != null) {
            addReq(getString(R.string.are_x_years_old_on_or_before_election_day, voteData.requirements?.age.toString()))
        }

        if (voteData.requirements?.nationality != null){
            addReq(resources.getString(R.string.is_citizen, voteData.requirements!!.nationality!!))
        }
    }

    private fun initButtons() {
        clickHelper.addViews(binding.mainButton, binding.backButton)
        clickHelper.setOnClickListener {
            when(it.id) {
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

    private fun addReq(text: String){
        val binding: LayoutRequirementItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.layout_requirement_item, binding.reqContainer, true)
        binding.textContent = text
    }

    companion object {
        const val VOTING_DATA = "VOTING_DATA"
    }
}