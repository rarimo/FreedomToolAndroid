package org.freedomtool.di

import dagger.Module
import dagger.Provides
import org.freedomtool.utils.AppLocaleManager
import javax.inject.Singleton

@Module
class LocaleManagerModule(
    private val localeManager: AppLocaleManager
) {
    @Provides
    @Singleton
    fun localeManager() = localeManager
}