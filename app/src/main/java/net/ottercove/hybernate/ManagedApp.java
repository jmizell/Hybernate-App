package net.ottercove.hybernate;

import android.app.NotificationManager;
import android.content.Context;

import org.threadly.concurrent.PriorityScheduler;
import org.threadly.concurrent.SchedulerService;
import org.threadly.util.AbstractService;

import java.util.concurrent.TimeUnit;

/**
 * Created by ottah on 9/26/15.
 */
public class ManagedApp {
    private String appName;
    private String appTitle;
    private PriorityScheduler scheduler = new PriorityScheduler(2, false);
    private NotificationManager notificationManager;
    private Context context;

    public ManagedApp(String appName,
                      String appTitle,
                      NotificationManager notificationManager,
                      Context context) {
        this.notificationManager = notificationManager;
        this.context = context;
        this.appName = appName;
        this.appTitle = appTitle;
    }

    public String getAppName() {
        return this.appName;
    }

    public Context getContext() {
        return this.context;
    }

    public NotificationManager getNotificationManager() {
        return this.notificationManager;
    }

    public Boolean enableApp() {
        String command = "pm enable " + this.appName;
        Object[] result = RootStuff.runCommand(command);
        return (Integer) result[0] == 0;
    }

    public Boolean disableApp() {
        String command = "pm disable " + this.appName;
        Object[] result = RootStuff.runCommand(command);
        return (Integer) result[0] == 0;
    }

    public Boolean launchApp() {
        String command = "monkey -p " + this.appName + " -c android.intent.category.LAUNCHER 1";
        Object[] result = RootStuff.runCommand(command);
        return (Integer) result[0] == 0;
    }

    public Boolean launchManaged() {
        if (this.enableApp() && this.launchApp()) {
            LaunchManagedApp app = new LaunchManagedApp(this);
            app.start();
            return true;
        } else {
            return false;
        }
    }

    private class LaunchManagedApp extends AbstractService {
        private checkRunning updateRunner;
        private ManagedApp managedApp;
        private SchedulerService ssi;

        public LaunchManagedApp(ManagedApp managedApp) {
            this.managedApp = managedApp;
            this.updateRunner = new checkRunning(managedApp.getAppName());
            this.ssi = new PriorityScheduler(2, true);
        }

        @Override
        protected void shutdownService() {
            ssi.remove(updateRunner);

            if (managedApp.disableApp()) {
                SendNotification.SingleNotification("Disabled " + appTitle,
                        "Disabled " + appTitle,
                        "App was disabled.",
                        managedApp.getNotificationManager(),
                        managedApp.getContext());
            } else {
                SendNotification.SingleNotification("Disabled " + appTitle,
                        "Disabled " + appTitle,
                        "Failed to disable app.",
                        managedApp.getNotificationManager(),
                        managedApp.getContext());
            }
        }

        @Override
        protected void startupService() {

            ssi.scheduleAtFixedRate(updateRunner, TimeUnit.SECONDS.toMillis(5), TimeUnit.SECONDS.toMillis(1));
        }

        private class checkRunning implements Runnable {
            private String appName;

            public checkRunning(String appName) {
                this.appName = appName;
            }

            @Override
            public void run() {
                if (!RootStuff.isProcessRunning(this.appName)) {
                    stopIfRunning();
                }
            }
        }
    }
}

