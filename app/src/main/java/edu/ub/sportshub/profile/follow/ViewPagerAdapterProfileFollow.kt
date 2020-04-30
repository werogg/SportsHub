package edu.ub.sportshub.profile.follow

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import edu.ub.sportshub.profile.follow.MyFollowees
import edu.ub.sportshub.profile.follow.MyFollowers

class ViewPagerAdapterProfileFollow(fm: FragmentManager,val user_id: String) : FragmentPagerAdapter(fm){
    override fun getItem(position: Int): Fragment {
        return when (position){
            0-> {
                MyFollowers(user_id)
            }
            1-> {
                MyFollowees(user_id)
            }
            else -> MyFollowers(user_id)
        }
    }

    override fun getCount(): Int {
        return 2
    }
}