package net.ottercove.hybernate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class LaunchActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        Context context = getApplicationContext();
        Intent intent = getIntent();
        String appName = intent.getStringExtra(LaunchAppService.NAME);
        String appTitle = intent.getStringExtra(LaunchAppService.TITLE);

        TextView tv1 = (TextView)findViewById(R.id.launch_message);
        tv1.setText("Launching " + appName);

        Intent launch = new Intent(this, LaunchAppService.class);
        launch.putExtra(LaunchAppService.NAME, appName);
        launch.putExtra(LaunchAppService.TITLE, appTitle);
        context.startService(launch);
        finish();
    }
}