package edu.ub.sportshub.profile.events

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapterProfile(fm: FragmentManager, s: String) : FragmentPagerAdapter(fm){
    private val ind = s
    override fun getItem(position: Int): Fragment {
        return when (position){
            0-> {
                MyEvents(ind)
            }
            1-> {
                OtherEvents(ind)
            }
            else -> MyEvents(ind)
        }
    }

    override fun getCount(): Int {
        return 2
    }
}