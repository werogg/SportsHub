package edu.ub.sportshub.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.api.Distribution
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import edu.ub.sportshub.R
import edu.ub.sportshub.auth.login.LoginActivity
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.home.HomeActivity
import edu.ub.sportshub.models.User
import kotlinx.android.synthetic.main.activity_edit_event.*
import kotlinx.android.synthetic.main.activity_editprofile.*
import kotlinx.android.synthetic.main.activity_editprofile.img_profile
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException
import java.security.AccessController.getContext
import java.util.*


class EditProfileActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST: Int = 1
    private var popupWindow: PopupWindow? = null
    private val mFirebaseAuth = AuthDatabaseHelper()
    private val mStoreDatabaseHelper = StoreDatabaseHelper()
    private var firebaseStorage = FirebaseStorage.getInstance()
    private var storageReference = firebaseStorage.reference
    private lateinit var filepath: Uri
    private lateinit var newemail: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)
        val uid = mFirebaseAuth.getCurrentUser()!!.uid
        val dpSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            130f,
            this?.resources?.displayMetrics
        ).toInt()
        mStoreDatabaseHelper.retrieveUser(uid)
            .addOnSuccessListener {
                Picasso.with(this)
                    .load(it.toObject(User::class.java)?.getProfilePicture().toString())
                    .resize(dpSize, dpSize)
                    .into(img_profile)
            }
        setupListeners()
    }

    private fun setupListeners() {
        val home = findViewById<TextView>(R.id.toolbar_my_profile_home)
        home.setOnClickListener() {
            buttonHomeClicked()
        }

        val image = findViewById<ImageView>(R.id.img_profile)
        image.setOnClickListener() {
            changeImage()
        }

        val newpaswd = findViewById<Button>(R.id.btn_changepassword)
        newpaswd.setOnClickListener() {
            changePassword()
        }

        val newemail = findViewById<Button>(R.id.btn_changeemail)
        newemail.setOnClickListener() {
            changeEmail()
        }

        val validate = findViewById<Button>(R.id.btn_validate2)
        validate.setOnClickListener() {
            buttonSaveClicked()
        }


        val signout = findViewById<TextView>(R.id.toolbar_signout)
        signout.setOnClickListener() {
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
        val coord = findViewById<ConstraintLayout>(R.id.editprofile_constraint_layout)
        popupWindow = PopupWindow(
            customView,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            true
        )
        popupWindow!!.width = dpValue1.toInt()
        popupWindow!!.height = dpValue2.toInt()
        popupWindow!!.showAtLocation(coord, Gravity.TOP, 0, 300)
    }

    private fun textSignOutClicked() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun buttonSaveClicked() {
        val newname = findViewById<EditText>(R.id.etxt_newname)
        val newdescrip = findViewById<EditText>(R.id.txt_newdescrip)
        if (newname.text.toString() != ("")) {
            val data = hashMapOf("username" to newname.text.toString())
            val uid = mFirebaseAuth.getCurrentUser()!!.uid
            val proces = mStoreDatabaseHelper.getUsersCollection().document(uid)
                .update(data as Map<String, Any>)
            proces.addOnCompleteListener() {
                if (it.isCanceled) {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }
        };
        if (newdescrip.text.toString() != ("")) {
            val data2 = hashMapOf("biography" to newdescrip.text.toString())
            val uid = mFirebaseAuth.getCurrentUser()!!.uid
            val proces2 = mStoreDatabaseHelper.getUsersCollection().document(uid)
                .update(data2 as Map<String, Any>)
            proces2.addOnCompleteListener() {
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

    private fun changeImage() {
        //Stuff in Dialog
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Change new Image")
        alertDialog.setMessage("Change Image")
        val inputnewurl = EditText(this)
        inputnewurl.setHint("Enter your URL here")
        val localimg = Button(this)
        localimg.setText("LOCAL IMAGE")
        //About Layout
        val linearl = LinearLayout(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        lp.setMargins(30, 10, 30, 10);
        //Setting Stuff
        inputnewurl.setLayoutParams(lp)
        localimg.setLayoutParams(lp)
        linearl.orientation = LinearLayout.VERTICAL
        linearl.addView(inputnewurl)
        linearl.addView(localimg)

        alertDialog.setView(linearl)
        localimg.setOnClickListener() {
            val intentlocal =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intentlocal.setType("image/")
            startActivityForResult(intentlocal, 10)
        }

        alertDialog.setPositiveButton(
            "VALIDATE",
            DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                if (inputnewurl.text.toString() != ("")) {
                    val data2 = hashMapOf("profilePicture" to inputnewurl.text.toString())
                    val uid = mFirebaseAuth.getCurrentUser()!!.uid
                    val proces2 = mStoreDatabaseHelper.getUsersCollection().document(uid)
                        .update(data2 as Map<String, Any>)
                    proces2.addOnCompleteListener() {
                        if (it.isSuccessful) {
                            Glide.with(this).load(inputnewurl).into(img_profile)
                        }
                    }
                }
            })
        alertDialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val path: Uri = data!!.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver, path
                )
                img_profile.setImageBitmap(bitmap)
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
                val referenceborrar = storageReference.child("images/users/${user?.photoUrl.toString()}")
                referenceborrar.delete()
            }
            reference.putFile(filepath)
                .addOnSuccessListener {
                    getImageUrl(reference)
                }
                .addOnFailureListener() {
                    Toast.makeText(this, "Image not uploaded", Toast.LENGTH_SHORT)
                }
        }
    }

    private fun getImageUrl(reference: StorageReference) {
        reference.downloadUrl.addOnSuccessListener {
            val imagenselected = it.toString()
            val data = hashMapOf("profilePicture" to imagenselected)
            val uid = mFirebaseAuth.getCurrentUser()!!.uid
            val proces2 = mStoreDatabaseHelper.getUsersCollection().document(uid)
                .update(data as Map<String, Any>)
        }
    }

    private fun changeEmail() {
        //AlertDialog
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Setting new email")
        alertDialog.setMessage("Enter email")
        val inputpassword = EditText(this)
        inputpassword.setHint("Enter your password")
        val input = EditText(this)
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        input.setHint("Enter your new email")
        val input2 = EditText(this)
        input2.setHint("Confirm your new email")
        input2.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        val linearl = LinearLayout(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        lp.setMargins(30, 10, 30, 10);
        input.setLayoutParams(lp)
        input2.setLayoutParams(lp)
        inputpassword.setLayoutParams(lp)
        linearl.orientation = LinearLayout.VERTICAL
        linearl.addView(input)
        linearl.addView(input2)
        linearl.addView(inputpassword)
        alertDialog.setView(linearl)
        //
        alertDialog.setPositiveButton("VALIDATE",
            DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                val email = input.getText().toString().trim()

                val confirmemail = input2.getText().toString()
                if (email.equals(confirmemail) && (!email.equals(""))) {
                    newemail = email
                    password = inputpassword.text.toString()
                    Toast.makeText(this, "Emails matched", Toast.LENGTH_SHORT).show()
                    changeEmailCredentials()
                } else {
                    Toast.makeText(this, "Emails didn't match", Toast.LENGTH_SHORT).show()
                }
            })
        alertDialog.setNegativeButton("CANCEL",
            DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.cancel();
            });
        alertDialog.show()
    }
    private fun changeEmailCredentials() {
        val user = mFirebaseAuth.getCurrentUser()
        val credential = EmailAuthProvider.getCredential(user?.email.toString(), password)
        if (credential != null) {
            user?.reauthenticate(credential)
                ?.addOnSuccessListener {
                    Toast.makeText(this, "Credentials OK", Toast.LENGTH_SHORT).show()
                    changeEmailContiue()
                }
                ?.addOnFailureListener() {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT)
                }
        }
    }
    private fun changeEmailContiue() {
        val user = mFirebaseAuth.getCurrentUser()
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(newemail)
            .addOnSuccessListener {
                val processupdate = user?.updateEmail(newemail)
                Thread.sleep(200)
                processupdate?.addOnCompleteListener {
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
        val inputpassword = EditText(this)
        inputpassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
        inputpassword.setHint("Enter your new password")
        val input2 = EditText(this)
        input2.setHint("Confirm your new password")
        input2.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        val linearl = LinearLayout(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        lp.setMargins(30, 10, 30, 10);
        input2.setLayoutParams(lp)
        inputpassword.setLayoutParams(lp)
        linearl.orientation = LinearLayout.VERTICAL
        linearl.addView(input2)
        linearl.addView(inputpassword)
        alertDialog.setView(linearl)
            .setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
                if (inputpassword.text.toString().equals(input2.text.toString()) && (inputpassword.text.toString() != "")) {
                    password = inputpassword.text.toString().trim()
                    Toast.makeText(this, "Passwords match", Toast.LENGTH_SHORT).show()
                    changePasswordContinue()
                } else {
                    Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("CANCEL", { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            })
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






