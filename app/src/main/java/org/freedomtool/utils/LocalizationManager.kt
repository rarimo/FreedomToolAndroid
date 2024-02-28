package org.freedomtool.utils

import android.content.Context
import org.freedomtool.logic.persistance.SecureSharedPrefs
import java.util.Locale

object LocalizationManager {
    fun getLocale(context: Context?): Locale? {
        if(context == null) return null
        val currentLocale = SecureSharedPrefs.getSavedLocale(context)
        if (currentLocale.isNullOrBlank()) {
            return context.resources.configuration.locales.get(0)
        }
        return Locale(currentLocale)
    }

    fun switchLocale(context: Context) {

        val currentLocale: Locale = if(getLocale(context) == null) {
            context.resources.configuration.locales.get(0)
        } else{
            getLocale(context)!!
        }

        if(currentLocale.language == "ru"){
            saveLocale(context, "en")
            return
        }
        saveLocale(context, "ru")
    }

    fun saveLocale(context: Context, locale: String) {
        SecureSharedPrefs.saveLocale(context, locale)
    }

}