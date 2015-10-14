package net.ottercove.hybernate;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AppNotificationService extends IntentService {
    public static final String DISABLE_NOTIFICATION = "net.ottercove.hybernate.action.disable_notification";
    public static final String LAUNCH_NOTIFICATION = "net.ottercove.hybernate.action.launch_notification";
    public static final String NAME = "net.ottercove.hybernate.extra.NAME";
    public static final String TITLE = "net.ottercove.hybernate.extra.TITLE";
    public static final String SUCCESS = "net.ottercove.hybernate.extra.SUCCESS";
    private static final String SINGLE = "single_message";
    private NotificationManager notificationManager;

    public static void sendDisableNotification(Context context,
                                               String name,
                                               String title,
                                               Boolean success) {
        Intent intent = new Intent(context, AppNotificationService.class);
        intent.setAction(DISABLE_NOTIFICATION);
        intent.putExtra(NAME, name);
        intent.putExtra(TITLE, title);
        intent.putExtra(SUCCESS, success);
        context.startService(intent);

    }

    public static void sendLaunchNotification(Context context,
                                              String name,
                                              String title,
                                              Boolean success) {
        Intent intent = new Intent(context, AppNotificationService.class);
        intent.setAction(LAUNCH_NOTIFICATION);
        intent.putExtra(NAME, name);
        intent.putExtra(TITLE, title);
        intent.putExtra(SUCCESS, success);
        context.startService(intent);
    }

    public AppNotificationService() {
        super("AppNotification");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (intent != null) {
            final String action = intent.getAction();
            if (DISABLE_NOTIFICATION.equals(action)) {
                final String name = intent.getStringExtra(NAME);
                final String title = intent.getStringExtra(TITLE);
                final Boolean success = intent.getBooleanExtra(SUCCESS, true);
                disableNotification(name, title, success);
            } else if (LAUNCH_NOTIFICATION.equals(action)) {
                final String name = intent.getStringExtra(NAME);
                final String title = intent.getStringExtra(TITLE);
                final Boolean success = intent.getBooleanExtra(SUCCESS, true);
                launchNotification(name, title, success);
            }
        }
        this.stopSelf();
    }

    private void disableNotification(String name, String title, Boolean success) {
        if(success) {
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(getString(R.string.app_disabled_title) + title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_disabled_title) + title)
                    .setContentText(getString(R.string.app_disabled_text))
                    .setAutoCancel(true)
                    .build();

            notificationManager.cancel(SINGLE, 0);
            notificationManager.cancel(name, 0);
            notificationManager.notify(SINGLE, 0, notification);
        } else {
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(getString(R.string.app_disabled_failed_title) + title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_disabled_failed_title) + title)
                    .setContentText(getString(R.string.app_disabled_failed_text))
                    .setAutoCancel(true)
                    .build();

            notificationManager.cancel(SINGLE, 0);
            notificationManager.cancel(name, 0);
            notificationManager.notify(SINGLE, 0, notification);
        }
    }

    private void launchNotification(String name, String title, Boolean success) {
        if(success) {
            Intent disable = new Intent(this, LaunchActivity.class);
            disable.setAction(LaunchAppService.DISABLE);
            disable.putExtra(LaunchAppService.NAME, name);
            disable.putExtra(LaunchAppService.TITLE, title);
            int id = (int) System.currentTimeMillis() / 1000;
            PendingIntent pendingIntent = PendingIntent.getActivity(this, id, disable, 0);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setTicker(getString(R.string.app_launched_title) + title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_launched_title) + title)
                    .setContentText(getString(R.string.app_launched_text))
                    .setAutoCancel(true)
                    .build();

            notificationManager.cancel(SINGLE, 0);
            notificationManager.notify(name, 0, notification);
        } else {
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker(getString(R.string.app_launch_failed_title) + title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_launch_failed_title) + title)
                    .setContentText(getString(R.string.app_launch_failed_text))
                    .setAutoCancel(true)
                    .build();

            notificationManager.cancel(SINGLE, 0);
            notificationManager.notify(SINGLE, 0, notification);
        }
    }
}
