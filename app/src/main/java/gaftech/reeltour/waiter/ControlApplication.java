package gaftech.reeltour.waiter;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

/**
 * Created by r.suleymanov on 11.07.2015.
 * email: ruslancer@gmail.com
 */

public class ControlApplication extends Application {
    private static final String TAG=ControlApplication.class.getName();
    private Waiter waiter;  //Thread which controls idle time

    // only lazy initializations here!
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "Starting application" + this.toString());
        waiter=new Waiter( (int)(5*60*1000), this); //5 mins
        waiter.start();
    }
    public void touch()
    {
        waiter.touch();
    }

    private Activity mCurrentActivity = null;
    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }


}
