package edu.ub.sportshub.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.ub.sportshub.R
import edu.ub.sportshub.auth.signup.SignupActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonLogin = findViewById<Button>(R.id.btn_login)
        val buttonSignup = findViewById<Button>(R.id.btn_signup)
        val textForgot = findViewById<TextView>(R.id.txt_forgot)


        buttonLogin.setOnClickListener(){
            onLoginButton()
        }

        buttonSignup.setOnClickListener(){
            onSignupButton()
        }

        textForgot.setOnClickListener(){
            onForgotPasswordButton()
        }

    }

    /**
     * Start intent to go to forgot password activity
     */
    private fun onForgotPasswordButton() {
        val popupIntent = Intent(this, LoginPopupActivity::class.java);
        startActivity(popupIntent)
    }

    /**
     * Start intent to go to signup activity
     */
    private fun onSignupButton() {
        val signupIntent = Intent(this, SignupActivity::class.java)
        startActivity(signupIntent)
    }

    /**
     * Retrieve user, password and try to login
     */
    private fun onLoginButton() {
        val textUser = findViewById<TextView>(R.id.txt_user)
        val textPassword = findViewById<TextView>(R.id.txt_pass)
        val user = textUser.text.toString();
        val password = textPassword.text.toString();
        executeLogin(user, password)
    }

    fun executeLogin(user : String, password : String) {
        // TODO LOGIN FUNC
    }
}
