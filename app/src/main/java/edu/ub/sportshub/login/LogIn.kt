package edu.ub.sportshub

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.User

class LogIn : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Declarar la nueva clase en el manifest
        btn_login.setOnClickListener(){
            val usuario = txt_user.text.toString();
            val password = txt_pass.text.toString();
        }

        btn_signup.setOnClickListener(){
            //Intent signup = new Intent(this,/*La clase del signup*/);
            //startActivity(singup);
            //Declarar una nueva clase en el manifest. Para la del login y la del singup
        }

        txtforgot.setOnClickListener(){
            val popup = Intent(this, popup::class.java);
            startActivity(popup)
        }

    }



}
