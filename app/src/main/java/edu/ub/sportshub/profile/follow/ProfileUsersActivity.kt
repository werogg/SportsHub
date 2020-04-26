package edu.ub.sportshub.profile.follow

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.ViewPager
import edu.ub.sportshub.R
import edu.ub.sportshub.profile.follow.ViewPagerAdapterProfileFollow
import kotlinx.android.synthetic.main.activity_profile_follow.*


class ProfileUsersActivity : AppCompatActivity() {
    private var mDots = arrayListOf<TextView>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_follow)
        val id = intent.getIntExtra("select",0)
        val fragmentAdapter2 =
            ViewPagerAdapterProfileFollow(
                supportFragmentManager
            )
        pager_follow.adapter = fragmentAdapter2
        pager_follow.currentItem = id
        addDots(id)
        pager_follow?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                addDots(position)
            }
        })

    }


    private fun addDots(position : Int){
        dots_follow.removeAllViews()
        mDots.clear()
        for (i in 0..1){
            val text = TextView(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                text.text = Html.fromHtml("&#8226", HtmlCompat.FROM_HTML_MODE_LEGACY)
            } else {
                text.text = Html.fromHtml("&#8226")
            }
            text.textSize = 35F
            if (i == position){
                text.setTextColor(resources.getColor(R.color.colorPrimaryDark))
            } else {
                text.setTextColor(resources.getColor(R.color.common_google_signin_btn_text_dark_disabled))
            }
            mDots.add(i,text)
            dots_follow.addView(text)
        }
    }
}