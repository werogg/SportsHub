package edu.ub.sportshub.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import edu.ub.sportshub.R
import edu.ub.sportshub.auth.login.LoginActivity
import edu.ub.sportshub.home.HomeActivity


class EditProfileActivity : AppCompatActivity() {

    private var popupWindow : PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        setupListeners()
    }

    private fun setupListeners() {
        val home = findViewById<TextView>(R.id.toolbar_my_profile_home)
        home.setOnClickListener(){
            buttonHomeClicked()
        }

        val validate = findViewById<Button>(R.id.btn_validate2)
        validate.setOnClickListener(){
            buttonSaveClicked()
        }

        val signout = findViewById<TextView>(R.id.toolbar_signout)
        signout.setOnClickListener(){
            textSignOutClicked()
        }

        val notificationsButton = findViewById<ImageView>(R.id.profile_toolbar_primary_notifications)

        notificationsButton.setOnClickListener {
            notificationsButtonClicked()
        }
    }

    private fun notificationsButtonClicked() {
        val displayMetrics = applicationContext.resources.displayMetrics
        val dpValue1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350f, displayMetrics)
        val dpValue2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 480f, displayMetrics)
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.fragment_notifications_primary, null)
        val coord = findViewById<ConstraintLayout>(R.id.editprofile_constraint_layout)
        popupWindow = PopupWindow(customView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT, true)
        popupWindow!!.width = dpValue1.toInt()
        popupWindow!!.height = dpValue2.toInt()
        popupWindow!!.showAtLocation(coord, Gravity.TOP,0,300)
    }

    private fun textSignOutClicked() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun buttonSaveClicked() {
        val goProfile = Intent(this, ProfileActivity::class.java)
        startActivity(goProfile)
    }

    private fun buttonHomeClicked() {
        val goHome = Intent(this, HomeActivity::class.java)
        startActivity(goHome)
    }
}
