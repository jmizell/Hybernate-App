package net.ottercove.hybernate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoDemoActivity extends ActionBarActivity {
    private VideoView videoView;
    private int position = 0;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_demo);

        if (mediaControls == null) {
            mediaControls = new MediaController(VideoDemoActivity.this) {
                @Override
                public void hide() {
                    super.show();
                }
                @Override
                public boolean dispatchKeyEvent(KeyEvent event) {
                    if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                        Activity a = (Activity)getContext();
                        a.finish();
                    }
                    return true;
                }
            };
        }

        videoView = (VideoView) findViewById(R.id.videoView);

        progressDialog = new ProgressDialog(VideoDemoActivity.this);
        progressDialog.setTitle("Hybernate Usage Demo");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            videoView.setMediaController(mediaControls);
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.create_shortcut));

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        finish();
    }
}
