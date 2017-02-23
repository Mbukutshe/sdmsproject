package com.example.student.sdms;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.Context.NOTIFICATION_SERVICE;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        SqliteController controller = new SqliteController(this);
        if(remoteMessage.getData().get("type").equalsIgnoreCase("message"))
        {
            String messageId = remoteMessage.getData().get("messageId");
            String message = remoteMessage.getData().get("message");
            String author = remoteMessage.getData().get("author");
            String date = remoteMessage.getData().get("date");
            controller.insertMessag(messageId, message,author,date);
            showNotification(message,"Platt Drive App");
        }
        else
            if(remoteMessage.getData().get("type").equalsIgnoreCase("document"))
            {
                String messageId = remoteMessage.getData().get("messageId");
                String message = remoteMessage.getData().get("message");
                String subject = remoteMessage.getData().get("subject");
                String link = remoteMessage.getData().get("link");
                String author = remoteMessage.getData().get("author");
                String date = remoteMessage.getData().get("date");
                String filename = remoteMessage.getData().get("filename");
                controller.insertMessage(message,subject,author,link,date,filename);
                showNotification(message, subject);
            }
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showNotification(String message,String subject)
    {
        Intent in = new Intent(this,SplashScreen.class);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,in,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setAutoCancel(true).setContentTitle(subject).setContentText(message).setSmallIcon(R.drawable.llogo).setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }
}