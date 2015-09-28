package net.ottercove.hybernate;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LaunchAppService extends IntentService {
    public static final String NAME = "net.ottercove.hybernate.extra.NAME";
    public static final String TITLE = "net.ottercove.hybernate.extra.TITLE";

    public LaunchAppService() {
        super("LaunchManagedApp");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (intent != null) {
            final String appName = intent.getStringExtra(NAME);
            final String appTitle = intent.getStringExtra(TITLE);

            if (RootStuff.isRooted() && RootStuff.rootGiven()) {
                ManagedApp app = new ManagedApp(appName, appTitle, notificationManager, context);

                if (app.launchManaged()) {
                    SendNotification.SingleNotification("Launched " + appTitle,
                            "Launched " + appTitle,
                            "App will be disable on exit.",
                            notificationManager,
                            context);
                } else {
                    SendNotification.SingleNotification("Failed to launch  " + appTitle,
                            "Failed to launch  " + appTitle,
                            "App was not launched.",
                            notificationManager,
                            context);
                }
            } else {
                Toast toast = Toast.makeText(context, "No root!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
