package org.freedomtool.feature.intro

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_SETTLING
import org.freedomtool.R
import org.freedomtool.base.view.BaseActivity
import org.freedomtool.databinding.ActivityIntroBinding
import org.freedomtool.feature.intro.logic.ViewPagerAdapter
import org.freedomtool.utils.Navigator
import pt.tornelas.segmentedprogressbar.SegmentedProgressBarListener

class IntroActivity : BaseActivity() {

    private lateinit var binding : ActivityIntroBinding
    private lateinit var adapter: ViewPagerAdapter

    private var progressPosition = 0
    override fun onCreateAllowed(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)
        binding.lifecycleOwner = this

        val fragments: List<Fragment> = listOf(
            SlideSecondFragment.newInstance(),
            SlideThirdFragment.newInstance(),
            SlideFourthFragment.newInstance()
        )
        binding.segmentedProgressBar.updateSegmentedSelectedColor(R.color.black)
        setProgressBarPosition(2)
        adapter = ViewPagerAdapter(this, fragments)
        binding.introPager.adapter = adapter

        onSwipeListener()
        initProgressBarEvents()
    }

    override fun onResume() {
        super.onResume()
        setProgressBarPosition(0)
        progressPosition= 0
    }

    override fun onPause() {
        binding.segmentedProgressBar.pause()
        super.onPause()
    }

    private fun initProgressBarEvents() {
        binding.segmentedProgressBar.listener = object: SegmentedProgressBarListener {
            override fun onFinished() {
                navigateToNext()
            }

            override fun onPage(oldPageIndex: Int, newPageIndex: Int) {
                binding.introPager.currentItem = newPageIndex
                progressPosition = newPageIndex
            }
        }
    }

    private var settled = false

    private fun onSwipeListener(){
        binding.introPager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setProgressBarPosition(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

                if (state == SCROLL_STATE_DRAGGING) {
                    settled = false;
                }
                if (state == SCROLL_STATE_SETTLING) {
                    settled = true;
                }
                if (state == SCROLL_STATE_IDLE && !settled && progressPosition > 1) {
                    navigateToNext()
                }
            }
        })
    }

    private fun navigateToNext() {
        Navigator.from(this).openInfo()
    }


    private fun setProgressBarPosition(position: Int) {
        binding.segmentedProgressBar.reset()
        binding.segmentedProgressBar.setPosition(position)
        binding.segmentedProgressBar.setProgress(0)
        progressPosition = position
    }

}