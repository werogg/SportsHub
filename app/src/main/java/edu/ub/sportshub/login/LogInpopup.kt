package edu.ub.sportshub

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.User

class LogInpopup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popuplogin)

        btn_back.setOnClickListener(){
            val main = Intent(this, MainActivity::class.java);
            startActivity(main)
        }
    }



}
