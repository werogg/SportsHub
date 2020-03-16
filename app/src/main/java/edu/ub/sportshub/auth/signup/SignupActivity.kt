package edu.ub.sportshub.auth.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import edu.ub.sportshub.R
import edu.ub.sportshub.auth.login.LoginActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper

class SignupActivity : AppCompatActivity() {

    private val authDatabaseHelper = AuthDatabaseHelper()
    private val storeDatabaseHelper = StoreDatabaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val buttonLogin = findViewById<TextView>(R.id.btn_login)
        val buttonSignup = findViewById<Button>(R.id.btn_signup)

        buttonLogin.setOnClickListener(){
            onLoginButton()
        }

        buttonSignup.setOnClickListener(){
            onSignupButton()
        }

    }

    /**
     * Go to login activity logic
     */
    private fun onLoginButton(){
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }


    /**
     * Signup logic
     * First, check if user is 18 of above
     */
    private fun onSignupButton(){
        val checkBox = findViewById<CheckBox>(R.id.checkBox)
        //
        if (checkBox.isChecked){
            // See if the username already exists
            // See if the email already exists
        }
        else{
            Toast.makeText(applicationContext, getString(R.string.error_age_not_permitted), Toast.LENGTH_SHORT).show()
        }
    }

}
