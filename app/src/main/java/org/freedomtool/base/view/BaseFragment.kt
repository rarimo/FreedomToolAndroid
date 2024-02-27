package org.freedomtool.base.view

import android.content.Context
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import org.freedomtool.App
import org.freedomtool.di.providers.ApiProvider
import org.freedomtool.utils.ClickHelper
import org.freedomtool.utils.ClipboardHelper
import org.freedomtool.view.util.ToastManager
import javax.inject.Inject

abstract class BaseFragment:  Fragment(){

    @Inject
    lateinit var toastManager: ToastManager

    @Inject
    lateinit var clickHelper: ClickHelper

    @Inject
    lateinit var clipboardHelper: ClipboardHelper

    @Inject
    lateinit var apiProvider: ApiProvider

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).appComponent.inject(this)
    }
    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}