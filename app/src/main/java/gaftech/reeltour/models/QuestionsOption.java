package gaftech.reeltour.models;

import android.database.Cursor;

import java.util.Date;

import gaftech.reeltour.helpers.DateHelper;

/**
 * Created by r.suleymanov on 01.07.2015.
 * email: ruslancer@gmail.com
 */

public class QuestionsOption  {
    private long _id;
    private long _question_id;
    private long _external_id;
    private String _label;
    private String _value;
    private long _author_id;
    private long _sort;
    private boolean _require_comment;
    private Date _date_modified;
    private Date _date_created;
    private Date _date_synchronized;
    DateHelper datetime = new DateHelper();
    public void setSort(long sort) {
        this._sort = sort;
    }
    public long getSort() {
        return this._sort;
    }
    public void setId(long id){
        this._id = id;
    }
    public long getId() {
        return this._id;
    }
    public void setQuestionId(long questionId) {
        this._question_id = questionId;
    }
    public long getQuestionId() {
        return this._question_id;
    }
    public void setExternalId(long authorId) {
        this._external_id = authorId;
    }
    public long getExternalId() {
        return this._external_id;
    }
    public void setLabel(String label) {
        this._label = label;
    }
    public String getLabel() {
        return this._label;
    }
    public void setValue(String value) {
        this._value = value;
    }
    public String getValue() {
        return this._value;
    }
    public void setRequireComment(boolean show_options) {
        this._require_comment = show_options;
    }
    public boolean getRequireComment() {
        return this._require_comment;
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
    public QuestionsOption (Cursor cursor) {
        this.setId(cursor.getLong(cursor.getColumnIndex(QuestionsOptionsDatabase._id_column_name)));
        this.setQuestionId(cursor.getLong(cursor.getColumnIndex(QuestionsOptionsDatabase._question_id_column_name)));
        this.setExternalId(cursor.getLong(cursor.getColumnIndex(QuestionsOptionsDatabase._external_id_column_name)));
        this.setDateCreated(cursor.getString(cursor.getColumnIndex(QuestionsOptionsDatabase._date_created_column_name)));
        this.setDateModified(cursor.getString(cursor.getColumnIndex(QuestionsOptionsDatabase._date_modified_column_name)));
        this.setDateSynchronized(cursor.getString(cursor.getColumnIndex(QuestionsOptionsDatabase._date_synchronized_column_name)));
        this.setSort(cursor.getLong(cursor.getColumnIndex(QuestionsOptionsDatabase._sort_column_name)));
        this.setLabel(cursor.getString(cursor.getColumnIndex(QuestionsOptionsDatabase._label_column_name)));
        this.setValue(cursor.getString(cursor.getColumnIndex(QuestionsOptionsDatabase._value_column_name)));
        this.setRequireComment(cursor.getInt(cursor.getColumnIndex(QuestionsOptionsDatabase._require_comment_column_name))==1);
    }
}