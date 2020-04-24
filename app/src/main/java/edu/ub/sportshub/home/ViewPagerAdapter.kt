package edu.ub.sportshub.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    override fun getItem(position: Int): Fragment {
        return when (position){
            0-> {
                EventsFragment()
            }
            1 -> {
                UsersFragment()
            }
            2-> {
                MapFragment()
            }
            else -> EventsFragment()

        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position){
            0 -> "Event"
            1 -> "User"
            2 -> "Map"
            else -> "error"
        }
    }
}