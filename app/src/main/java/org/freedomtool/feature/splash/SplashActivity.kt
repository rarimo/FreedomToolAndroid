package org.freedomtool.feature.splash

import android.os.Bundle
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.logic.persistance.SecureSharedPrefs
import org.freedomtool.utils.Navigator


class SplashActivity : BaseActivity() {

    override fun onCreateAllowed(savedInstanceState: Bundle?) {

        if (!SecureSharedPrefs.isFirstLaunch(this)) {
            Navigator.from(this).openCheckPinCode()
            finish()
            return
        }

        Navigator.from(this).openStart()
        finish()


    }
}