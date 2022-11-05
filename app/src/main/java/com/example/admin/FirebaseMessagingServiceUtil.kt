package com.example.admin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.resources.Compatibility.Api18Impl.setAutoCancel
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseMessagingServiceUtil: FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("alarm", "onNewToken: ${token}")
    }

    /**
     * 디바이스가 FCM을 통해서 메시지를 받으면 수행된다.
     * @remoteMessage: FCM에서 보낸 데이터 정보들을 저장한다.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // FCM을 통해서 전달 받은 정보에 Notification 정보가 있는 경우 알림을 생성한다.
        if (remoteMessage.notification != null){
            sendNotification(remoteMessage)
        }else{
            Log.d("alarm", "수신 에러: Notification이 비어있습니다.")
        }
    }

    /**
     * FCM에서 보낸 정보를 바탕으로 디바이스에 Notification을 생성한다.
     * @remoteMessage: FCM에서 보낸 메시지
     */

    private fun sendNotification(remoteMessage: RemoteMessage){
        val id = 0
        var title = remoteMessage.notification!!.title
        var body = remoteMessage.notification!!.body


        //var intent = Intent(this, MainActivity::class.java)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //val pendingIntent = PendingIntent.getActivity(this, id, intent, PendingIntent.FLAG_ONE_SHOT)

        val soundUri =
            Uri.parse("android.resource://" + applicationContext.packageName + "/" + R.raw.sos_message)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, "ch1")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)

        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (soundUri != null) {
                // Changing Default mode of notification
                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
                // Creating an Audio Attribute
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()

                // Creating Channel
                val channel =
                    NotificationChannel("ch1", "App Name", NotificationManager.IMPORTANCE_HIGH)
                channel.setSound(soundUri, audioAttributes)
                channel.enableLights(true)
                //channel.enableVibration(true)
                mNotificationManager.createNotificationChannel(channel)
            }
        }
        mNotificationManager.notify(0, notificationBuilder.build())
        /*
        val channelId = "Channel ID"
        val channel = NotificationChannel(channelId, "Notice", NotificationManager.IMPORTANCE_HIGH)
        val soundUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.sos_message)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        channel.setSound(soundUri, audioAttributes)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            //.setSound(soundUri)
            .setPriority(Notification.PRIORITY_HIGH)
            //.setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(id, notificationBuilder.build())


         */
    }
}