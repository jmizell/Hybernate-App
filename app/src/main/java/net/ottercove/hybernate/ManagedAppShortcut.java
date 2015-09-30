package net.ottercove.hybernate;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManagedAppShortcut extends ActionBarActivity {
    ListView listView ;
    PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shortcut);
        pm = this.getPackageManager();

        ArrayList<AppListModel> items = getPackageList();
        AppListAdapter adapter = new AppListAdapter(this, items);
        listView = (ListView) findViewById(R.id.app_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                int itemPosition = position;
                AppListModel itemValue = (AppListModel) listView.getItemAtPosition(position);

                BitmapDrawable bitmapIcon = (BitmapDrawable) itemValue.getIcon();

                Intent targetIntent = new Intent(getApplicationContext(),
                        net.ottercove.hybernate.LaunchActivity.class);
                targetIntent.setAction(LaunchAppService.LAUNCH);
                targetIntent.putExtra(LaunchAppService.NAME, itemValue.getAppPackage());
                targetIntent.putExtra(LaunchAppService.TITLE, itemValue.getTitle());

                Intent shortcutIntent = new Intent();
                shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, targetIntent);
                shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, itemValue.getTitle());
                shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmapIcon.getBitmap());
                setResult(RESULT_OK, shortcutIntent);

                Context context = getApplicationContext();
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                ManagedApp managedapp = new ManagedApp(itemValue.getAppPackage(),
                        itemValue.getTitle());

                if (managedapp.disableApp()) {
                    SendNotification.SingleNotification("Disabled " + itemValue.getTitle(),
                            "Disabled " + itemValue.getTitle(),
                            "App was disabled.",
                            notificationManager,
                            context);
                } else {
                    SendNotification.SingleNotification("Failed to disable " + itemValue.getTitle(),
                            "Failed to disable " + itemValue.getTitle(),
                            "App was not disabled.",
                            notificationManager,
                            context);
                }

                finish();
            }
        });
    }

    private ArrayList<AppListModel> getPackageList() {
        ArrayList<AppListModel> items = new ArrayList<AppListModel>();

            List<PackageInfo> packageList = pm.getInstalledPackages(0);

            for (PackageInfo item: packageList) {
                AppListModel appModel = getPackageModel(item.packageName);
                if(appModel != null) {
                    items.add(appModel);
                }
            }

        Collections.sort(items);
        return items;
    }

    private AppListModel getPackageModel(String packageName) {
        try {
            ApplicationInfo app = pm.getApplicationInfo(packageName, 0);
            Drawable icon = pm.getApplicationIcon(app);
            String appName = (String) pm.getApplicationLabel(app);
            return new AppListModel(icon, appName, packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
