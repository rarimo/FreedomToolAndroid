package org.freedomtool.feature.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import org.freedomtool.R
import org.freedomtool.base.view.BaseFragment
import org.freedomtool.databinding.FragmentSecondSlideBinding

class SlideSecondFragment: BaseFragment() {

    private lateinit var binding: FragmentSecondSlideBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View{
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_second_slide, container, false
        )

        return binding.root
    }

    companion object {
        fun newInstance(): SlideSecondFragment {
            return SlideSecondFragment()
        }
    }

}