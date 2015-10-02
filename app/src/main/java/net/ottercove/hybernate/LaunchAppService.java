package net.ottercove.hybernate;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class LaunchAppService extends IntentService {
    public static final String LAUNCH = "net.ottercove.hybernate.extra.LAUNCH";
    public static final String DISABLE = "net.ottercove.hybernate.extra.DISABLE";
    public static final String NAME = "net.ottercove.hybernate.extra.NAME";
    public static final String TITLE = "net.ottercove.hybernate.extra.TITLE";
    private Context context;

    public LaunchAppService() {
        super("LaunchManagedApp");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = getApplicationContext();
//        AppCleanupServices.cleanApplications(this);

        if (intent != null) {
            final String appName = intent.getStringExtra(NAME);
            final String appTitle = intent.getStringExtra(TITLE);
            final String action = intent.getAction();

            if (RootStuff.isRooted() && RootStuff.rootGiven()) {
                ManagedApp app = new ManagedApp(appName, appTitle);
                app.setContext(context);

                if (LAUNCH.equals(action)) {
                    launchApp(app);
                } else if (DISABLE.equals(action)) {
                    disableApp(app);
                }
            } else {
                Toast toast = Toast.makeText(context, "No root!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        this.stopSelf();
    }

    protected void launchApp(ManagedApp app) {
        if (app.launchManaged()) {
            AppNotificationService.sendLaunchNotification(context,
                    app.getAppName(),
                    app.getAppTitle(),
                    true);
//            AppCleanupServices.addApplication(this, app.getAppName());
        } else {
            AppNotificationService.sendLaunchNotification(context,
                    app.getAppName(),
                    app.getAppTitle(),
                    false);
        }
    }

    public void disableApp(ManagedApp app) {
        if (app.disableApp()) {
            AppNotificationService.sendDisableNotification(context,
                    app.getAppName(),
                    app.getAppTitle(),
                    true);
//            AppCleanupServices.delApplication(this, app.getAppName());
        } else {
            AppNotificationService.sendDisableNotification(context,
                    app.getAppName(),
                    app.getAppTitle(),
                    false);
        }
    }
}
