package gaftech.reeltour;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import gaftech.reeltour.models.Tour;
import gaftech.reeltour.models.ToursDatabase;
import gaftech.reeltour.waiter.ControlActivity;

import static android.media.AudioManager.*;

/**
 * Created by r.suleymanov on 28.06.2015.
 * email: ruslancer@gmail.com
 */

public class WelcomeActivity extends ControlActivity {
    Tour currentTour;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(getBaseContext(), WaitingScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        AudioManager mAudioManager = (AudioManager)getSystemService(this.AUDIO_SERVICE);
        setVolumeControlStream(STREAM_MUSIC);
        //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0);

        ToursDatabase td = new ToursDatabase(this);
        currentTour = td.getCurrentTour();
        if (currentTour != null) {
            VideoView vw = (VideoView) findViewById(R.id.greetingVideoView);
            vw.setVideoPath(currentTour.getGreetingsVideoUri());
            MediaController mc = new MediaController(this);
            mc.setAnchorView(vw);
            mc.setMediaPlayer(vw);
            vw.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //mp.setVolume(100, 100);
                    mp.setLooping(false);
                }
            });
            vw.setMediaController(mc);
            vw.start();
            vw.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Intent intent = new Intent(getBaseContext(), RoomsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            Toast.makeText(this, R.string.error_fetching_tour, Toast.LENGTH_LONG).show();
        }
    }


}
