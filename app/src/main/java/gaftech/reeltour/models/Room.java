package gaftech.reeltour.models;

import android.database.Cursor;

import java.util.Date;

import gaftech.reeltour.helpers.DateHelper;

/**
 * Created by r.suleymanov on 10.04.2015.
 * email: ruslancer@gmail.com
 */

public class Room {
    private long _id;
    private long _tour_id;
    private String _name;
    private String _video_uri;
    private String _background_image_uri;
    private long _external_id;
    private Date _date_modified;
    private Date _date_synchronized;
    private Date _video_modified;
    DateHelper datetime  = new DateHelper();
    public Room() { }
    public Room(long id, long tour_id, String name, String video, String bg) {
        _id = id;
        _name = name;
        _video_uri = video;
        _tour_id = tour_id;
        _background_image_uri = bg;
    }
    public long getId() { return _id; }
    public String getName() { return _name; }
    public String getVideoUri() { return _video_uri; }
    public String getBgImageUri() { return _background_image_uri; }
    public long getTourId() { return _tour_id; }
    public long getExternalId() { return _external_id; }
    public Date getDateModified() {return _date_modified; }
    public Date getVideoModified() {return _video_modified; }
    public Date getDateSynchronized() {return _date_synchronized; }

    public void setId(long id) { this._id = id; }
    public void setTourId(long tour_id) { this._tour_id = tour_id; }
    public void setName(String name) {this._name = name; }
    public void setExtId(long extid) {this._external_id = extid; }
    public void setBgImageUri(String path) { this._background_image_uri = path; }
    public void setVideoUri(String path) { this._video_uri = path; }
    public void setDateModified(Date date) { this._date_modified = date; }
    public void setDateModified(String date) { this._date_modified = datetime.strToDate(date); }
    public void setDateSynchronized(Date date) { this._date_synchronized = date; }
    public void setDateSynchronized(String date){ this._date_synchronized = datetime.strToDate(date); }
    public void setVideoModified(Date date) { this._video_modified = date; }
    public void setVideoModified(String date) { this._video_modified = datetime.strToDate(date); }

    public static Room fromCursor(Cursor cursor) {
        Room room = new Room();
        room.setId(cursor.getLong(cursor.getColumnIndex(RoomsDatabase._id_column_name)));
        room.setTourId(cursor.getLong(cursor.getColumnIndex(RoomsDatabase._tour_id_column_name)));
        room.setName(cursor.getString(cursor.getColumnIndex(RoomsDatabase._name_column_name)));
        room.setExtId(cursor.getLong(cursor.getColumnIndex(RoomsDatabase._external_id_column_name)));
        room.setBgImageUri(cursor.getString(cursor.getColumnIndex(RoomsDatabase._background_image_uri_column_name)));
        room.setVideoUri(cursor.getString(cursor.getColumnIndex(RoomsDatabase._video_uri_column_name)));
        room.setDateModified(cursor.getString(cursor.getColumnIndex(RoomsDatabase._date_modified_column_name)));
        room.setDateSynchronized(cursor.getString(cursor.getColumnIndex(RoomsDatabase._date_synchronized_column_name)));
        room.setVideoModified(cursor.getString(cursor.getColumnIndex(RoomsDatabase._video_modified_column_name)));
        return room;
    }
}