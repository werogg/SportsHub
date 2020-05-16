package edu.ub.sportshub.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.MetadataChanges
import edu.ub.sportshub.MainActivity
import edu.ub.sportshub.R
import edu.ub.sportshub.data.enums.NotificationType
import edu.ub.sportshub.helpers.AuthDatabaseHelper
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.*

class NotificationService : Service() {

    private val TAG = "NotificationService"

    private val mStoreDatabaseHelper = StoreDatabaseHelper()
    private val mAuthDatabaseHelper = AuthDatabaseHelper()

    override fun onCreate() {
        Log.i(TAG, "onCreate: NotificationService is being created...");
        setupListeners()
        Log.i(TAG, "Service listeners were setup")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onCreate: NotificationService is being created...");
        setupListeners()
        return START_STICKY
    }

    private fun setupListeners() {
        val userId = mAuthDatabaseHelper.getCurrentUser()!!.uid
        mStoreDatabaseHelper.getUsersCollection().document(userId).collection("notifications").addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w(TAG, "listen:error", e)
                return@addSnapshotListener
            }

            var notificationId : String? = null

            for (doc in snapshots!!.documentChanges) {
                when (doc.type) {
                    DocumentChange.Type.ADDED -> {
                        notificationId = doc.document.id
                    }
                }
            }

            if (notificationId != null) {
                mStoreDatabaseHelper.retrieveNotification(notificationId).addOnSuccessListener {
                    var notification : Notification? = null

                    when (it.get("notificationType")) {
                        "FOLLOWED" -> {
                            notification = it.toObject(NotificationFollowed::class.java)

                            mStoreDatabaseHelper.retrieveUser(notification!!.getCreatorUid()).addOnSuccessListener { user ->
                                val creatorUser = user.toObject(User::class.java)
                                showNotification(resources.getString(R.string.title_notification_followed, creatorUser!!.getUsername()), (notification as NotificationFollowed).getMessage(applicationContext, creatorUser.getUsername()))
                            }
                        }
                        "ASSIST_TO_CREATOR" -> {
                            notification = it.toObject(NotificationAssist::class.java)

                            mStoreDatabaseHelper.retrieveUser(notification!!.getCreatorUid()).addOnSuccessListener { user ->
                                val creatorUser = user.toObject(User::class.java)
                                showNotification(resources.getString(R.string.title_notification_assist_creator, creatorUser!!.getUsername()), (notification as NotificationAssist).getMessage(applicationContext, creatorUser.getUsername()))
                            }
                        }

                        "ASSIST_TO_FOLLOWERS" -> {
                            notification = it.toObject(NotificationAssist::class.java)

                            mStoreDatabaseHelper.retrieveUser(notification!!.getCreatorUid()).addOnSuccessListener { user ->
                                val creatorUser = user.toObject(User::class.java)
                                showNotification(resources.getString(R.string.title_notification_assist, creatorUser!!.getUsername()), (notification as NotificationAssist).getMessage(applicationContext, creatorUser.getUsername()))
                            }
                        }

                        "LIKED" -> {
                            notification = it.toObject(NotificationLiked::class.java)

                            mStoreDatabaseHelper.retrieveUser(notification!!.getCreatorUid()).addOnSuccessListener { user ->
                                val creatorUser = user.toObject(User::class.java)
                                showNotification(resources.getString(R.string.title_notification_liked, creatorUser!!.getUsername()), (notification).getMessage(applicationContext, creatorUser.getUsername()))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showNotification(title: String?, body: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channelId = "sportshub_notifications";
            var channel = NotificationChannel(
                channelId,
                "Sportshub notifications channel",
                NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelId);
        }

        notificationManager.notify(0, notificationBuilder.build())
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}