package edu.ub.sportshub.auth.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import edu.ub.sportshub.R
import edu.ub.sportshub.auth.login.LoginActivity
import edu.ub.sportshub.data.auth.AuthHandlerFactory
import edu.ub.sportshub.data.auth.LoginHandler
import edu.ub.sportshub.data.auth.RegisterHandler
import edu.ub.sportshub.data.enums.RegisterResult
import edu.ub.sportshub.data.events.auth.AuthEvent
import edu.ub.sportshub.data.events.auth.RegisterPerformedEvent
import edu.ub.sportshub.data.listeners.AuthPerformedListener
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.User
import java.util.*

class SignupActivity : AppCompatActivity(), AuthPerformedListener {

    lateinit var registerHandler: RegisterHandler
    lateinit var checkBox : CheckBox
    lateinit var username : EditText
    lateinit var fullname : EditText
    lateinit var email : EditText
    lateinit var pass : EditText
    lateinit var repeat_pass : EditText
    lateinit var buttonSignup : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerHandler = AuthHandlerFactory.getRegisterHandler()
        registerHandler.registerListener(this)

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


    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    /**
     * Signup logic
     * First, check if user +18 and username is not registered
     */
    private fun onSignupButton(){
        checkBox = findViewById<CheckBox>(R.id.checkBox)
        username = findViewById<EditText>(R.id.txt_username_signup)
        fullname = findViewById<EditText>(R.id.txt_fullname_signup)
        email = findViewById<EditText>(R.id.txt_email_signup)
        pass = findViewById<EditText>(R.id.txt_pass_signup)
        repeat_pass = findViewById<EditText>(R.id.txt_rep_pass)
        buttonSignup = findViewById<Button>(R.id.btn_signup)
        buttonSignup.isEnabled = false

        if (pass.text.toString() != repeat_pass.text.toString()) {
            repeat_pass.requestFocus()
            repeat_pass.error = getString(R.string.error_age_not_permitted)
        } else if (!checkBox.isChecked) {
            checkBox.requestFocus()
            checkBox.error = getString(R.string.error_age_not_permitted)

        } else {
            registerHandler.performRegister(username.text.toString(), fullname.text.toString(), email.text.toString(), pass.text.toString(), getString(R.string.default_profile_picture))
        }
        buttonSignup.isEnabled = true
    }

    override fun onActionPerformed(event: AuthEvent) {
        if (event is RegisterPerformedEvent) {
            when (event.registerResult) {
                RegisterResult.WEAK_PASSWORD -> {
                    pass.error = getString(R.string.error_weak_password)
                    username.requestFocus()
                }

                RegisterResult.MAIL_ALREADY_EXISTS -> {
                    email.error = getString(R.string.error_email_already_exists)
                    username.requestFocus()
                }

                RegisterResult.USERNAME_ALREADY_EXISTS -> {
                    username.error = getString(R.string.error_username_already_in_use)
                    username.requestFocus()
                }

                RegisterResult.SUCCESS -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
            buttonSignup.isEnabled = true
        }
    }

}

