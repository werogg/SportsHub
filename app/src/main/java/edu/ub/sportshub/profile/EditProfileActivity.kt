package edu.ub.sportshub.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import edu.ub.sportshub.R
import edu.ub.sportshub.auth.login.LoginActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.home.HomeActivity
import edu.ub.sportshub.models.User
import java.io.IOException
import java.util.*


class EditProfileActivity : AppCompatActivity() {
    private var popupWindow: PopupWindow? = null
    private val mFirebaseAuth = AuthDatabaseHelper()
    private val mStoreDatabaseHelper = StoreDatabaseHelper()
    private var firebaseStorage = FirebaseStorage.getInstance()
    private var storageReference = firebaseStorage.reference
    private var filepath: Uri? = null
    private lateinit var newemail: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)
        val imageProfileView = findViewById<ImageView>(R.id.img_profile)
        val uid = mFirebaseAuth.getCurrentUser()!!.uid
        val dpSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            130f,
            resources?.displayMetrics
        ).toInt()
        mStoreDatabaseHelper.retrieveUser(uid)
            .addOnSuccessListener {
                Picasso.with(this)
                    .load(it.toObject(User::class.java)?.getProfilePicture().toString())
                    .resize(dpSize, dpSize)
                    .into(imageProfileView)
            }
        setupListeners()
    }

    private fun setupListeners() {
        val homeView = findViewById<TextView>(R.id.toolbar_my_profile_home)
        homeView.setOnClickListener() {
            buttonHomeClicked()
        }

        val profileImageView = findViewById<ImageView>(R.id.img_profile)
        profileImageView.setOnClickListener() {
            changeImage()
        }

        val newPasswordButton = findViewById<Button>(R.id.btn_changepassword)
        newPasswordButton.setOnClickListener() {
            changePassword()
        }

        val newEmailButton = findViewById<Button>(R.id.btn_changeemail)
        newEmailButton.setOnClickListener() {
            changeEmail()
        }

        val validateButton = findViewById<Button>(R.id.btn_validate2)
        validateButton.setOnClickListener() {
            buttonSaveClicked()
        }


        val signOutButton = findViewById<TextView>(R.id.toolbar_signout)
        signOutButton.setOnClickListener() {
            textSignOutClicked()
        }

        val notificationsButton =
            findViewById<ImageView>(R.id.profile_toolbar_primary_notifications)

        notificationsButton.setOnClickListener {
            notificationsButtonClicked()
        }
    }

    private fun notificationsButtonClicked() {
        val displayMetrics = applicationContext.resources.displayMetrics
        val dpValue1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350f, displayMetrics)
        val dpValue2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 480f, displayMetrics)
        val inflater =
            applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.fragment_notifications_primary, null)
        val coordinates = findViewById<ConstraintLayout>(R.id.editprofile_constraint_layout)
        popupWindow = PopupWindow(
            customView,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            true
        )
        popupWindow!!.width = dpValue1.toInt()
        popupWindow!!.height = dpValue2.toInt()
        popupWindow!!.showAtLocation(coordinates, Gravity.TOP, 0, 300)
    }

    private fun textSignOutClicked() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun buttonSaveClicked() {
        val newName = findViewById<EditText>(R.id.etxt_newname)
        val newDescription = findViewById<EditText>(R.id.txt_newdescrip)
        if (newName.text.toString() != ("")) {
            val data = hashMapOf("username" to newName.text.toString())
            val uid = mFirebaseAuth.getCurrentUser()!!.uid
            mStoreDatabaseHelper.getUsersCollection().document(uid)
                .update(data as Map<String, Any>)
                .addOnCompleteListener() {
                if (it.isCanceled) {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
        if (newDescription.text.toString() != ("")) {
            val data2 = hashMapOf("biography" to newDescription.text.toString())
            val uid = mFirebaseAuth.getCurrentUser()!!.uid
            mStoreDatabaseHelper.getUsersCollection().document(uid)
                .update(data2 as Map<String, Any>)
                .addOnCompleteListener() {
                if (it.isCanceled) {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
        val goProfile = Intent(this, ProfileActivity::class.java)
        startActivity(goProfile)
    }

    private fun buttonHomeClicked() {
        val goHome = Intent(this, HomeActivity::class.java)
        startActivity(goHome)

    }

    @SuppressLint("IntentReset")
    private fun changeImage() {
        //Stuff in Dialog
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Change new Image")
        alertDialog.setMessage("Change Image")
        val inputNewUrl = EditText(this)
        inputNewUrl.hint = "Enter your URL here"
        val localImage = Button(this)
        localImage.text = "LOCAL IMAGE"
        //About Layout
        val linearLayout = LinearLayout(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        lp.setMargins(30, 10, 30, 10);
        //Setting Stuff
        inputNewUrl.layoutParams = lp
        localImage.layoutParams = lp
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(inputNewUrl)
        linearLayout.addView(localImage)

        alertDialog.setView(linearLayout)
        localImage.setOnClickListener() {
            val localIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            localIntent.type = "image/"
            startActivityForResult(localIntent, 10)
        }

        alertDialog.setPositiveButton(
            "VALIDATE"
        ) { _: DialogInterface, _: Int ->
            if (inputNewUrl.text.toString() != ("")) {
                val data2 = hashMapOf("profilePicture" to inputNewUrl.text.toString())
                val uid = mFirebaseAuth.getCurrentUser()!!.uid
                mStoreDatabaseHelper.getUsersCollection().document(uid)
                    .update(data2 as Map<String, Any>)
                    .addOnCompleteListener() {
                        if (it.isSuccessful) {
                            val imageProfileView = findViewById<ImageView>(R.id.img_profile)
                            val dpSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130f, resources?.displayMetrics).toInt()
                            Picasso.with(this)
                                .load(inputNewUrl.toString())
                                .resize(dpSize, dpSize)
                                .into(imageProfileView)
                        }
                    }
            }
        }
        alertDialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val path: Uri? = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver, path
                )
                val imageProfileView = findViewById<ImageView>(R.id.img_profile)
                imageProfileView.setImageBitmap(bitmap)
                filepath = path
                uploadImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        if (filepath != null) {
            val key = UUID.randomUUID().toString()
            val user = mFirebaseAuth.getCurrentUser()
            val reference = storageReference.child("images/users/$key")
            if (user?.photoUrl.toString()!=""){
                val referenceDelete = storageReference.child("images/users/${user?.photoUrl.toString()}")
                referenceDelete.delete()
            }
            reference.putFile(filepath!!)
                .addOnSuccessListener {
                    getImageUrl(reference)
                }
                .addOnFailureListener() {
                    Toast.makeText(this, "Image not uploaded", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun getImageUrl(reference: StorageReference) {
        reference.downloadUrl.addOnSuccessListener {
            val imageSelected = it.toString()
            val data = hashMapOf("profilePicture" to imageSelected)
            val uid = mFirebaseAuth.getCurrentUser()!!.uid
            mStoreDatabaseHelper.getUsersCollection().document(uid)
                .update(data as Map<String, Any>)
        }
    }

    private fun changeEmail() {
        //AlertDialog
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Setting new email")
        alertDialog.setMessage("Enter email")
        val inputPassword = EditText(this)
        inputPassword.hint = "Enter your password"
        val input = EditText(this)
        input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        input.hint = "Enter your new email"
        val input2 = EditText(this)
        input2.hint = "Confirm your new email"
        input2.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        val linearLayout = LinearLayout(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        lp.setMargins(30, 10, 30, 10);
        input.layoutParams = lp
        input2.layoutParams = lp
        inputPassword.layoutParams = lp
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(input)
        linearLayout.addView(input2)
        linearLayout.addView(inputPassword)
        alertDialog.setView(linearLayout)
        //
        alertDialog.setPositiveButton("VALIDATE"
        ) { _: DialogInterface, _: Int ->
            val email = input.text.toString().trim()
            val confirmEmail = input2.text.toString()
            if (email == confirmEmail && (email != "")) {
                newemail = email
                password = inputPassword.text.toString()
                Toast.makeText(this, "Emails matched", Toast.LENGTH_SHORT).show()
                changeEmailCredentials()
            } else {
                Toast.makeText(this, "Emails didn't match", Toast.LENGTH_SHORT).show()
            }
        }
        alertDialog.setNegativeButton("CANCEL"
        ) { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.cancel();
        }
        alertDialog.show()
    }
    private fun changeEmailCredentials() {
        val user = mFirebaseAuth.getCurrentUser()
        val credential = EmailAuthProvider.getCredential(user?.email.toString(), password)
        if (credential != null) {
            user?.reauthenticate(credential)
                ?.addOnSuccessListener {
                    Toast.makeText(this, "Credentials OK", Toast.LENGTH_SHORT).show()
                    changeEmailContinue()
                }
                ?.addOnFailureListener() {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun changeEmailContinue() {
        val user = mFirebaseAuth.getCurrentUser()
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(newemail)
            .addOnSuccessListener {
                user?.updateEmail(newemail)
                    ?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Changes saved, we have send you a new email verification.",
                                Toast.LENGTH_SHORT
                            ).show()
                            user.sendEmailVerification()
                        } else {
                            Toast.makeText(this, it.exception!!.message, Toast.LENGTH_LONG).show()
                        }
                    }
            }
            .addOnFailureListener() {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }


    private fun changePassword() {
        //Layout Password
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Setting new password")
        alertDialog.setMessage("Enter password")
        val inputPassword = EditText(this)
        inputPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        inputPassword.hint = "Enter your new password"
        val input2 = EditText(this)
        input2.hint = "Confirm your new password"
        input2.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        val linearLayout = LinearLayout(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        lp.setMargins(30, 10, 30, 10);
        input2.layoutParams = lp
        inputPassword.layoutParams = lp
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(input2)
        linearLayout.addView(inputPassword)
        alertDialog.setView(linearLayout)
            .setPositiveButton("OK") { _: DialogInterface, _: Int ->
                if (inputPassword.text.toString() == input2.text.toString() && (inputPassword.text.toString() != "")) {
                    password = inputPassword.text.toString().trim()
                    Toast.makeText(this, "Passwords match", Toast.LENGTH_SHORT).show()
                    changePasswordContinue()
                } else {
                    Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("CANCEL") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
        alertDialog.show()
    }

    private fun changePasswordContinue() {
        mFirebaseAuth.getCurrentUser()?.updatePassword(password)?.addOnSuccessListener {
            Toast.makeText(this, "Changes saved.", Toast.LENGTH_SHORT).show()
        }
            ?.addOnFailureListener() {
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
            }
    }
}
