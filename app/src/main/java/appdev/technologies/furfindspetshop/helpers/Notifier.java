package appdev.technologies.furfindspetshop.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import appdev.technologies.furfindspetshop.NotificationBubble;
import appdev.technologies.furfindspetshop.R;
import appdev.technologies.furfindspetshop.eventhandlers.OnNotifierSent;

public class Notifier
{
    public static final String CHANNEL_ID = "channel_id";

    public final int NOTIF_ID_DEFAULT = 32;
    public final int NOTIF_ID_SPECIAL = 64;

    public final int NOTIF_DEFAULT = NotificationCompat.PRIORITY_DEFAULT;
    public final int NOTIF_MAX = NotificationCompat.PRIORITY_MAX;

    private final long[] VIBRATE_PATTERN = {0, 100, 500, 100, 500, 100, 2000};

    private Context context;

    private OnNotifierSent onNotifierSent;

    private HashMap<String, Integer> notifIcons = new HashMap<String, Integer>() {{
        put("Woof woof",   R.drawable.icn_notif_dog);
        put("Meow meow",   R.drawable.icn_notif_cat);
        put("Tweet tweet", R.drawable.icn_notif_bird);
    }};

    public Notifier(Context context)
    {
        this.context = context;
    }

    public void notify(String message, int notificationId)
    {
        //createNotificationChannel();
        Map.Entry<String, Integer> iconEntry = getRandomIcon();

        String iconEntryKey = iconEntry.getKey();
        int iconEntryValue = iconEntry.getValue();

        NotificationManager manager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = manager.getNotificationChannel(CHANNEL_ID);

            if (channel == null)
            {
                CharSequence name = "High Importance";
                String description = "channel_description";
                int importance = NotificationManager.IMPORTANCE_HIGH;

                channel = new NotificationChannel(CHANNEL_ID, name, importance);

                channel.setDescription(description);
                channel.enableVibration(true);
                channel.setVibrationPattern(VIBRATE_PATTERN);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }
        Intent notificationIntent = new Intent(this.context, NotificationBubble.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this.context, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getActivity
                    (this.context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, CHANNEL_ID)
                .setSmallIcon(iconEntryValue)
                .setLargeIcon(BitmapFactory.decodeResource(this.context.getResources(), iconEntryValue))
                .setContentTitle(iconEntryKey + "!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(VIBRATE_PATTERN)
                .setAutoCancel(false)
                .setTicker("Notification");

        builder.setFullScreenIntent(pendingIntent, true);

        //Notification notification = buildNotification(message, priority);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.context);

        // Use the notificationId for this notification
        notificationManager.notify(notificationId, builder.build());

        if (onNotifierSent != null)
            onNotifierSent.sent();
    }

    public void setOnNotifierSent(OnNotifierSent listener) {
        onNotifierSent = listener;
    }

    private Map.Entry<String, Integer> getRandomIcon() {
        Random random = new Random();
        Object[] entries = notifIcons.entrySet().toArray();
        return (Map.Entry<String, Integer>) entries[random.nextInt(entries.length)];
    }
}
