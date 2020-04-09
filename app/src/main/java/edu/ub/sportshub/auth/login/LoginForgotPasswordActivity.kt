package edu.ub.sportshub.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.ub.sportshub.R
import edu.ub.sportshub.helpers.AuthDatabaseHelper

class LoginForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_forgot_password)

        val buttonBack = findViewById<Button>(R.id.btn_back)
        val buttonSendEmail = findViewById<Button>(R.id.btn_send_email)
        val emailTextBox = findViewById<EditText>(R.id.txt_email_signup)

        buttonBack.setOnClickListener {
            onButtonBack()
        }

        buttonSendEmail.setOnClickListener {
            onButtonSendRecoveryEmail(emailTextBox.text.toString())
        }

    }

    private fun onButtonBack() {
        val intent = Intent(this, LoginActivity::class.java);
        startActivity(intent)
    }

    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun onButtonSendRecoveryEmail(email : String) {
        if (email.isEmpty()) return
        val authDatabaseHelper = AuthDatabaseHelper()
        authDatabaseHelper.sendPasswordResetEmail(email)
        Toast.makeText(applicationContext, getString(R.string.password_recovery_email_sent) + email, Toast.LENGTH_LONG).show()
    }



}
