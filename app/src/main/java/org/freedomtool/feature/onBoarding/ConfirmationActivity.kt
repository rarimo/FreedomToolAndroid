package org.freedomtool.feature.onBoarding

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.airbnb.lottie.LottieDrawable
import io.reactivex.rxkotlin.addTo
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.databinding.ActivityConfirmingBinding
import org.freedomtool.feature.onBoarding.logic.GenerateVerifyableCredenial
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.Navigator
import org.freedomtool.utils.ObservableTransformers
import org.freedomtool.utils.TimerManager

class ConfirmationActivity : BaseActivity() {

    lateinit var binding: ActivityConfirmingBinding
    private lateinit var expiriyDate: String

    private lateinit var nextTimer: TimerManager
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirming)
        binding.lifecycleOwner = this

        expiriyDate = intent?.getStringExtra(EXPIRY_DATE)!!


        GenerateVerifyableCredenial().generateIdentity(this, this.apiProvider)
            .compose(ObservableTransformers.defaultSchedulersSingle())
            .subscribe({
                SecureSharedPrefs.savePassportData(this)
                setReady()
            }, {
                toastManager.long(R.string.not_able_to_generate_proof)
                Log.e("Error", it.message.toString())
                finish()
            })
            .addTo(compositeDisposable)


    }


    private fun setReady() {
        binding.animation.setAnimation(R.raw.checkbox_succes)
        binding.animation.repeatCount = 0
        binding.animation.playAnimation()

        binding.titleText.text = getString(R.string.all_done)

        binding.tipText.text = getString(R.string.all_done_description)

        nextTimer = TimerManager {
            Navigator.from(this).openVote()
            finish()
        }

        nextTimer.startTimer(5000)

    }


    companion object {
        const val EXPIRY_DATE = "expiry_date"
    }

}


