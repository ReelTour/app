package gaftech.reeltour.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import gaftech.reeltour.DatabaseHandler;
import gaftech.reeltour.helpers.DateHelper;

/**
 * Created by r.suleymanov on 28.06.2015.
 * email: ruslancer@gmail.com
 */

public class RecordingDatabase extends DatabaseHandler {
        public static final String _id_column_name = "id";
        public static final String _feedback_id_column_name = "feedback_id";
        public static final String _external_id_column_name = "external_id";
        public static final String _recording_uri_column_name = "recording_uri";
        public static final String _date_modified_column_name = "date_modified";
        public static final String _date_synchronized_column_name = "date_synchronized";
        DateHelper datetime = new DateHelper();
        public RecordingDatabase(Context context) {
            super(context);
        }
        public long addRecording(Recording recording) {
            if (recording != null) {
                Recording current = null;
                if (recording.getId()>0) { current = getRecording(recording.getId()); }
                long id = 0;
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(this._feedback_id_column_name,    recording.getFeedbackId());
                values.put(this._recording_uri_column_name,  recording.getRecordingUri());
                values.put(this._date_modified_column_name,  datetime.getCurrentDateFormatted());
                if (current != null) {
                    String strFilter = "id=?";
                    db.update(TABLE_RECORDINGS, values,  strFilter, new String[] { Long.toString(recording.getId()) });
                } else {
                    id = db.insert(TABLE_RECORDINGS, null, values);
                }
                db.close();
                return id;
            }
            return -1;
        }
        public Recording getRecording(long recording_id) {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.query(TABLE_RECORDINGS,
                    new String[] {this._id_column_name, this._feedback_id_column_name, this._recording_uri_column_name,
                    this._external_id_column_name, this._date_modified_column_name, this._date_synchronized_column_name},
                    "id=?", new String[] { String.valueOf(recording_id) }, null, null, null, null);
            if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
                Recording r = new Recording();
                r.setId(cursor.getLong(0));
                r.setFeedbackId(cursor.getLong(1));
                r.setRecordingUri(cursor.getString(2));
                r.setExtId(cursor.getLong(3));
                r.setDateModified(cursor.getString(4));
                if (cursor.getString(5)!=null) { r.setDateSynchronized(cursor.getString(5)); }
                cursor.close();
                db.close();
                return r;
            }
            return null;
        }
        public int setSynchronized(long id, long extid) {
            Log.d("reel", "Marking recording as synchronized: " + extid);
            SQLiteDatabase db;
            ContentValues values = new ContentValues();
            values.put(_external_id_column_name, extid);
            String sync_date = datetime.getCurrentDateFormatted();
            values.put(_date_synchronized_column_name, sync_date);
            if (getRecording(id) != null) {
                db = this.getWritableDatabase();
                String strFilter = "id=?";
                db.update(TABLE_RECORDINGS, values, strFilter, new String[]{Long.toString(id)});
                Log.d("reel", "Set recording as synchronized at: " + sync_date);
            } else {
                Log.d("reel", "Couldn't set as synchronized, recording not found: " + sync_date);
            }
            return 0;
        }
}