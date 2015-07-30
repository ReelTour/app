package gaftech.reeltour.models;

import android.database.Cursor;

import java.util.Date;

import gaftech.reeltour.helpers.DateHelper;

/**
 * Created by r.suleymanov on 10.04.2015.
 * email: ruslancer@gmail.com
 */

public class Tour {
    private long _id;
    private String _name;
    private String _address;
    private long _author_id;
    private String _background_image_uri;
    private String _greetings_video_uri;
    private Date _date_modified;
    private Date _video_modified;
    private Date _image_modified;
    private Date _date_synchronized;
    private long _external_id;
    DateHelper datetime = new DateHelper();;
    public Tour() {
    }
    public Tour(long id, String name, String address, long author_id, String background_image_uri,
                String greetings_video_uri) {
        _id = id;
        _name = name;
        _author_id = author_id;
        _address   = address;
        _background_image_uri = background_image_uri;
        _greetings_video_uri  = greetings_video_uri;
    }
    public long getId() {
        return _id;
    }
    public String getName() {
        return _name;
    }
    public long getAuthorId() {
        return _author_id;
    }
    public String getBackgroundImageUri() {
        return _background_image_uri;
    }
    public String getGreetingsVideoUri() {
        return _greetings_video_uri;
    }
    public String getAddress() {
        return _address;
    }
    public Date getDateModified() {return _date_modified; }
    public Date getDateSynchronized() {return _date_synchronized; }
    public long getExternalId() { return this._external_id; }
    public Date getImageModified() {return this._image_modified; }
    public Date getVideoModified() {return this._video_modified; }

    public void setId(long id) { this._id = id; }
    public void setName(String name) { this._name = name; }
    public void setAddress(String address) {
        this._address = address;
    }
    public void setExternalId(long extid) {
        this._external_id = extid;
    }
    public void setAuthorId(long authorid) {
        this._author_id = authorid;
    }
    public void setBgImageUri(String path) {
        this._background_image_uri = path;
    }
    public void setGreetingsVideoUri(String path) {
        this._greetings_video_uri = path;
    }
    public void setDateModified(Date date) {
        this._date_modified = date;
    }
    public void setDateModified(String date) { this._date_modified =  datetime.strToDate(date); }
    public void setDateSynchronized(Date date) {
        this._date_synchronized = date;
    }
    public void setDateSynchronized(String date){ this._date_synchronized = datetime.strToDate(date); }
    public void setBgImageModified(Date date) {
        this._image_modified = date;
    }
    public void setBgImageModified(String date) { this._image_modified = datetime.strToDate(date); }
    public void setVideoModified(Date date) {
        this._video_modified = date;
    }
    public void setVideoModified(String date) { this._video_modified = datetime.strToDate(date); }
    public static Tour fromCursor(Cursor cursor) {
        Tour tour = new Tour();
        tour.setId(cursor.getLong(cursor.getColumnIndex(ToursDatabase._id_column_name)));
        tour.setName(cursor.getString(cursor.getColumnIndex(ToursDatabase._name_column_name)));
        tour.setAddress(cursor.getString(cursor.getColumnIndex(ToursDatabase._address_column_name)));
        tour.setExternalId(cursor.getLong(cursor.getColumnIndex(ToursDatabase._external_id_column_name)));
        tour.setAuthorId(cursor.getLong(cursor.getColumnIndex(ToursDatabase._author_id_column_name)));
        tour.setBgImageUri(cursor.getString(cursor.getColumnIndex(ToursDatabase._background_image_uri_column_name)));
        tour.setGreetingsVideoUri(cursor.getString(cursor.getColumnIndex(ToursDatabase._greetings_video_uri_column_name)));
        tour.setDateModified(cursor.getString(cursor.getColumnIndex(ToursDatabase._date_modified_column_name)));
        tour.setDateSynchronized(cursor.getString(cursor.getColumnIndex(ToursDatabase._date_synchronized_column_name)));
        tour.setVideoModified(cursor.getString(cursor.getColumnIndex(ToursDatabase._video_modified_column_name)));
        return tour;
    }
}