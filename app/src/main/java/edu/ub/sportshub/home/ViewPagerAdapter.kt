package edu.ub.sportshub.home

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import edu.ub.sportshub.R

class ViewPagerAdapter(fm:FragmentManager) : FragmentPagerAdapter(fm){

    override fun getItem(position: Int): Fragment {
        return when (position){
            0-> {
                Events()
            }
            1 -> {
                Users()
            }
            else-> {
                return Map()
            }

        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position){
            0 -> Resources.getSystem().getString(R.string.eventsFragment)
            1 -> Resources.getSystem().getString(R.string.usersFragment)
            else -> {
                return Resources.getSystem().getString(R.string.mapFragment)
            }
        }
    }
}