package gaftech.reeltour.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import gaftech.reeltour.DatabaseHandler;
import gaftech.reeltour.helpers.DateHelper;

/**
 * Created by r.suleymanov on 11.04.2015.
 * email: ruslancer@gmail.com
 */

public class RoomsDatabase extends DatabaseHandler {

    public static final String _id_column_name = "id";
    public static final String _tour_id_column_name = "tour_id";
    public static final String _name_column_name = "name";
    public static final String _external_id_column_name = "external_id";
    public static final String _background_image_uri_column_name = "background_image_uri";
    public static final String _video_uri_column_name = "video_uri";
    public static final String _date_modified_column_name = "date_modified";
    public static final String _video_modified_column_name = "video_modified";
    public static final String _date_synchronized_column_name = "date_synchronized";
    DateHelper datetime;
    public RoomsDatabase(Context context) {
        super(context);
        datetime = new DateHelper();
    }
    public long addRoom(Room r) {
        if (r != null) {
            Room current = null;
            if (r.getId()>0) { current = getRoom(r.getId()); }
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", r.getName());
            values.put("tour_id", r.getTourId());
            values.put("video_uri", r.getVideoUri());
            values.put("background_image_uri", r.getBgImageUri());
            values.put("date_modified", datetime.getCurrentDateFormatted() );
            long id = 0;
            String curr_video_file = "";
            if (current != null) { curr_video_file = current.getVideoUri(); }
            if ( !curr_video_file.equals(r.getVideoUri()) ) {
                values.put(this._video_modified_column_name, datetime.getCurrentDateFormatted());
            }
            if (current != null) {
                String strFilter = "id=?";
                db.update(TABLE_ROOMS, values,  strFilter, new String[] { Long.toString(r.getId()) });
            } else {
                id = db.insert(TABLE_ROOMS, null, values);
            }
            db.close();
            return id;
        } else {
            return -1;
        }
    }

    public int deleteRoom(long id) {
        if (id<=0) return -1;
        SQLiteDatabase db = this.getWritableDatabase();
        String strFilter = "id=?";
        db.delete(TABLE_ROOMS, strFilter, new String[] { Long.toString(id) });
        db.close();
        return 0;
    }

    public Room getRoom(long room_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_ROOMS,
                new String[] {"id","tour_id","name","video_uri","background_image_uri"},
                "id=?", new String[] { String.valueOf(room_id) }, null, null, null, null);
        if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
            Room r = new Room();
            r.setId(cursor.getLong(0));
            r.setTourId(cursor.getLong(1));
            r.setName(cursor.getString(2));
            r.setVideoUri(cursor.getString(3));
            cursor.close();
            db.close();
            return r;
        }
        return null;
    }

    public ArrayList<Room> getRooms(long tour_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_ROOMS, new String[] {"id","tour_id","name","video_uri","background_image_uri"}, "tour_id=?", new String[] { String.valueOf(tour_id) }, null, null, null, null);
        if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
            ArrayList<Room> list = new ArrayList<Room>();
            do {
                long id = cursor.getInt(0);
                long tour = cursor.getInt(1);
                String name = cursor.getString(2);
                String video = cursor.getString(3);
                String bg = cursor.getString(4);
                list.add(new Room(id, tour, name, video, bg));
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
            return list;
        } else {
            db.close();
            return null;
        }
    }

    public int setSynchronized(long id, long extid) {
        Log.d("reel", "Marking room as synchronized: " + extid);
        SQLiteDatabase db;
        ContentValues values = new ContentValues();
        values.put(_external_id_column_name, extid);
        String sync_date = datetime.getCurrentDateFormatted();
        values.put(_date_synchronized_column_name, sync_date);
        if (getRoom(id) != null) {
            db = this.getWritableDatabase();
            String strFilter = "id=?";
            db.update(TABLE_ROOMS, values, strFilter, new String[]{Long.toString(id)});
            Log.d("reel", "Set room as synchronized at: " + sync_date);
        } else {
            Log.d("reel", "Couldn't set as synchronized, room not found: " + sync_date);
        }

        return 0;
    }
}
