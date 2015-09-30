package net.ottercove.hybernate;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LaunchAppService extends IntentService {
    public static final String LAUNCH = "net.ottercove.hybernate.extra.LAUNCH";
    public static final String DISABLE = "net.ottercove.hybernate.extra.DISABLE";
    public static final String NAME = "net.ottercove.hybernate.extra.NAME";
    public static final String TITLE = "net.ottercove.hybernate.extra.TITLE";
    private Context context;
    private NotificationManager notificationManager;

    public LaunchAppService() {
        super("LaunchManagedApp");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = getApplicationContext();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (intent != null) {
            final String appName = intent.getStringExtra(NAME);
            final String appTitle = intent.getStringExtra(TITLE);
            final String action = intent.getAction();

            if (RootStuff.isRooted() && RootStuff.rootGiven()) {
                ManagedApp app = new ManagedApp(appName, appTitle);
                app.setNotificationManager(notificationManager);
                app.setContext(context);

                if (action == LAUNCH) {
                    launchApp(app);
                } else if (action == DISABLE) {
                    disableApp(app);
                }
            } else {
                Toast toast = Toast.makeText(context, "No root!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    protected void launchApp(ManagedApp app) {
        if (app.launchManaged()) {
            Intent disable = new Intent(this, LaunchActivity.class);
            disable.setAction(DISABLE);
            disable.putExtra(LaunchAppService.NAME, app.getAppName());
            disable.putExtra(LaunchAppService.TITLE, app.getAppTitle());

            PendingIntent intent = PendingIntent.getActivity(this, 0, disable, 0);

            Notification notification = new NotificationCompat.Builder(context)
                    .setContentIntent(intent)
                    .setTicker("Launched " + app.getAppTitle())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Launched " + app.getAppTitle())
                    .setContentText("Click here to disable.")
                    .setAutoCancel(true)
                    .build();

            notificationManager.cancelAll();
            notificationManager.notify(0, notification);

        } else {
            SendNotification.SingleNotification("Failed to launch  " + app.getAppTitle(),
                    "Failed to launch  " + app.getAppTitle(),
                    "App was not launched.",
                    notificationManager,
                    context);
        }
    }

    public void disableApp(ManagedApp app) {
        if (app.disableApp()) {
            SendNotification.SingleNotification("Disabled " + app.getAppTitle(),
                    "Disabled " + app.getAppTitle(),
                    "App was diasabled.",
                    notificationManager,
                    context);
        } else {
            SendNotification.SingleNotification("Failed to disable  " + app.getAppTitle(),
                    "Failed to disable  " + app.getAppTitle(),
                    "App was not disabled.",
                    notificationManager,
                    context);
        }
    }
}
