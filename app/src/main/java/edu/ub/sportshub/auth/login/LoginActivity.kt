package edu.ub.sportshub.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import edu.ub.sportshub.R
import edu.ub.sportshub.auth.signup.SignupActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.home.HomeActivity
import edu.ub.sportshub.models.User

class LoginActivity : AppCompatActivity() {

    private val authDatabaseHelper = AuthDatabaseHelper()
    private val storeDatabaseHelper = StoreDatabaseHelper()

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

    private fun executeLogin(user : String, password : String) {
        if (user == "" || password == "") {
            Toast.makeText(applicationContext, getString(R.string.error_empty_login_fields), Toast.LENGTH_SHORT).show()
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
                    val textUsername = findViewById<TextView>(R.id.txt_user)
                    textUsername.error = getString(R.string.error_wrong_username)
                    textUsername.requestFocus()
                    textUsername.text = ""
                }
            }
    }

    private fun continueLogin(mail : String?, password : String) {
        if (!mail.isNullOrEmpty()) {
            authDatabaseHelper.loginAccount(mail!!, password)
                .addOnSuccessListener {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    val errorCode = (it as FirebaseAuthException).errorCode

                    if (errorCode == "ERROR_WRONG_PASSWORD")  {
                        Toast.makeText(applicationContext, getString(R.string.error_wrong_password), Toast.LENGTH_LONG).show();
                        val textPassword = findViewById<TextView>(R.id.txt_pass)
                        textPassword.error = getString(R.string.error_wrong_password)
                        textPassword.requestFocus()
                        textPassword.text = ""
                    }
                }
        }
    }
}
