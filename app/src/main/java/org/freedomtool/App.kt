package org.freedomtool

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.freedomtool.di.ApiProviderModule
import org.freedomtool.di.AppComponent
import org.freedomtool.di.DaggerAppComponent
import org.freedomtool.di.LocaleManagerModule
import org.freedomtool.di.UtilsModule
import org.freedomtool.utils.AppLocaleManager
import java.io.IOException
import java.security.Security

class App : Application() {
    lateinit var appComponent: AppComponent
        private set

    private lateinit var mLocaleManager: AppLocaleManager

    private val localeManager: AppLocaleManager
        get() = mLocaleManager


    override fun onCreate() {
        super.onCreate()
        initLocale()
        initDi()
        Security.insertProviderAt(BouncyCastleProvider(), 1)
        initRxErrorHandler()
        setupBouncyCastle()
    }

    private fun setupBouncyCastle() {
        val provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
            ?: // Web3j will set up the provider lazily when it's first used.
            return
        if (provider::class.java.equals(BouncyCastleProvider::class.java)) {
            // BC with same package name, shouldn't happen in real life.
            throw IllegalStateException("BC with same package name")
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }


    private fun initLocale() {
        mLocaleManager = AppLocaleManager(this, getAppPreferences())
        localeManager.initLocale()
    }

    private fun getAppPreferences(): SharedPreferences {
        return getSharedPreferences("App", Context.MODE_PRIVATE)
    }

    private fun initRxErrorHandler() {
        RxJavaPlugins.setErrorHandler {
            var e = it
            if (e is UndeliverableException) {
                e = e.cause
            }
            if (e is IOException) {
                // fine, irrelevant network problem or API that throws on cancellation
                return@setErrorHandler
            }
            if (e is InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return@setErrorHandler
            }
            if ((e is NullPointerException) || (e is IllegalArgumentException)) {
                // that's likely a bug in the application
                Thread.currentThread().uncaughtExceptionHandler?.uncaughtException(
                    Thread.currentThread(), e
                )
                return@setErrorHandler
            }
            if (e is IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().uncaughtExceptionHandler?.uncaughtException(
                    Thread.currentThread(), e
                )
                return@setErrorHandler
            }
            Log.w("RxErrorHandler", "Undeliverable exception received, not sure what to do", e)
        }
    }

    private fun initDi() {
        appComponent = DaggerAppComponent.builder()
            .utilsModule(
                UtilsModule(localeManager.getLocalizeContext(this))
            ).localeManagerModule(LocaleManagerModule(localeManager))
            .apiProviderModule(ApiProviderModule())
            .build()
        appComponent.inject(this)
    }

}