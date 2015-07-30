package gaftech.reeltour.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import gaftech.reeltour.DatabaseHandler;
import gaftech.reeltour.helpers.DateHelper;

/**
 * Created by r.suleymanov on 11.04.2015.
 * email: ruslancer@gmail.com
 */

public class ToursDatabase extends DatabaseHandler {
    public static final int DEFAULT_TOUR_ID = 1;
    public static final String _id_column_name = "id";
    public static final String _name_column_name = "name";
    public static final String _external_id_column_name = "external_id";
    public static final String _address_column_name = "address";
    public static final String _author_id_column_name = "author_id";
    public static final String _background_image_uri_column_name = "background_image_uri";
    public static final String _greetings_video_uri_column_name = "greetings_video_uri";
    public static final String _date_modified_column_name = "date_modified";
    public static final String _date_synchronized_column_name = "date_synchronized";
    public static final String _image_modified_column_name = "image_modified";
    public static final String _video_modified_column_name = "video_modified";
    DateHelper datetime;
    public ToursDatabase(Context context) {
        super(context);
        datetime = new DateHelper();
    }
    public Tour getCurrentTour() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_TOURS,
                new String[] {"id","name","address","author_id","background_image_uri","greetings_video_uri"},
                "id=?", new String[] { String.valueOf(this.DEFAULT_TOUR_ID) }, null, null, null, null);
        if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
            long id = Long.parseLong(cursor.getString(0));
            String name = cursor.getString(1);
            String address = cursor.getString(2);
            long author_id = Long.parseLong(cursor.getString(3));
            String background_image_uri = cursor.getString(4);
            String greetings_video_uri = cursor.getString(5);
            cursor.close();
            db.close();
            if (DEFAULT_TOUR_ID == id) {
                Tour t = new Tour(id, name, address, author_id, background_image_uri, greetings_video_uri);
                return t;
            } else {
                return null;
            }
        } else {
            db.close();
            return null;
        }
    }
    public int setCurrentTour(Tour tour, boolean delete_rooms) {
        SQLiteDatabase db;
        Tour current = getCurrentTour();
        ContentValues values = new ContentValues();
        values.put("name", tour.getName());
        values.put("address", tour.getAddress());
        values.put("author_id", tour.getAuthorId());
        values.put("background_image_uri", tour.getBackgroundImageUri());
        values.put("greetings_video_uri", tour.getGreetingsVideoUri());
        if (delete_rooms) {
            values.put("external_id", 0);
        }
        values.put("date_modified", datetime.getCurrentDateFormatted());
        String curr_video_file = "";
        String curr_image_file = "";
        if (current!=null) {
            curr_video_file = current.getGreetingsVideoUri();
            curr_image_file = current.getBackgroundImageUri();
        }
        if ( !curr_video_file.equals(tour.getGreetingsVideoUri()) ) {
            values.put(this._video_modified_column_name, datetime.getCurrentDateFormatted());
        }
        if ( !curr_image_file.equals(tour.getBackgroundImageUri()) ) {
            values.put(this._image_modified_column_name, datetime.getCurrentDateFormatted());
        }
        if (current != null) {
            db = this.getWritableDatabase();
            String strFilter = "id=?";
            db.update(TABLE_TOURS, values, strFilter, new String[] { Long.toString(tour.getId()) } );
        } else {
            db = this.getWritableDatabase();
            values.put("id", tour.getId());
            db.insert(TABLE_TOURS, null, values);
        }
        if (delete_rooms) {
            db.delete(TABLE_ROOMS, null, null);
        }
        db.close();
        return 0;
    }
    public int setSynchronized(long id, long extid) {
        Log.d("reel", "Marking tour as synchronized: " + extid);
        SQLiteDatabase db;
        ContentValues values = new ContentValues();
        values.put(_external_id_column_name, extid);
        String sync_date = datetime.getCurrentDateFormatted();
        values.put(_date_synchronized_column_name, sync_date);
        if (getCurrentTour() != null) {
            db = this.getWritableDatabase();
            String strFilter = "id=?";
            db.update(TABLE_TOURS, values, strFilter, new String[] { Long.toString(id) });
            Log.d("reel", "Set tour as synchronized at: " + sync_date);
        } else {
            Log.d("reel", "Couldn't set as synchronized, tour not found: " + sync_date);
        }
        return 0;
    }

}
