package org.freedomtool.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import org.freedomtool.logic.persistance.SecureSharedPrefs
import java.util.Locale

class AppLocaleManager(
    private val context: Context,
    private val preferences: SharedPreferences
) {
    private val defaultLocale = "en"
        .let(::Locale)

    val availableLocales = listOf(
        Locale("en"),
        Locale("ru"),
    )

    fun getLocale(): Locale = loadLocale() ?: defaultLocale

    fun setLocale(locale: Locale) {
        if (locale == getLocale()) {
            return
        }
        saveLocale(locale)
        applyLocale(locale)
    }

    /**
     * Applies locale based on the stored or default one.
     * Call this method on app init
     */
    fun initLocale() {
        applyLocale(getLocale())
    }

    /**
     * @return [Context] set up to use current locale
     */
    fun getLocalizeContext(baseContext: Context): Context {
        return LocalizedContextWrapper.wrap(baseContext, getLocale())
    }

    private fun applyLocale(locale: Locale) {
        LocalizedContextWrapper.wrap(context, locale)
        Locale.setDefault(locale)
    }

    private fun loadLocale(): Locale? {
        return preferences
            .getString(CURRENT_LOCALE_KEY, "")
            ?.takeIf(String::isNotEmpty)
            ?.let { Locale(it) }
    }

    fun saveLocale(locale: Locale) {
        preferences
            .edit()
            .putString(CURRENT_LOCALE_KEY, locale.language)
            .apply()
    }

    private companion object {
        private const val CURRENT_LOCALE_KEY = "current_locale"
    }
}

