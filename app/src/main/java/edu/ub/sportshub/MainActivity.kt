package edu.ub.sportshub

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.User

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun registerTestButton(view : View) {
        var textView = findViewById<TextView>(R.id.textView)
        var authDatabaseHelper = AuthDatabaseHelper()
        authDatabaseHelper.createAccount(
            "supwer00@gmail.com",
            "password123",
            "werogg",
            "joel otero",
            "23/05/2000",
            ""
        ).addOnFailureListener {
            textView.text = it.message
        }
    }

    fun retrieveUserTestButton(view : View) {
        var textView = findViewById<TextView>(R.id.textView)
        var storeDatabaseHelper = StoreDatabaseHelper()
        var user = storeDatabaseHelper.retrieveUser("W06MnGyhDYg7a1PnoZ1NW93o3Pv1")
            .addOnSuccessListener {
                var user = it.toObject(User::class.java)
                textView.text = user!!.getUsername()
            }

    }

}
