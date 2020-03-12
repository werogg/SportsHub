package edu.ub.sportshub.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import edu.ub.sportshub.R
import edu.ub.sportshub.helpers.AuthDatabaseHelper

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun logout(view : View) {
        val authDatabaseHelper = AuthDatabaseHelper()
        authDatabaseHelper.signOut(this)
    }
}
