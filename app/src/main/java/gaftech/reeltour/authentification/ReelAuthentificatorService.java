package gaftech.reeltour.authentification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by r.suleymanov on 23.06.2015.
 * email: ruslancer@gmail.com
 */

public class ReelAuthentificatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        ReelAuthentificator authenticator = new ReelAuthentificator(this);
        Log.d("reel authentificator", authenticator.toString());
        return authenticator.getIBinder();
    }
}
