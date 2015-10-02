package net.ottercove.hybernate;

import android.graphics.drawable.Drawable;

/**
 * Created by ottah on 9/27/15.
 */
public class AppListModel implements Comparable<AppListModel> {
    private Drawable icon;
    private String title;
    private String appPackage;
    private Boolean systemApp;

    private boolean isGroupHeader = false;

    public AppListModel(Drawable icon, String title, String appPackage) {
        super();
        this.icon = icon;
        this.title = title;
        this.appPackage = appPackage;
        this.systemApp = false;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAppPackage() {
        return this.appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public void setSystemApp(Boolean systemApp) {
        this.systemApp = systemApp;
    }

    public Boolean getSystemApp() {
        return this.systemApp;
    }

    @Override
    public int compareTo(AppListModel other) {
        return this.getTitle().compareTo(other.getTitle());
    }
}
