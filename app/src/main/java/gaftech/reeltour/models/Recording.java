package gaftech.reeltour.models;

import android.database.Cursor;

import java.util.Date;

import gaftech.reeltour.helpers.DateHelper;

/**
 * Created by r.suleymanov on 28.06.2015.
 * email: ruslancer@gmail.com
 */

public class Recording {
    private long _id;
    private long _feedback_id;
    private long _feedback_ext_id;
    private String _recording_uri;
    private long _external_id;
    private Date _date_modified;
    private Date _date_synchronized;
    DateHelper datetime = new DateHelper();
    public Recording() { }
    public long getId() { return _id; }
    public String getRecordingUri() {
        return _recording_uri;
    }
    public long getFeedbackId() { return _feedback_id; }
    public long getExternalId() { return _external_id; }
    public long getFeedbackExtId() { return _feedback_ext_id; }
    public Date getDateModified() {return _date_modified; }
    public Date getDateSynchronized() {return _date_synchronized; }
    public void setId(long id) { this._id = id; }
    public void setFeedbackId(long feedback_id) { this._feedback_id = feedback_id; }
    public void setFeedbackExtId(long feedback_ext_id) { this._feedback_ext_id = feedback_ext_id; }
    public void setExtId(long extid) {this._external_id = extid; }
    public void setRecordingUri(String path) {this._recording_uri = path; }
    public void setDateModified(Date date) {
        this._date_modified = date;
    }
    public void setDateModified(String date) { this._date_modified = datetime.strToDate(date); }
    public void setDateSynchronized(Date date) {
        this._date_synchronized = date;
    }
    public void setDateSynchronized(String date){ this._date_synchronized = datetime.strToDate(date); }
    public static Recording fromCursor(Cursor cursor) {
        Recording recording = new Recording();
        recording.setId(cursor.getLong(cursor.getColumnIndex(RecordingDatabase._id_column_name)));
        recording.setFeedbackId(cursor.getLong(cursor.getColumnIndex(RecordingDatabase._feedback_id_column_name)));
        recording.setExtId(cursor.getLong(cursor.getColumnIndex(RecordingDatabase._external_id_column_name)));
        recording.setRecordingUri(cursor.getString(cursor.getColumnIndex(RecordingDatabase._recording_uri_column_name)));
        recording.setDateModified(cursor.getString(cursor.getColumnIndex(RecordingDatabase._date_modified_column_name)));
        recording.setDateSynchronized(cursor.getString(cursor.getColumnIndex(RecordingDatabase._date_synchronized_column_name)));
        return recording;
    }
}
