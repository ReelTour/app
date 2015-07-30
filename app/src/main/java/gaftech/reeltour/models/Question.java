package gaftech.reeltour.models;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;

import gaftech.reeltour.helpers.DateHelper;

/**
 * Created by r.suleymanov on 01.07.2015.
 * email: ruslancer@gmail.com
 */

public class Question {
    private long _id;
    private long _tour_id;
    private long _external_id;
    private String _message;
    private long _author_id;
    private long _sort;
    private boolean _show_options;
    private Date _date_modified;
    private Date _date_created;
    private Date _date_synchronized;
    DateHelper datetime;
    public void setId(long id){
        this._id = id;
    }
    public long getId() {
        return this._id;
    }
    public void setTourId(long tour_id) {
        this._tour_id = tour_id;
    }
    public long getTourId() {
        return this._tour_id;
    }
    public void setExternalId(long externalId) {
        this._external_id = externalId;
    }
    public void setSort(long sort) {
        this._sort = sort;
    }
    public long getSort() {
        return this._sort;
    }
    public long getExternalId() {
        return this._external_id;
    }
    public void setAuthorId(long authorId) {
        this._author_id = authorId;
    }
    public long getAuthorId() {
        return this._author_id;
    }
    public void setMessage(String message) {
        this._message = message;
    }
    public String getMessage() {
        return this._message;
    }
    public void setShowOptions(boolean show_options) {
        this._show_options = show_options;
    }
    public boolean getShowOptions() {
        return this._show_options;
    }
    public Date getDateCreated() {return _date_created; }
    public Date getDateModified() {return _date_modified; }
    public Date getDateSynchronized() {return _date_synchronized; }
    public void setDateCreated(Date date) {
        this._date_created = date;
    }
    public void setDateCreated(String date) { this._date_created = datetime.strToDate(date); }
    public void setDateModified(Date date) {
        this._date_modified = date;
    }
    public void setDateModified(String date) { this._date_modified = datetime.strToDate(date); }
    public void setDateSynchronized(Date date) {
        this._date_synchronized = date;
    }
    public void setDateSynchronized(String date){ this._date_synchronized = datetime.strToDate(date); }
    public ArrayList<QuestionsOption> options;
    public Question (Cursor cursor) {
        datetime  = new DateHelper();
        this.setId(cursor.getLong(cursor.getColumnIndex(QuestionsDatabase._id_column_name)));
        this.setTourId(cursor.getLong(cursor.getColumnIndex(QuestionsDatabase._tour_id_column_name)));
        this.setExternalId(cursor.getLong(cursor.getColumnIndex(QuestionsDatabase._external_id_column_name)));
        this.setMessage(cursor.getString(cursor.getColumnIndex(QuestionsDatabase._message_column_name)));
        this.setAuthorId(cursor.getLong(cursor.getColumnIndex(QuestionsDatabase._author_id_column_name)));
        this.setDateCreated(cursor.getString(cursor.getColumnIndex(QuestionsDatabase._date_created_column_name)));
        this.setDateModified(cursor.getString(cursor.getColumnIndex(QuestionsDatabase._date_modified_column_name)));
        this.setDateSynchronized(cursor.getString(cursor.getColumnIndex(QuestionsDatabase._date_synchronized_column_name)));
        this.setShowOptions(cursor.getInt(cursor.getColumnIndex(QuestionsDatabase._show_options_column_name)) == 1);
        this.setSort(cursor.getLong(cursor.getColumnIndex(QuestionsDatabase._sort_column_name)));
        if (this.getShowOptions()) {
            options = new ArrayList<QuestionsOption>();
        }
    }
    public void addOption(QuestionsOption option) {
        options.add(option);
    }
}
