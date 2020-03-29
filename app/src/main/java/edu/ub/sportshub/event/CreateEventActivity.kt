package edu.ub.sportshub.event

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Address
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.home.HomeActivity
import edu.ub.sportshub.profile.ProfileActivity
import edu.ub.sportshub.utils.StringUtils
import kotlinx.android.synthetic.main.activity_create_event.*
import kotlinx.android.synthetic.main.activity_event.*
import java.io.IOException
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    private var popupWindow : PopupWindow? = null
    private val c = Calendar.getInstance()
    private val year = c.get(Calendar.YEAR)
    private val month = c.get(Calendar.MONTH)
    private val day = c.get(Calendar.DAY_OF_MONTH)
    private val hour = c.get(Calendar.HOUR_OF_DAY)
    private val minute = c.get(Calendar.MINUTE)
    private var suggestionsAddresses = mutableListOf<Address>()
    private var autoCompleteAdapter : ArrayAdapterNoFilter? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private val PICK_IMAGE_REQUEST = 22
    private var filePath : Uri? = null
    private var firebaseStorage = FirebaseStorage.getInstance()
    private var storageReference = firebaseStorage.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        setupActivityFunctionalities()

    }

    private fun setupActivityFunctionalities() {
        setupListeners()
        setupAddressAutocomplete()
    }

   private fun notifyResult(value : String) {
        if (value.isNotEmpty()) {
            autoCompleteAdapter?.clear()

            for (string in StringUtils.getAdressArrayFromName(this, value)) {
                autoCompleteAdapter?.add(string)
            }

            for (address in suggestionsAddresses) {
                autoCompleteAdapter?.add("${address.featureName} ${address.countryName} ${address.postalCode}")
            }
          
            autoCompleteAdapter!!.notifyDataSetChanged()
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
                var value = p0.toString()

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
        })

        autoCompleteTextView.setOnItemClickListener { _, _, i, _ ->
            if (i < suggestionsAddresses.size) {
                val selected = suggestionsAddresses[i]
                latitude = selected.latitude
                longitude = selected.longitude
            }
        }

        autoCompleteTextView.threshold = 2
        autoCompleteTextView.setAdapter(autoCompleteAdapter)
    }

    /**
     * Setup all listeners in the activity
     */
    private fun setupListeners() {
        val textProfile = findViewById<TextView>(R.id.toolbar_secondary_txt_my_profile)

        textProfile.setOnClickListener {
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

        val createEventButton = findViewById<Button>(R.id.button_add_event)

        createEventButton.setOnClickListener {
            onCreateEventButtonClicked()
        }
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

    private fun onCreateEventButtonClicked() {


        val intent = Intent(this, EventActivity::class.java)
        startActivity(intent)
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

        val dpd = DatePickerDialog(this,DatePickerDialog.OnDateSetListener {
                view, year, month, day ->

            viewTextCreate.text = " $day  $month  $year"
        }, year, month, day)

        dpd.show()

        //dateStamp = Timestamp(Date(year,month,day))

    }

    private fun onButtonHour() {

        val c = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->

            viewTextCreate.text = "$hour $minute"
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
                var bitmap = MediaStore
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

        if (filePath != null){
            val key = UUID.randomUUID().toString()

            var reference = storageReference.child(
                "images/events/$key"
            )
            reference.putFile(filePath!!)
                .addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.event_image_uploaded), Toast.LENGTH_SHORT)
                        .show()
                }
        }

    }


    private fun onButtonNewEvent() {
        val titeEvent = findViewById<EditText>(R.id.title_text)
        val whereEvent = findViewById<AutoCompleteTextView>(R.id.where_text)  //Geocoder
        val descEvent = findViewById<EditText>(R.id.description_text)

        var dateStamp : Timestamp? = null

        // Obtener lat y long de address
        val locLat = StringUtils.getLocationFromName(applicationContext, whereEvent.text.toString())?.latitude
        val locLong = StringUtils.getLocationFromName(applicationContext, whereEvent.text.toString())?.longitude
        val toast = Toast.makeText(applicationContext, "Latitude: $locLat\nLongitude: $locLong", Toast.LENGTH_LONG)
        toast.show()

    }


}
