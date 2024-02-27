package org.freedomtool.utils

import android.content.Context
import android.os.LocaleList
import java.util.Locale

object LocalizedContextWrapper {
    fun wrap(context: Context, locale: Locale?): Context {
        var localContext = context
        val res = localContext.resources
        val configuration = res.configuration

        configuration.setLocale(locale)
        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)
        localContext = localContext.createConfigurationContext(configuration)
        return localContext
    }
}