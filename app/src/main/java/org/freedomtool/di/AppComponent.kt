package org.freedomtool.di

import dagger.Component
import org.freedomtool.App
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.base.view.BaseBottomSheetDialog
import org.freedomtool.base.view.BaseFragment
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        UtilsModule::class,
        LocaleManagerModule::class,
        RetrofitModule::class,
        ApiProviderModule::class,
    ]
)

interface AppComponent {
    fun inject (app: App)
    fun inject (baseActivity: BaseActivity)
    fun inject (baseFragment: BaseFragment)
    fun inject (baseBottomSheetDialog: BaseBottomSheetDialog)
}