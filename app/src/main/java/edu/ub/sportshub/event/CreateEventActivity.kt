package edu.ub.sportshub.event

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Address
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.data.data.DataAccessObjectFactory
import edu.ub.sportshub.data.enums.CreateEventResult
import edu.ub.sportshub.data.events.database.DataEvent
import edu.ub.sportshub.data.events.database.EventCreatedEvent
import edu.ub.sportshub.data.listeners.DataChangeListener
import edu.ub.sportshub.data.models.event.EventDao
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.home.HomeActivity
import edu.ub.sportshub.profile.ProfileActivity
import edu.ub.sportshub.utils.StringUtils
import kotlinx.android.synthetic.main.activity_create_event.*
import java.io.IOException
import java.util.*

class CreateEventActivity : AppCompatActivity(), DataChangeListener {

    private var popupWindow : PopupWindow? = null
    private val calendar = Calendar.getInstance()
    private var year = calendar.get(Calendar.YEAR)
    private var month = calendar.get(Calendar.MONTH)
    private var day = calendar.get(Calendar.DAY_OF_MONTH)
    private var hour = calendar.get(Calendar.HOUR_OF_DAY)
    private var minute = calendar.get(Calendar.MINUTE)
    private var suggestionsAddresses = mutableListOf<Address>()
    private var autoCompleteAdapter : ArrayAdapterNoFilter? = null
    private val PICK_IMAGE_REQUEST = 22
    private var filePath : Uri? = null
    private var firebaseStorage = FirebaseStorage.getInstance()
    private var storageReference = firebaseStorage.reference
    private var dateSelected = false
    private var hourSelected = false
    private var imageUploaded = false
    private var databaseHelper = StoreDatabaseHelper()
    private var authDatabaseHelper = AuthDatabaseHelper()
    private var imageSelected : String? = null
    private var progressBar : ProgressBar? = null
    private var addressHandler : Handler? = null
    private lateinit var eventDao : EventDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        setupActivityFunctionalities()

        eventDao = DataAccessObjectFactory.getEventDao()
        eventDao.registerListener(this)

    }

    private fun setupActivityFunctionalities() {
        setupListeners()
        setupAddressAutocomplete()
    }

    /**
     * Setup all listeners in the activity
     */
    private fun setupListeners() {
        val textProfile = findViewById<TextView>(R.id.toolbar_txt_my_profile)

        textProfile.setOnClickListener {
            profileClicked()
        }

        val imageProfile = findViewById<CircleImageView>(R.id.toolbar_image_my_profile)

        imageProfile.setOnClickListener {
            profileClicked()
        }

        val homeText = findViewById<TextView>(R.id.toolbar_home)

        homeText.setOnClickListener {
            homeTextClicked()
        }



        val notificationsButton = findViewById<ImageView>(R.id.toolbar_notifications)

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

        val createEventButton = findViewById<Button>(R.id.button_add_event)
        createEventButton.isEnabled = true
        createEventButton.isClickable = true
        createEventButton.setOnClickListener {
            onCreateEventButtonClicked()
        }
    }

    private fun setupAddressAutocomplete() {
        setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL)

        autoCompleteAdapter = ArrayAdapterNoFilter(this, android.R.layout.simple_dropdown_item_1line)
        autoCompleteAdapter!!.setNotifyOnChange(false)

        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.where_text)

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (addressHandler == null) addressHandler = Handler()

                val runnable = Runnable {
                    val value = p0.toString()

                    if (value.isNotEmpty()) {
                        Thread(Runnable {
                            runOnUiThread {
                                notifyResult(value)
                            }
                        }).start()

                    } else {
                        autoCompleteAdapter?.clear()
                    }
                }

                addressHandler!!.removeCallbacks(runnable)
                addressHandler!!.postDelayed(runnable, 2000)

            }
        })

        autoCompleteTextView.threshold = 2
        autoCompleteTextView.setAdapter(autoCompleteAdapter)
    }

   private fun notifyResult(value : String) {
        if (value.isNotEmpty()) {
            autoCompleteAdapter?.clear()

            for (string in StringUtils.getAddressArrayFromName(this, value)) {
                autoCompleteAdapter?.add(string)
            }

            for (address in suggestionsAddresses) {
                autoCompleteAdapter?.add("${address.featureName} ${address.countryName} ${address.postalCode}")
            }
          
            autoCompleteAdapter!!.notifyDataSetChanged()
        }
    }

    private fun homeTextClicked() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }


    private fun notificationsButtonClicked() {
        val displayMetrics = applicationContext.resources.displayMetrics
        val dpValue1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350f, displayMetrics)
        val dpValue2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 480f, displayMetrics)
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.fragment_notifications_secondary, null)
        val coord = findViewById<ConstraintLayout>(R.id.create_event_constraint_layout)
        popupWindow = PopupWindow(customView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT, true)
        popupWindow!!.width = dpValue1.toInt()
        popupWindow!!.height = dpValue2.toInt()
        popupWindow!!.showAtLocation(coord, Gravity.TOP,0,300)
    }

    private fun profileClicked() {
        val popupIntent = Intent(this, ProfileActivity::class.java)
        startActivity(popupIntent)
    }

    private fun onButtonDay() {

        val dpd = DatePickerDialog(this,DatePickerDialog.OnDateSetListener {
                _, year, month, day ->
            this.year = year - 1900
            this.month = month
            this.day = day
            dateSelected = true
            viewTextCreate.text = "$day/$month/$year"
        }, year, month, day)

        dpd.show()
    }

    private fun onButtonHour() {

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            this.hour = hour
            this.minute = minute
            hourSelected = true
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
        val rootLayout = findViewById<ConstraintLayout>(R.id.create_event_constraint_layout)
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
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

    private fun onCreateEventButtonClicked() {
        val button = findViewById<Button>(R.id.button_add_event)
        val titleEvent = findViewById<EditText>(R.id.title_text)
        val whereEvent = findViewById<AutoCompleteTextView>(R.id.where_text)  //Geocoder
        val descEvent = findViewById<EditText>(R.id.description_text)
        if (!button.isEnabled and !button.isClickable){
            Toast.makeText(this,getString(R.string.event_button_save), Toast.LENGTH_LONG).show();
        } else {
            if (dateSelected and hourSelected and imageUploaded and titleEvent.text.toString().isNotEmpty()
                and whereEvent.text.toString().isNotEmpty() and descEvent.text.toString().isNotEmpty()
            ) {

                val location = StringUtils.getLocationFromName(this, whereEvent.text.toString())
                val currentUserUid = authDatabaseHelper.getCurrentUser()?.uid!!

                if (location == null || imageSelected == null) return

                eventDao.createEvent(
                    currentUserUid,
                    titleEvent.text.toString(),
                    Timestamp(Date(year, month, day, hour, minute)),
                    Timestamp.now(),
                    GeoPoint(location.latitude, location.longitude),
                    descEvent.text.toString(),
                    imageSelected!!
                )
                button.isEnabled = false;
                button.isClickable = false;
            }
        }

    }

    private fun goToEventActivity(eventId: String) {
        val intent = Intent(this, EventActivity::class.java)
        intent.putExtra("eventId", eventId)
        startActivity(intent)
    }

    override fun onDataLoaded(event: DataEvent) {
        if (event is EventCreatedEvent) {
            when (event.result) {
                CreateEventResult.SUCCESS -> {
                    Toast.makeText(applicationContext, getString(R.string.event_created), Toast.LENGTH_SHORT).show()
                    if (event.getEventId() != null) {
                        goToEventActivity(event.getEventId()!!)
                    }
                }

                CreateEventResult.EXCEPTION -> {
                    Toast.makeText(applicationContext, getString(R.string.event_creation_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
