package edu.ub.sportshub.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.home.HomeActivity
import edu.ub.sportshub.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_create_event.*
import java.util.*

class EditEventActivity : AppCompatActivity() {

    private var popupWindow : PopupWindow? = null
    private val calendar = Calendar.getInstance()
    private var year = calendar.get(Calendar.YEAR)
    private var month = calendar.get(Calendar.MONTH)
    private var day = calendar.get(Calendar.DAY_OF_MONTH)
    private var hour = calendar.get(Calendar.HOUR_OF_DAY)
    private var minute = calendar.get(Calendar.MINUTE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)
        setupActivityFunctionalities()
        //Cargar cuando se crea el layout los datos del evento
    }

    private fun setupActivityFunctionalities() {
        setupListeners()
    }

    /**
     * Setup all listeners in the activity
     */
    private fun setupListeners() {
        val textProfile = findViewById<TextView>(R.id.toolbar_secondary_txt_my_profile)

        textProfile.setOnClickListener(){
            profileClicked()
        }

        val imageProfile = findViewById<CircleImageView>(R.id.toolbar_secondary_image_my_profile)

        imageProfile.setOnClickListener {
            profileClicked()
        }

        val homeText = findViewById<TextView>(R.id.toolbar_secondary_home)

        homeText.setOnClickListener {
            homeTextClicked()
        }


        val notificationsButton = findViewById<ImageView>(R.id.toolbar_secondary_notifications)

        notificationsButton.setOnClickListener {
            notificationsButtonClicked()
        }

        val buttonDay = findViewById<Button>(R.id.button_day)

        buttonDay.setOnClickListener {
            onButtonDay()
        }

        val buttonHour = findViewById<Button>(R.id.button_hour)

        buttonHour.setOnClickListener {
            onButtonHour()
        }

        val buttonImage = findViewById<Button>(R.id.button_image)

        buttonImage.setOnClickListener {
            onButtonImageClicked()
        }

        val editEventButton = findViewById<Button>(R.id.edit_event_activity_edit_event_button)

        editEventButton.setOnClickListener {
            onEditEventButtonClicked()
        }

    }

    private fun notificationsButtonClicked() {
        val displayMetrics = applicationContext.resources.displayMetrics
        val dpValue1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350f, displayMetrics)
        val dpValue2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 480f, displayMetrics)
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.fragment_notifications_secondary, null)
        val coord = findViewById<ConstraintLayout>(R.id.edit_event_constraint_layout)
        popupWindow = PopupWindow(customView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT, true)
        popupWindow!!.width = dpValue1.toInt()
        popupWindow!!.height = dpValue2.toInt()
        popupWindow!!.showAtLocation(coord, Gravity.TOP,0,220)
    }

    private fun homeTextClicked() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun profileClicked() {
        val popupIntent = Intent(this, ProfileActivity::class.java)
        startActivity(popupIntent)
    }

    private fun onButtonDay() {
        
        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener {
                _, year, month, day ->
            this.year = year
            this.month = month
            this.day = day
            viewTextCreate.text = "$day/$month/$year"
        }, year, month, day)

        dpd.show()
    }

    private fun onButtonHour() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            this.hour = hour
            this.minute = minute
            viewTextCreate.text = "$hour:$minute"
        }
        TimePickerDialog(this, timeSetListener, hour, minute, true).show()
    }

    private fun onButtonImageClicked() {
        TODO("Not yet implemented")
    }

    private fun onEditEventButtonClicked() {

        val intent = Intent(this, EventActivity::class.java)
        startActivity(intent)
    }
}
