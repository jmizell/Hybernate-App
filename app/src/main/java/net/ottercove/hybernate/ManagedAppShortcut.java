package net.ottercove.hybernate;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    AppListModel itemValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shortcut);
        pm = this.getPackageManager();

        final ArrayList<AppListModel> items = getPackageList();
        AppListAdapter adapter = new AppListAdapter(this, items);
        listView = (ListView) findViewById(R.id.app_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                itemValue = (AppListModel) listView.getItemAtPosition(position);
                confirmDisable();
            }
        });
    }

    private void disableApp() {
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

        ManagedApp managedapp = new ManagedApp(itemValue.getAppPackage(),
                itemValue.getTitle());

        if (managedapp.disableApp()) {;
            AppNotificationService.sendDisableNotification(this,
                    itemValue.getAppPackage(),
                    itemValue.getTitle(),
                    true);
        } else {
            AppNotificationService.sendDisableNotification(this,
                    itemValue.getAppPackage(),
                    itemValue.getTitle(),
                    false);
        }
        finish();
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
            AppListModel appModel = new AppListModel(icon, appName, packageName);

            if((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                appModel.setSystemApp(true);
            }

            return appModel;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private void confirmDisable() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.disable_app_dialog));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                disableApp();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
