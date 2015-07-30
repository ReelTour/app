package gaftech.reeltour.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import gaftech.reeltour.DatabaseHandler;
import gaftech.reeltour.helpers.DateHelper;

/**
 * Created by r.suleymanov on 02.07.2015.
 * email: ruslancer@gmail.com
 */

public class FeedbacksDatabase extends DatabaseHandler {
    public static final String _id_column_name = "id";
    public static final String _tour_id_column_name = "tour_id";
    public static final String _external_id_column_name = "external_id";
    public static final String _author_id_column_name = "author_id";
    public static final String _start_date_column_name = "start_date";
    public static final String _date_created_column_name = "date_created";
    public static final String _date_modified_column_name = "date_modified";
    public static final String _date_synchronized_column_name = "date_synchronized";
    public static final String _email_column_name = "email";
    DateHelper datetime = new DateHelper();

    public FeedbacksDatabase(Context context) {
        super(context);
    }
    public long add(Feedback fb) {
        if (fb == null) return -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(this._tour_id_column_name, fb.getTourId());
        values.put(this._author_id_column_name, fb.getAuthorId());
        values.put(this._start_date_column_name, datetime.dateToStr(fb.getDateStart()) );
        values.put(this._date_created_column_name, datetime.getCurrentDateFormatted());
        values.put(this._date_modified_column_name, datetime.getCurrentDateFormatted());
        values.put(this._email_column_name, fb.getEmail());
        long id = db.insert(TABLE_FEEDBACKS, null, values);
        db.close();
        if (id>0) return id;
        return -1;
    }
    public Feedback get(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_FEEDBACKS,
                new String[]{
                        this._id_column_name,
                        this._tour_id_column_name,
                        this._external_id_column_name,
                        this._author_id_column_name,
                        this._start_date_column_name,
                        this._date_created_column_name,
                        this._date_modified_column_name,
                        this._date_synchronized_column_name,
                        this._email_column_name
                },
                "id=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
            Feedback r = new Feedback();
            r.setId(cursor.getLong(0));
            r.setTourId(cursor.getLong(1));
            r.setExternalId(cursor.getLong(2));
            r.setAuthorId(cursor.getLong(3));
            r.setDateStart(cursor.getString(4));
            r.setDateCreated(cursor.getString(5));
            r.setDateModified(cursor.getString(6));
            r.setDateSynchronized(cursor.getString(7));
            r.setEmail(cursor.getString(8));
            cursor.close();
            db.close();
            return r;
        }
        return null;
    }
    public int setSynchronized(long id, long extid) {
        Log.d("reel", "Marking feedback as synchronized: " + extid);
        SQLiteDatabase db;
        ContentValues values = new ContentValues();
        values.put(_external_id_column_name, extid);
        String sync_date = datetime.getCurrentDateFormatted();
        values.put(_date_synchronized_column_name, sync_date);
        if (get(id) != null) {
            db = this.getWritableDatabase();
            String strFilter = "id=?";
            db.update(TABLE_FEEDBACKS, values, strFilter, new String[]{Long.toString(id)});
            Log.d("reel", "Set feedback as synchronized at: " + sync_date);
        } else {
            Log.d("reel", "Couldn't set as synchronized, feedback not found: " + sync_date);
        }
        return 0;
    }
}