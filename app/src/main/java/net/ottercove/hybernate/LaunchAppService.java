package net.ottercove.hybernate;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

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

        if (intent != null && CheckRootActivity.check()) {
            AppCleanupServices.cleanApplications(this);
            final String appName = intent.getStringExtra(NAME);
            final String appTitle = intent.getStringExtra(TITLE);
            final String action = intent.getAction();

            ManagedApp app = new ManagedApp(appName, appTitle);
            app.setContext(context);

            if (LAUNCH.equals(action)) {
                launchApp(app);
            } else if (DISABLE.equals(action)) {
                disableApp(app);
            }
        } else {
            CheckRootActivity.displayRootStatus(context);
        }
    }

    protected void launchApp(ManagedApp app) {
        if (app.launchManaged()) {
            AppNotificationService.sendLaunchNotification(context,
                    app.getAppName(),
                    app.getAppTitle(),
                    true);
            AppCleanupServices.addApplication(this, app.getAppName());
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
            AppCleanupServices.delApplication(this, app.getAppName());
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        } else {
            AppNotificationService.sendDisableNotification(context,
                    app.getAppName(),
                    app.getAppTitle(),
                    false);
        }
        this.stopSelf();
    }
}
