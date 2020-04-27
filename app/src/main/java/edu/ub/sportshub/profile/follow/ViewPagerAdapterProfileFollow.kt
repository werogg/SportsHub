package edu.ub.sportshub.profile.follow

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import edu.ub.sportshub.profile.follow.MyFollowees
import edu.ub.sportshub.profile.follow.MyFollowers

class ViewPagerAdapterProfileFollow(fm: FragmentManager,s: String) : FragmentPagerAdapter(fm){
    private val ind = s
    override fun getItem(position: Int): Fragment {
        return when (position){
            0-> {
                MyFollowers(ind)
            }
            1-> {
                MyFollowees(ind)
            }
            else -> MyFollowers(ind)
        }
    }

    override fun getCount(): Int {
        return 2
    }
}