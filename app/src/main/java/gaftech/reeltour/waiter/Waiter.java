package gaftech.reeltour.waiter;

import android.content.Intent;
import android.util.Log;

import gaftech.reeltour.WaitingScreenActivity;
import gaftech.reeltour.helpers.SharedSession;
import gaftech.reeltour.models.ToursDatabase;

/**
 * Created by r.suleymanov on 11.07.2015.
 * email: ruslancer@gmail.com
 */

public class Waiter extends Thread {
    private static final String TAG=Waiter.class.getName();
    private long lastUsed;
    private long period;
    private boolean stop;
    private ControlApplication app;
    public SharedSession msharedSession;
    ToursDatabase tours;
    public Waiter(long period, ControlApplication app) {
        this.period=period; stop=false;
        this.app = app;
        msharedSession = new SharedSession(app);
        tours = new ToursDatabase(app);
    }
    public void run() {
        long idle=0;
        this.touch();
        do {
            idle=System.currentTimeMillis()-lastUsed;
            try {
                Thread.sleep(5000); //check every 5 seconds
            } catch (InterruptedException e) {
                Log.d(TAG, "Waiter interrupted!");
            }
            if(idle > period) {
                idle=0;
                Log.d(TAG, idle + " > " + period);
                ControlActivity  ca = (ControlActivity)app.getCurrentActivity();
                if ( ca != null && msharedSession.hasMembership()) {
                    if (tours.getCurrentTour() != null) {
                        Intent intent = new Intent(ca, WaitingScreenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        app.startActivity(intent);
                        ca.finish();
                    }
                }
            }
        } while(!stop);
    }
    public synchronized void touch() {
        lastUsed=System.currentTimeMillis();
    }
    public synchronized void forceInterrupt() {
        this.interrupt();
    }
    public synchronized void stopMe() {
        stop=true;
        super.stop();
    }
    public synchronized void setPeriod(long period) {
        this.period=period;
    }

}
