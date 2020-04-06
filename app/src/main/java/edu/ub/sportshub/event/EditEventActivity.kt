package edu.ub.sportshub.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.home.HomeActivity
import edu.ub.sportshub.models.Event
import edu.ub.sportshub.profile.ProfileActivity
import edu.ub.sportshub.utils.StringUtils
import kotlinx.android.synthetic.main.activity_create_event.*
import java.io.IOException
import java.util.*

class EditEventActivity : AppCompatActivity() {

    private var popupWindow : PopupWindow? = null
    private val calendar = Calendar.getInstance()
    private var year = calendar.get(Calendar.YEAR)
    private var month = calendar.get(Calendar.MONTH)
    private var day = calendar.get(Calendar.DAY_OF_MONTH)
    private var hour = calendar.get(Calendar.HOUR_OF_DAY)
    private var minute = calendar.get(Calendar.MINUTE)
    private var eventId : String? = null
    private var event : Event? = null
    private var mStoreDatabaseHelper = StoreDatabaseHelper()
    private var firebaseStorage = FirebaseStorage.getInstance()
    private var storageReference = firebaseStorage.reference
    private val PICK_IMAGE_REQUEST = 22
    private var imageSelected : String? = null
    private var progressBar : ProgressBar? = null
    private var addressHandler : Handler? = null
    private var filePath : Uri? = null
    private var imageUploaded = false
    private var databaseHelper = StoreDatabaseHelper()
    private var authDatabaseHelper = AuthDatabaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        eventId = intent.getStringExtra("eventId")

        if (eventId != null) {
            mStoreDatabaseHelper.retrieveEvent(eventId!!).addOnSuccessListener {
                event = it.toObject(Event::class.java)
                updateEventInfo()
                setupActivityFunctionalities()
            }.addOnFailureListener{
                finish()
            }

            //Cargar cuando se crea el layout los datos del evento
        }

    }

    private fun updateEventInfo() {

        val titleEvent = findViewById<EditText>(R.id.title_text)
        val whereEvent = findViewById<EditText>(R.id.where_text)
        val descEvent = findViewById<EditText>(R.id.description_text)
        val viewImage = findViewById<ImageView>(R.id.image_selector)

        val title = event?.getTitle()
        titleEvent.setText(title)

        val desc = event?.getDescription()
        descEvent.setText(desc)

        val place = StringUtils.getAddressFromLocation(this,event?.getPosition()!!.latitude
            , event?.getPosition()!!.longitude) //event?.getPosition()
        whereEvent.setText(place)

        Picasso.with(this)
            .load(event?.getEventImage()!!)
            .resize(viewImage.width, viewImage.height)
            .into(viewImage)

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
        selectImage()
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Image from here..."),
            PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var success = false

        if (requestCode == PICK_IMAGE_REQUEST
            && resultCode == RESULT_OK
            && data != null
            && data.data != null) {

            val imageSelector = findViewById<ImageView>(R.id.image_selector)
            // Get the Uri of data
            filePath = data.data
            try {
                // Setting image on image view using Bitmap
                val bitmap = MediaStore
                    .Images
                    .Media
                    .getBitmap(
                        contentResolver,
                        filePath)
                imageSelector.setImageBitmap(bitmap)
                success = true
            }
            catch (e : IOException) {
                // Log the exception
                e.printStackTrace()
            }
            finally {
                if (success) uploadImage()
            }
        }
    }
    private fun uploadImage() {
        val rootLayout = findViewById<ConstraintLayout>(R.id.edit_event_constraint_layout)
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleLarge)
        var params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        params.addRule(RelativeLayout.CENTER_VERTICAL)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        rootLayout.addView(progressBar, params)

        if (filePath != null){
            progressBar?.visibility = View.VISIBLE
            val key = UUID.randomUUID().toString()

            val reference = storageReference.child(
                "images/events/$key"
            )
            reference.putFile(filePath!!)
                .addOnSuccessListener {
                    getImageUrl(reference)
                }
                .addOnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.event_image_not_uploaded),
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar?.visibility = View.GONE
                }
        }

    }

    private fun getImageUrl(reference: StorageReference) {
        reference.downloadUrl.addOnSuccessListener {
            imageSelected = it.toString()
            imageUploaded = true
            Toast.makeText(this, getString(R.string.event_image_uploaded), Toast.LENGTH_SHORT)
                .show()
            progressBar?.visibility = View.GONE
        }
            .addOnFailureListener {
                progressBar?.visibility = View.GONE
            }
    }

    private fun onEditEventButtonClicked() {

        val titleEvent = findViewById<EditText>(R.id.title_text)
        val whereEvent = findViewById<EditText>(R.id.where_text)  //Geocoder
        val descEvent = findViewById<EditText>(R.id.description_text)

        if (!imageUploaded) imageSelected = event?.getEventImage()

        if (titleEvent.text.toString().isNotEmpty() and whereEvent.text.toString().isNotEmpty()
            and descEvent.text.toString().isNotEmpty()) {

            val location = StringUtils.getLocationFromName(this, whereEvent.text.toString())

            databaseHelper.retrieveEventRef(eventId!!)
                .update(mapOf(
                    "title" to titleEvent.text.toString(),
                    "position" to GeoPoint(location?.latitude!!, location?.longitude!!),
                    "description" to descEvent.text.toString(),
                    "eventImage" to imageSelected!!
                //faltaria el timeStamp
            ))
            val intent = Intent(this, EventActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)

        }

    }
}
