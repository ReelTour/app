package gaftech.reeltour.contentprovider;

import android.net.Uri;

/**
 * Created by r.suleymanov on 23.06.2015.
 * email: ruslancer@gmail.com
 */

public class ReelContentProviderContract {
        public static final String AUTHORITY = "Gaftech.ReelTour.provider.reeltour";

        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.reel.tour";
        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd.reel.tour";
        // content://<authority>/<path to type>
        public static final Uri TOURS_URI = Uri.parse("content://"+AUTHORITY+"/tours");
        public static final Uri ROOMS_URI = Uri.parse("content://"+AUTHORITY+"/rooms");
        public static final Uri RECORDINGS_URI = Uri.parse("content://"+AUTHORITY+"/recordings");
        public static final Uri FEEDBACKS_URI = Uri.parse("content://"+AUTHORITY+"/feedbacks");
        public static final Uri QUESTIONS_ANSWERS_URI = Uri.parse("content://"+AUTHORITY+"/questions_answers");
}