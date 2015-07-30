package gaftech.reeltour.waiter;

import android.app.Activity;
import android.util.Log;

/**
 * Created by r.suleymanov on 11.07.2015.
 * email: ruslancer@gmail.com
 */

public class ControlActivity extends Activity
{
    private static final String TAG=ControlActivity.class.getName();

    /**
     * Gets reference to global Application
     * @return must always be type of ControlApplication! See AndroidManifest.xml
     */
    public ControlApplication getApp()
    {
        return (ControlApplication )this.getApplication();
    }

    @Override
    public void onUserInteraction()
    {
        super.onUserInteraction();
        getApp().touch();
        Log.d(TAG, "User interaction to " + this.toString());
    }
    protected void onResume() {
        super.onResume();
        getApp().setCurrentActivity(this);

        //Intent intent = new Intent(this, ActivityRestartService.class);
        //startService(intent);
    }
    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences(){
        Activity currActivity = getApp().getCurrentActivity();
        if (currActivity != null && currActivity.equals(this))
            getApp().setCurrentActivity(null);
    }
}