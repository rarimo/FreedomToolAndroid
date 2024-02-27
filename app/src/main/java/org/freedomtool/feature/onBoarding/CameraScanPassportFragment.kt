package org.freedomtool.feature.onBoarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import org.freedomtool.R
import org.freedomtool.base.view.BaseFragment
import org.freedomtool.databinding.FragmentCameraScanPassportBinding
import org.freedomtool.mlkit.text.TextRecognitionProcessor.ResultListener
import org.freedomtool.utils.nfc.model.DocType
import org.freedomtool.utils.Navigator
import org.jmrtd.lds.icao.MRZInfo
import java.io.IOException


class CameraScanPassportFragment : BaseFragment(), ResultListener {

    private lateinit var binding: FragmentCameraScanPassportBinding
    private var cameraSource: org.freedomtool.mlkit.camera.CameraSource? = null
    private var preview: org.freedomtool.mlkit.camera.CameraSourcePreview? = null
    private var graphicOverlay: org.freedomtool.mlkit.other.GraphicOverlay? = null
    private val docType = DocType.PASSPORT
    private val TAG = CameraScanPassportFragment::class.java.simpleName
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_camera_scan_passport, container, false
        )

        preview = binding.cameraSourcePreview
        graphicOverlay = binding.graphicsOverlay

        createCameraSource()
        startCameraSource()
        initButtons()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        startCameraSource()
    }

    /** Stops the camera.  */
    override fun onPause() {
        super.onPause()
        preview!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cameraSource != null) {
            cameraSource!!.release()
        }
    }

    private fun createCameraSource() {
        if (cameraSource == null) {
            cameraSource = org.freedomtool.mlkit.camera.CameraSource(
                requireActivity(),
                graphicOverlay
            )
            cameraSource!!.setFacing(org.freedomtool.mlkit.camera.CameraSource.CAMERA_FACING_BACK)
        }
        cameraSource!!.setMachineLearningFrameProcessor(
            org.freedomtool.mlkit.text.TextRecognitionProcessor(
                docType,
                this
            )
        )
    }

    private fun initButtons() {
        clickHelper.addViews(binding.privacyText)

        clickHelper.setOnClickListener {
            when(it.id){
                binding.privacyText.id -> {
                    Navigator.from(this).openBrowser("https://freedomtool.org/privacy-policy.html")
                }
            }
        }
    }

    private fun startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null")
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null")
                }
                preview!!.start(cameraSource, graphicOverlay)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource!!.release()
                cameraSource = null
            }
        }
    }


    companion object {
        const val MRZ_INFO = "MRZ_INFO"

        @JvmStatic
        fun newInstance() =
            CameraScanPassportFragment().apply {

            }

    }

    override fun onSuccess(mrzInfo: MRZInfo?) {
        parentFragmentManager.setFragmentResult(MRZ_INFO, bundleOf(MRZ_INFO to mrzInfo))
        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
    }

    override fun onError(exp: Exception?) {
        parentFragmentManager.setFragmentResult(MRZ_INFO, bundleOf(MRZ_INFO to null))

    }


}
