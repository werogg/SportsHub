package edu.ub.sportshub.profile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import edu.ub.sportshub.R
import edu.ub.sportshub.home.HomeActivity


class EditProfileActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        val home = findViewById<TextView>(R.id.size)
        home.setOnClickListener(){
            val gohome = Intent(this,HomeActivity::class.java)
            startActivity(gohome)
        }

        val validate = findViewById<ImageButton>(R.id.btn_validate)
        validate.setOnClickListener(){
            val goprofile = Intent(this,ProfileActivity::class.java)
            startActivity(goprofile)
        }
    }
}
