package org.freedomtool.feature.onBoarding

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import io.reactivex.rxkotlin.addTo
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.databinding.ActivityScanBinding
import org.freedomtool.feature.onBoarding.logic.NfcReaderTask
import org.freedomtool.utils.nfc.model.EDocument
import org.freedomtool.utils.ObservableTransformers
import org.freedomtool.view.util.ToastManager
import org.jmrtd.BACKey
import org.jmrtd.BACKeySpec
import org.jmrtd.lds.icao.MRZInfo
import java.util.Objects

class ScanActivity : BaseActivity() {

    private lateinit var binding: ActivityScanBinding
    private lateinit var adapter: NfcAdapter

    private var passportNumber: String? = null
    private var expirationDate: String? = null
    private var birthDate: String? = null
    private lateinit var eDocument: EDocument
    private var nfcFragment: NfcScanPassportFragment? = null

    private lateinit var mrzInfo: MRZInfo

    private var isScanning: Boolean = false
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan)

        initButton()
        adapter = NfcAdapter.getDefaultAdapter(this)
        listenFragmentsResults()
        startScanCameraPassportFragment()
        binding.stepsText.text = resources.getString(R.string.steps, "1")
    }


    private fun setMrzData(mrzInfo: MRZInfo) {

        this.mrzInfo = mrzInfo
        passportNumber = mrzInfo.documentNumber
        expirationDate = mrzInfo.dateOfExpiry
        birthDate = mrzInfo.dateOfBirth
    }

    private fun fragmentsResultListener(requestKey: String, bundle: Bundle) {
        when (requestKey) {
            CameraScanPassportFragment.MRZ_INFO -> {
                val result = bundle.getSerializable(requestKey) as MRZInfo?
                if (result != null) {
                    setMrzData(result)
                    startScanNfcPassportFragment()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun listenFragmentsResults() {
        val requests = listOf(
            CameraScanPassportFragment.MRZ_INFO,
        )
        requests.forEach { request ->
            supportFragmentManager.setFragmentResultListener(
                request, this, ::fragmentsResultListener
            )
        }
    }

    private fun startScanCameraPassportFragment() {
        isScanning = false
        val fragment = CameraScanPassportFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(binding.fragmentContainer.id, fragment)
            .addToBackStack(null).commit()
    }

    private fun startScanNfcPassportFragment() {

        binding.stepsText.text = resources.getString(R.string.steps, "2")
        val fragment = NfcScanPassportFragment.newInstance(mrzInfo = mrzInfo)
        nfcFragment = fragment
        supportFragmentManager.beginTransaction().replace(binding.fragmentContainer.id, fragment)
            .addToBackStack(null).commit()
        isScanning = true
    }

    private fun startResultDataPassportFragment() {
        binding.stepsText.text = resources.getString(R.string.steps, "3")
        val fragment = ResultDataPassportFragment.newInstance()
        fragment.eDocumentData = eDocument
        supportFragmentManager.beginTransaction().replace(binding.fragmentContainer.id, fragment)
            .addToBackStack(null).commit()
        isScanning = false
    }

    override fun onNewIntent(intent: Intent) {

        if(!isScanning)
            return
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action || NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {

            val tag =
                Objects.requireNonNull(intent.extras)!!.getParcelable<Tag>(NfcAdapter.EXTRA_TAG)!!

            if (passportNumber != null && passportNumber!!.isNotEmpty() && expirationDate != null && expirationDate!!.isNotEmpty() && birthDate != null && birthDate!!.isNotEmpty()) {
                val bacKey: BACKeySpec = BACKey(passportNumber, birthDate, expirationDate)
                val isoDep = IsoDep.get(tag)
                isoDep.timeout = 5 * 1000
                val task = NfcReaderTask(isoDep, bacKey, this)
                nfcFragment?.setLoading()
                task.getResultSubject()
                    .compose(ObservableTransformers.defaultSchedulers())
                    .subscribe({
                        eDocument = it
                        startResultDataPassportFragment()
                    }, {
                        ToastManager(this).short(resources.getString(R.string.error_while_reading))
                        nfcFragment?.setScanMe()
                    }).addTo(compositeDisposable)

                task.execute()
            } else {
                Log.e("ERROR", "LOOKS LIKE ITS NOT WORKING")
            }

        } else {
            super.onNewIntent(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, this.javaClass)
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val filter = arrayOf(arrayOf("android.nfc.tech.IsoDep"))
        adapter!!.enableForegroundDispatch(this, pendingIntent, null, filter)
    }

    private fun initButton() {
        clickHelper.addViews(binding.closeBtn)
        clickHelper.setOnClickListener {
            when (it.id) {
                binding.closeBtn.id -> {
                    finish()
                }
            }
        }
    }


}