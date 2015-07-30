package gaftech.reeltour.models;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;

import gaftech.reeltour.helpers.DateHelper;

/**
 * Created by r.suleymanov on 02.07.2015.
 * email: ruslancer@gmail.com
 */

public class Feedback {
    private long _id;
    private long _tour_id;
    private long _external_id;
    private long _author_id;
    private String _email;
    private Date _date_created;
    private Date _date_modified;
    private Date _date_synchronized;
    private Date _date_start;
    ArrayList<QuestionsAnswer> answers = new ArrayList<QuestionsAnswer>();
    DateHelper datetime = new DateHelper();

    public void setId(long id) {this._id  = id;}
    public long getId() {return this._id; }
    public void setEmail(String email) {this._email  = email;}
    public String getEmail() {return this._email; }
    public void setTourId(long id) {this._tour_id  = id;}
    public long getTourId() {return this._tour_id; }
    public void setExternalId(long id) {this._external_id  = id;}
    public long getExternalId() {return this._external_id; }
    public void setAuthorId(long id) {this._author_id  = id;}
    public long getAuthorId() {return this._author_id; }
    public void setDateCreated(Date date) {
        this._date_created = date;
    }
    public void setDateCreated(String date) { this._date_created = datetime.strToDate(date); }
    public Date getDateModified() {return this._date_modified; }
    public Date getDateSynchronized() {return this._date_synchronized; }
    public void setDateModified(Date date) {
        this._date_modified = date;
    }
    public void setDateModified(String date) { this._date_modified = datetime.strToDate(date); }
    public void setDateSynchronized(Date date) {
        this._date_synchronized = date;
    }
    public void setDateSynchronized(String date){ this._date_synchronized = datetime.strToDate(date); }
    public void setDateStart(Date date) {
        this._date_start = date;
    }
    public void setDateStart(String date){ this._date_start = datetime.strToDate(date); }
    public Date getDateStart() {
        return this._date_start;
    }
    public Date getDateCreated() {
        return this._date_created;
    }
    public Feedback() { }
    public Feedback (Cursor cursor) {
        this.setId(cursor.getLong(cursor.getColumnIndex(FeedbacksDatabase._id_column_name)));
        this.setTourId(cursor.getLong(cursor.getColumnIndex(FeedbacksDatabase._tour_id_column_name)));
        this.setExternalId(cursor.getLong(cursor.getColumnIndex(FeedbacksDatabase._external_id_column_name)));
        this.setAuthorId(cursor.getLong(cursor.getColumnIndex(FeedbacksDatabase._author_id_column_name)));
        this.setDateStart(cursor.getString(cursor.getColumnIndex(FeedbacksDatabase._start_date_column_name)));
        this.setDateCreated(cursor.getString(cursor.getColumnIndex(FeedbacksDatabase._date_created_column_name)));
        this.setDateModified(cursor.getString(cursor.getColumnIndex(FeedbacksDatabase._date_modified_column_name)));
        this.setDateSynchronized(cursor.getString(cursor.getColumnIndex(FeedbacksDatabase._date_synchronized_column_name)));
        this.setEmail(cursor.getString(cursor.getColumnIndex(FeedbacksDatabase._email_column_name)));
    }
    public Feedback(long tour_id, long author_id, Date date_start, String email) {
        this.setTourId(tour_id);
        this.setAuthorId(author_id);
        this.setDateStart(date_start);
        this.setEmail(email);
    }

    public void putAnswer(QuestionsAnswer a) {
        this.answers.add(a);
    }
    public ArrayList<QuestionsAnswer> getAnswers() {
        return this.answers;
    }
}
