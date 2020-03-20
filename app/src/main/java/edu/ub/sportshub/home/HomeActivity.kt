package edu.ub.sportshub.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import edu.ub.sportshub.R
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.profile.ProfileActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val textprofile = findViewById<TextView>(R.id.txt_name)
        //entrar al profile.
        textprofile.setOnClickListener(){
            val popupIntent = Intent(this, ProfileActivity::class.java)
            startActivity(popupIntent)
        }
    }

    fun logout(view : View) {
        val authDatabaseHelper = AuthDatabaseHelper()
        authDatabaseHelper.signOut(this)
    }
}
