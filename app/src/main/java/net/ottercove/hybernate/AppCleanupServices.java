package net.ottercove.hybernate;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AppCleanupServices extends IntentService {
    public static final String ADD = "net.ottercove.hybernate.action.ADD";
    public static final String DEL = "net.ottercove.hybernate.action.DEL";
    public static final String CLEAN = "net.ottercove.hybernate.action.CLWAN";
    public static final String NAME = "net.ottercove.hybernate.extra.NAME";
    private static final String APP_PREFKEY = "net.ottercove.hybernate";
    private static final String PREFKEY = "active_apps";

    public static void addApplication(Context context, String name) {
        Intent intent = new Intent(context, AppCleanupServices.class);
        intent.setAction(ADD);
        intent.putExtra(NAME, name);
        context.startService(intent);
    }

    public static void delApplication(Context context, String name) {
        Intent intent = new Intent(context, AppCleanupServices.class);
        intent.setAction(DEL);
        intent.putExtra(NAME, name);
        context.startService(intent);
    }

    public static void cleanApplications(Context context) {
        Intent intent = new Intent(context, AppCleanupServices.class);
        intent.setAction(CLEAN);
        context.startService(intent);
    }

    public AppCleanupServices() {
        super("AppCleanupServices");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ADD.equals(action)) {
                final String name = intent.getStringExtra(NAME);
                delAppAction(name);
            } else if (DEL.equals(action)) {
                final String name = intent.getStringExtra(NAME);
                addAppAction(name);
            } else if (CLEAN.equals(action)) {
                cleanAppAction();
            }
        }
    }

    private void delAppAction(String name) {
        Set<String> activeApps = getActiveAppsSet();
        activeApps.remove(name);
        commitAppPreferences(activeApps);
    }

    private void addAppAction(String name) {
        Set<String> activeApps = getActiveAppsSet();
        activeApps.add(name);
        commitAppPreferences(activeApps);
    }

    private void cleanAppAction() {
        Set<String> activeApps = getActiveAppsSet();

        if (activeApps.size() == 0) {
            return;
        }

        for (Iterator<String> apps = activeApps.iterator(); apps.hasNext(); ) {
            String appName = apps.next();
            if(appName != "" && !RootStuff.isProcessRunning(appName)) {
                ManagedApp app = new ManagedApp(appName, "");
                if (app.disableApp()) {
                    apps.remove();
                    AppNotificationService.sendDisableNotification(this,
                            app.getAppName(),
                            app.getAppTitle(),
                            true);
                }
            }
        }

        commitAppPreferences(activeApps);
    }

    private Set<String> getActiveAppsSet() {
        SharedPreferences settings = getAppPreferences();
        Set<String> activeApps = settings.getStringSet(PREFKEY, new HashSet<String>());
        return activeApps;
    }

    private SharedPreferences.Editor getSettingsEditor() {
        SharedPreferences settings = getAppPreferences();
        SharedPreferences.Editor editor = settings.edit();
        return editor;
    }

    private SharedPreferences getAppPreferences() {
        SharedPreferences settings = getSharedPreferences("net.ottercove.hybernate", 0);
        return settings;
    }

    private void commitAppPreferences(Set<String> values) {
        SharedPreferences.Editor editor = getSettingsEditor();
        editor.putStringSet(PREFKEY, values);
        editor.commit();
    }
}
