package org.freedomtool.feature.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import org.freedomtool.R
import org.freedomtool.base.view.BaseFragment
import org.freedomtool.databinding.FragmentThirdSlideBinding

class SlideThirdFragment: BaseFragment() {

    private lateinit var binding: FragmentThirdSlideBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View{
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_third_slide, container, false
        )

        return binding.root
    }

    companion object {
        fun newInstance(): SlideThirdFragment {
            return SlideThirdFragment()
        }
    }

}