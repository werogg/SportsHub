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
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.User
import java.util.*

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


    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    /**
     * Signup logic
     * First, check if user +18 and username is not registered
     */
    private fun onSignupButton(){
        val checkBox = findViewById<CheckBox>(R.id.checkBox)
        val username = findViewById<EditText>(R.id.txt_username_signup)
        val fullname = findViewById<EditText>(R.id.txt_fullname_signup)
        val email = findViewById<EditText>(R.id.txt_email_signup)
        val pass = findViewById<EditText>(R.id.txt_pass_signup)
        val buttonSignup = findViewById<Button>(R.id.btn_signup)
        buttonSignup.isEnabled = false

        // Check if user is over aged
        if (checkBox.isChecked){

            // Search if given username is available
            storeDatabaseHelper.getUsersCollection().whereEqualTo("username", username.text.toString()).get()
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        // If no users registered with the given username continue with signup flow
                        if (it.result?.isEmpty!!) {
                            onSignupContinue(username, fullname, email, pass)
                        } else {
                            username.error = getString(R.string.error_username_already_in_use)
                            username.requestFocus()
                        }
                        buttonSignup.isEnabled = true
                    }
                }
        }
        // Underage
        else{
            checkBox.requestFocus()
            checkBox.error = getString(R.string.error_age_not_permitted)
            buttonSignup.isEnabled = true
        }
    }

    /**
     * Check if mail is not registered and password is not weak
     */
    private fun onSignupContinue(
        username: EditText,
        fullname: EditText,
        email: EditText,
        pass: EditText
    ) {
        // Try to create the account
        authDatabaseHelper.createAccount(email.text.toString(), pass.text.toString())
            .addOnCompleteListener {
                // If signup is succes continue with the flow
                if (it.isSuccessful) {
                    signupLastFlow(username, fullname, email)
                }
            }
                // If failed check the reason
            .addOnFailureListener {
                if (it is FirebaseAuthWeakPasswordException) {
                    pass.error = getString(R.string.error_weak_password)
                    username.requestFocus()
                } else if (it is FirebaseAuthUserCollisionException) {
                    email.error = getString(R.string.error_email_already_exists)
                    username.requestFocus()
                }
            }
    }

    /**
     * All is good, register and store the user.
     * Send the verification mail.
     */
    private fun signupLastFlow(
        username: EditText,
        fullname: EditText,
        email: EditText
    ) {

        // Instance the user object and store it
        val newUser = User(
            username.text.toString(),
            fullname.text.toString(),
            "",
            Timestamp.now(),
            email.text.toString(),
            "",
            authDatabaseHelper.getCurrentUser()!!.uid,
            false
        )

        storeDatabaseHelper.storeUser(newUser)
        authDatabaseHelper.getCurrentUser()!!.sendEmailVerification()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}

