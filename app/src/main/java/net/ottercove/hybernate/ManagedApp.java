package net.ottercove.hybernate;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.threadly.concurrent.PriorityScheduler;
import org.threadly.concurrent.SchedulerService;
import org.threadly.util.AbstractService;

import java.util.concurrent.TimeUnit;

public class ManagedApp {
    private String appName;
    private String appTitle;
    private PriorityScheduler scheduler = new PriorityScheduler(2, false);
    private Context context;

    public ManagedApp(String appName,
                      String appTitle) {
        this.context = null;
        this.appName = appName;
        this.appTitle = appTitle;
    }

    public String getAppName() {
        return this.appName;
    }

    public String getAppTitle() {
        return this.appTitle;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
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
        try {
            Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(appName);
            context.startActivity( LaunchIntent );
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    public Boolean launchManaged() {
        if (this.enableApp() && this.launchApp()) {
            LaunchManagedApp app = new LaunchManagedApp();
            app.start();
            return true;
        } else {
            return false;
        }
    }

    private class LaunchManagedApp extends AbstractService {
        private checkRunning updateRunner;
        private SchedulerService ssi;

        public LaunchManagedApp() {
            this.updateRunner = new checkRunning(appName);
            this.ssi = new PriorityScheduler(2, true);
        }

        @Override
        protected void shutdownService() {
            ssi.remove(updateRunner);

            if (disableApp() && context != null) {
                AppNotificationService.sendDisableNotification(context,
                        appName,
                        appTitle,
                        true);

            } else if (context!= null) {
                AppNotificationService.sendDisableNotification(context,
                        appName,
                        appTitle,
                        false);

            } else {
                Log.println(Log.DEBUG, "ManagedApp.class", "Failed to disable " + appTitle);
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

