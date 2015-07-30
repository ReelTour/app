package gaftech.reeltour.models;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by r.suleymanov on 02.07.2015.
 * email: ruslancer@gmail.com
 */

public class QuestionsAnswer {
    private long _id;
    private long _question_id;
    private long _feedback_id;
    private long _option_chosen;
    private String _comment;
    private Date _date_created;
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.ENGLISH);

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
    public void setFeedbackId(long feedbackId) {
        this._feedback_id = feedbackId;
    }
    public long getFeedbackId() {
        return this._feedback_id;
    }
    public void setOptionChoosenId(long optId) {
        this._option_chosen = optId;
    }
    public long getOptionChoosenId() {
        return this._option_chosen;
    }
    public void setComment(String comment) { this._comment = comment; }
    public String getComment() { return this._comment; }
    public Date getDateCreated() {return _date_created; }
    public void setDateCreated(Date date) {
        this._date_created = date;
    }
    public void setDateCreated(String date) {
        try {
            this._date_created = format.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public QuestionsAnswer() {

    }
    public QuestionsAnswer (Cursor cursor) {
        this.setId(cursor.getLong(cursor.getColumnIndex(QuestionsAnswersDatabase._id_column_name)));
        this.setQuestionId(cursor.getLong(cursor.getColumnIndex(QuestionsAnswersDatabase._question_id_column_name)));
        this.setFeedbackId(cursor.getLong(cursor.getColumnIndex(QuestionsAnswersDatabase._feedback_id_column_name)));
        this.setDateCreated(cursor.getString(cursor.getColumnIndex(QuestionsAnswersDatabase._date_created_column_name)));
        this.setComment(cursor.getString(cursor.getColumnIndex(QuestionsAnswersDatabase._comment_column_name)));
        this.setOptionChoosenId(cursor.getLong(cursor.getColumnIndex(QuestionsAnswersDatabase._option_chosen_column_name)));
    }
    public QuestionsAnswer(long question_id, long feedbackId,  String comment) {
        this.setQuestionId(question_id);
        this.setFeedbackId(feedbackId);
        this.setComment(comment);
    }
}
