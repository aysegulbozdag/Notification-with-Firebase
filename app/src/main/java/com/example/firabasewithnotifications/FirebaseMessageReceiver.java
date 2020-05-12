package com.example.firabasewithnotifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageReceiver extends FirebaseMessagingService {

    //FirebaseMessagingService sınıfı ile gelen mesajı parametreleriyle birlikte alıyoruz.
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        //Gelen içerik json formatında ise çalışır.
        if (remoteMessage.getData().size() > 0) {
            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
            Log.d("tag", String.valueOf(remoteMessage.getData().size()));
        }

        //Konsoldan mesaj gönderiliyorsa çalışır.
        if (remoteMessage.getNotification() != null) {
            //mesaj içeriği alınıp bildirim gösteren metod çağrılıyor.
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            Log.d("tag1", remoteMessage.getNotification().getTitle() + " " + remoteMessage.getNotification().getBody());
        }
    }

    //RemoteViews ile tasarım halledildi.
    private RemoteViews getCustomDesign(String title, String message) {
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.idTitle, title);
        remoteViews.setTextViewText(R.id.idMessage, message);
        remoteViews.setImageViewResource(R.id.idIcon, R.drawable.call_emoji);
        return remoteViews;
    }

    public void showNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);//Bildirime basıldığında hangi aktiviteye gidileceğini gösterir.
        String channel_id = "web_app_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Pendingintent yabancı uygulamaların sizin uygulamanızı kullanırken izin alarak kullanmasını sağlar.
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.call_emoji)
                .setSound(uri)// Bildirime ses vermek için kullanılır.
                .setAutoCancel(true)//kullanıcı bildirime girdiğinde otomatik olarak silinsin. False denirse bildirim kalıcı olur.
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})//Bildirime ses vermek için kullanılır.
                .setOnlyAlertOnce(true)// Yalnızca bir kere uyar.
                .setContentIntent(pendingIntent);
        //Versiyon kontrolü yapılıyor.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = builder.setContent(getCustomDesign(title, message));
        } else {
            builder = builder.setContentTitle(title)//Bildirim başlığı
                    .setContentText(message)//Bildirim mesajı
                    .setSmallIcon(R.drawable.call_emoji);//Bildirim simgesi
        }

        //NotificationManager ile bildirimi yayınlıyoruz.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "web_app", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);

        }
        notificationManager.notify(0, builder.build());// Notify= bildirmek
    }

}
