package edu.ub.sportshub

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.ub.sportshub.auth.login.LoginActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.home.HomeActivity

class MainActivity : AppCompatActivity() {

    private val authDatabaseHelper = AuthDatabaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Define intent to RegisterActivity
        var intent = Intent(this, HomeActivity::class.java)

        // If user is not logged re-define intent to login
        if (!authDatabaseHelper.isUserLogged()) {
            intent = Intent(this, LoginActivity::class.java)
        }

        registerTestButton()
        startActivity(intent)
    }

    private fun registerTestButton() {
        authDatabaseHelper.createAccount(
            "supwer00@gmail.com",
            "password123",
            "werogg",
            "joel otero",
            "23/05/2000",
            ""
        )
    }

    /*
    fun retrieveUserTestButton(view : View) {
        var textView = findViewById<TextView>(R.id.textView)
        var storeDatabaseHelper = StoreDatabaseHelper()
        var user = storeDatabaseHelper.retrieveUser("W06MnGyhDYg7a1PnoZ1NW93o3Pv1")
            .addOnSuccessListener {
                var user = it.toObject(User::class.java)
                textView.text = user!!.getUsername()
            }

    }
    */

}
