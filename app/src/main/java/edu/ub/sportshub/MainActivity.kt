package edu.ub.sportshub

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.ub.sportshub.auth.login.LoginActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.home.HomeActivity
import edu.ub.sportshub.notifications.NotificationService

class MainActivity : AppCompatActivity() {

    private val authDatabaseHelper = AuthDatabaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // If user is not logged re-define intent to login
        if (!authDatabaseHelper.isUserLogged()) {
            intent = Intent(this, LoginActivity::class.java)
        } else if (!authDatabaseHelper.getCurrentUser()?.isEmailVerified!!) {
            authDatabaseHelper.signOut()
            intent = Intent(this, LoginActivity::class.java)
        } else {
            // Define intent to RegisterActivity
            var intent = Intent(this, HomeActivity::class.java)

            Intent(this, NotificationService::class.java).also { intent ->
                startService(intent)
            }

            startActivity(intent)
        }
    }
}
