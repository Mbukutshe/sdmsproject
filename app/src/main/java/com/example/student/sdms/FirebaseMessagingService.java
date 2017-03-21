package com.example.student.sdms;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.v4.app.NotificationCompat;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        final Animation textAnim = new AlphaAnimation(0.0f,1.0f);
        textAnim.setDuration(50);
        textAnim.setStartOffset(20);
        textAnim.setRepeatMode(Animation.REVERSE);
        textAnim.setRepeatCount(3);
        SqliteController controller = new SqliteController(this);
        if(remoteMessage.getData().get("type").equalsIgnoreCase("message"))
        {
            int messageId = Integer.parseInt(remoteMessage.getData().get("messageId"));
            String message = remoteMessage.getData().get("message");
            String author = remoteMessage.getData().get("author");
            String date = remoteMessage.getData().get("date");
            String urgent = remoteMessage.getData().get("urgent");
            String subject = remoteMessage.getData().get("subject");
            controller.insertMessag(messageId, message,author,date,urgent,subject);
            if(MainActivity.isAppActive())
            {
                //MainActivity.newMessage();

                List<ItemObject> myDataset = getAllItemList();
                MessageAdapter adapter = new MessageAdapter(getApplicationContext(),myDataset);
                adapter.addItem(myDataset);
                showNotification(message,"Platt Drive App",true);
            }
            else
            {
                showNotification(message,"Platt Drive App",true);
            }

        }
        else
            if(remoteMessage.getData().get("type").equalsIgnoreCase("document"))
            {
                int messageId = Integer.parseInt(remoteMessage.getData().get("messageId"));
                String message = remoteMessage.getData().get("message");
                String subject = remoteMessage.getData().get("subject");
                String link = remoteMessage.getData().get("link");
                String author = remoteMessage.getData().get("author");
                String date = remoteMessage.getData().get("date");
                String filename = remoteMessage.getData().get("filename");
                controller.insertMessage(messageId,message,subject,author,link,date,filename);
                showNotification(message, "Platt Drive App",false);
            }
        try
        {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showNotification(String message,String subject,boolean isMessage)
    {

        if(MainActivity.isAppActive() && isMessage)
        {
            Intent in = new Intent(this,MainActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,in,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setAutoCancel(true).setContentTitle(subject).setContentText(message).setSmallIcon(R.drawable.llogo).setContentIntent(pendingIntent);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0,builder.build());
        }
        else
            if(MainActivity.isAppActive() && !isMessage)
            {
                Intent in = new Intent(this,SplashScreen.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,in,PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setAutoCancel(true).setContentTitle(subject).setContentText(message).setSmallIcon(R.drawable.llogo).setContentIntent(pendingIntent);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(0,builder.build());
            }
            else
            {
                Intent in = new Intent(this,downloads.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,in,PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setAutoCancel(true).setContentTitle(subject).setContentText(message).setSmallIcon(R.drawable.llogo).setContentIntent(pendingIntent);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(0,builder.build());
            }
    }
    private List<ItemObject> getAllItemList(){


        List<ItemObject> allItems = new ArrayList<>();
        Cursor data = new SqliteController(this).getAllMessage();
        if(data.moveToFirst())
        {
            do
            {
                allItems.add(new ItemObject(data.getInt(0),data.getString(1),data.getString(4),"",data.getString(2),data.getString(3),"",data.getString(5)));
            }
            while(data.moveToNext());
        }
        return allItems;
    }
}