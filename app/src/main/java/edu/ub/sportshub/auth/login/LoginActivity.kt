package edu.ub.sportshub.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import edu.ub.sportshub.MainActivity
import edu.ub.sportshub.R
import edu.ub.sportshub.auth.signup.SignupActivity
import edu.ub.sportshub.data.auth.AuthHandlerFactory
import edu.ub.sportshub.data.auth.FireauthLoginHandler
import edu.ub.sportshub.data.auth.ILoginHandler
import edu.ub.sportshub.data.auth.LoginHandler
import edu.ub.sportshub.data.enums.LoginResult
import edu.ub.sportshub.data.events.auth.AuthEvent
import edu.ub.sportshub.data.events.auth.LoginPerformedEvent
import edu.ub.sportshub.data.listeners.AuthPerformedListener
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity(), AuthPerformedListener {

    private lateinit var loginButton : Button
    private lateinit var loginHandler: LoginHandler
    private lateinit var authDatabaseHelper : AuthDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authDatabaseHelper = AuthDatabaseHelper()
        loginHandler = AuthHandlerFactory.getLoginHandler()
        loginHandler.registerListener(this)

        val buttonLogin = findViewById<Button>(R.id.btn_login)
        val buttonSignup = findViewById<Button>(R.id.btn_signup)
        val textForgot = findViewById<TextView>(R.id.txt_login)
        loginButton = findViewById<Button>(R.id.btn_login)


        buttonLogin.setOnClickListener(){
            buttonLogin.onEditorAction(EditorInfo.IME_ACTION_DONE)
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
        val popupIntent = Intent(this, LoginForgotPasswordActivity::class.java)
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
        loginButton.isEnabled = false
        val textUser = findViewById<TextView>(R.id.txt_username_signup)
        val textPassword = findViewById<TextView>(R.id.txt_pass_signup)
        val user = textUser.text.toString()
        val password = textPassword.text.toString()

        loginHandler.performLogin(user, password)
    }

    override fun onBackPressed() {
        exitProcess(0)
    }

    override fun onActionPerformed(event: AuthEvent) {
        if (event is LoginPerformedEvent) {
            when (event.loginResult) {
                LoginResult.SUCCESS -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                LoginResult.MISSING_FIELDS -> {
                    Toast.makeText(applicationContext, getString(R.string.error_empty_login_fields), Toast.LENGTH_SHORT).show()
                    loginButton.isEnabled = true
                }

                LoginResult.WRONG_USERNAME -> {
                    Toast.makeText(applicationContext, getString(R.string.error_wrong_username), Toast.LENGTH_SHORT).show()
                    val textUsername = findViewById<TextView>(R.id.txt_username_signup)
                    textUsername.error = getString(R.string.error_wrong_username)
                    textUsername.requestFocus()
                    textUsername.text = ""
                    loginButton.isEnabled = true
                }

                LoginResult.WRONG_PASSWORD -> {
                    Toast.makeText(applicationContext, getString(R.string.error_wrong_password), Toast.LENGTH_LONG).show()
                    val textPassword = findViewById<TextView>(R.id.txt_pass_signup)
                    textPassword.error = getString(R.string.error_wrong_password)
                    textPassword.requestFocus()
                    textPassword.text = ""
                    loginButton.isEnabled = true
                }

                LoginResult.USER_NOT_VERIFIED -> {
                    val snackbar = Snackbar.make(
                        login_layout,
                        R.string.error_user_not_verified,
                        Snackbar.LENGTH_LONG
                    )
                    snackbar.duration = 8000
                    snackbar.setAction("Resend mail", View.OnClickListener {
                        authDatabaseHelper.getCurrentUser()!!.sendEmailVerification()
                        snackbar.dismiss()
                    })
                    snackbar.show()
                    loginButton.isEnabled = true
                }

                LoginResult.USER_BANNED -> {
                    Toast.makeText(applicationContext, getString(R.string.error_user_banned), Toast.LENGTH_SHORT).show()
                    loginButton.isEnabled = true
                }

                LoginResult.TOO_MUCH_REQUESTS -> {
                    loginButton.isEnabled = true
                }

                LoginResult.UNKNOWN_EXCEPTION -> {
                    loginButton.isEnabled = true
                }
            }
        }
    }


}
