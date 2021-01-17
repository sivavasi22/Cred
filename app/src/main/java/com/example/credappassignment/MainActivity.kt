package com.example.credappassignment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {

    private var progr = 0

    private var expandableLayout0: ExpandableLayout? = null
    private var expandableLayout1: ExpandableLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewPager: ViewPager = findViewById(R.id.view_pager) as ViewPager
        viewPager.setAdapter(simpleAdapter(supportFragmentManager))

        val tabLayout: TabLayout = findViewById(R.id.tab_layout) as TabLayout
        tabLayout.setupWithViewPager(viewPager)




    }



    private class simpleAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
        override fun getCount(): Int {
            return 1
        }

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return AccordionFragment()

            }
            throw IllegalStateException("There's no fragment for position $position")
        }

        override fun getPageTitle(position: Int): CharSequence {
            return "HI"
        }
    }



}
