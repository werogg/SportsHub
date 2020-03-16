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
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import edu.ub.sportshub.MainActivity
import edu.ub.sportshub.R
import edu.ub.sportshub.auth.signup.SignupActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val authDatabaseHelper = AuthDatabaseHelper()
    private val storeDatabaseHelper = StoreDatabaseHelper()
    private lateinit var loginButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        executeLogin(user, password)
    }

    /**
     * First state of login flow
     * Search email by username
     */
    private fun executeLogin(user : String, password : String) {
        if (user == "" || password == "") {
            Toast.makeText(applicationContext, getString(R.string.error_empty_login_fields), Toast.LENGTH_SHORT).show()
            loginButton.isEnabled = true
            return
        }

        val usersCollectionRef = storeDatabaseHelper.getUsersCollection()

        usersCollectionRef.whereEqualTo("username", user).get()
            .addOnSuccessListener {
                if (it.documents.size == 1) {
                    val mail = it.documents.get(0).toObject(User::class.java)?.getEmail()
                    continueLogin(mail, password)
                } else {
                    Toast.makeText(applicationContext, getString(R.string.error_wrong_username), Toast.LENGTH_SHORT).show()
                    val textUsername = findViewById<TextView>(R.id.txt_username_signup)
                    textUsername.error = getString(R.string.error_wrong_username)
                    textUsername.requestFocus()
                    textUsername.text = ""
                    loginButton.isEnabled = true
                }
            }
    }

    /**
     * Second state of login flow
     * Check if password is correct
     */
    private fun continueLogin(mail : String?, password : String) {
        if (!mail.isNullOrEmpty()) {
            authDatabaseHelper.loginAccount(mail!!, password)
                .addOnSuccessListener {
                    onLoginSuccess()
                }
                .addOnFailureListener {
                    if (it is FirebaseTooManyRequestsException) {
                        Toast.makeText(applicationContext, getString(R.string.error_wrong_password), Toast.LENGTH_LONG).show()
                    }
                    else if (it is FirebaseAuthException) {
                        val errorCode = it.errorCode

                        if (errorCode == "ERROR_WRONG_PASSWORD")  {
                            Toast.makeText(applicationContext, getString(R.string.error_wrong_password), Toast.LENGTH_LONG).show()
                            val textPassword = findViewById<TextView>(R.id.txt_pass_signup)
                            textPassword.error = getString(R.string.error_wrong_password)
                            textPassword.requestFocus()
                            textPassword.text = ""
                        }
                    }
                    loginButton.isEnabled = true
                }
        }
    }

    /**
     * Last state login flow
     * User logged, check for email verification and banned state
     */
    private fun onLoginSuccess() {
        if (authDatabaseHelper.getCurrentUser() == null) return

        val uid = authDatabaseHelper.getCurrentUser()!!.uid
        storeDatabaseHelper.retrieveUser(uid)
            .addOnSuccessListener {
                var user = it.toObject(User::class.java)

                // Need this check to avoid crash for no time to get authed user if spammed
                if (user != null) {
                    if (user.isBanned()) {
                        Toast.makeText(applicationContext, getString(R.string.error_user_banned), Toast.LENGTH_SHORT).show()
                        loginButton.isEnabled = true
                    // Need this check to avoid crash for no time to get authed user if spammed
                    } else if (authDatabaseHelper.isUserLogged()) {
                        if (!(authDatabaseHelper.getCurrentUser()!!.isEmailVerified)) {
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
                        } else {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
    }
}
