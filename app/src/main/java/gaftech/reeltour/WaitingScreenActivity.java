package gaftech.reeltour;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import gaftech.reeltour.helpers.BitmapResizer;
import gaftech.reeltour.models.Tour;
import gaftech.reeltour.models.ToursDatabase;
import gaftech.reeltour.waiter.ControlActivity;

/**
 * Created by r.suleymanov on 28.06.2015.
 * email: ruslancer@gmail.com
 */

public class WaitingScreenActivity extends ControlActivity {
    Tour currentTour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToursDatabase td = new ToursDatabase(this);
        currentTour = td.getCurrentTour();
        if (currentTour != null) {
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.waiting_screen_pane);

            ImageView im = (ImageView) findViewById(R.id.imageBgWelcomeScreen);

            BitmapResizer br = new BitmapResizer(this);
            Bitmap imageBitmap = BitmapFactory.decodeFile(currentTour.getBackgroundImageUri());
            im.setImageBitmap(br.getResizedBitmap(imageBitmap));
        } else {
            Toast.makeText(this, R.string.error_fetching_tour, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        if(action == MotionEvent.ACTION_DOWN) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return super.onTouchEvent(event);
    }
}
