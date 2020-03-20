package edu.ub.sportshub.profile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import edu.ub.sportshub.R
import edu.ub.sportshub.home.HomeActivity

class ProfileActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val txteditprofile = findViewById<TextView>(R.id.txt_edit)

        txteditprofile.setOnClickListener(){
            val popupIntent = Intent(this, EditProfileActivity::class.java)
            startActivity(popupIntent)
        }

        val home = findViewById<TextView>(R.id.size)
        home.setOnClickListener(){
            val gohome = Intent(this,HomeActivity::class.java)
            startActivity(gohome)
        }
    }
}
