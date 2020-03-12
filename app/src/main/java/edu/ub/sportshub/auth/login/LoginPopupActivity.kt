package edu.ub.sportshub.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import edu.ub.sportshub.MainActivity
import edu.ub.sportshub.R

class LoginPopupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_popup)

        val buttonBack = findViewById<Button>(R.id.btn_back)

        buttonBack.setOnClickListener(){
            val intent = Intent(this, LoginActivity::class.java);
            startActivity(intent)
        }
    }



}
