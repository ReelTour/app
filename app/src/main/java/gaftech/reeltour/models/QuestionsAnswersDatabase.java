package gaftech.reeltour.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import gaftech.reeltour.DatabaseHandler;

/**
 * Created by r.suleymanov on 01.07.2015.
 * email: ruslancer@gmail.com
 */

public class QuestionsAnswersDatabase extends DatabaseHandler {
    public static final String _id_column_name = "id";
    public static final String _feedback_id_column_name = "feedback_id";
    public static final String _question_id_column_name = "question_id";
    public static final String _option_chosen_column_name = "option_chosen";
    public static final String _comment_column_name = "comment";
    public static final String _date_created_column_name = "date_created";

    public QuestionsAnswersDatabase(Context context) {
        super(context);
    }

    public void addList(ArrayList<QuestionsAnswer> qalist) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i=0; i<qalist.size(); i++) {
            QuestionsAnswer qa = qalist.get(i);
            ContentValues values = new ContentValues();
            values.put(this._feedback_id_column_name, qa.getFeedbackId() );
            values.put(this._question_id_column_name, qa.getQuestionId() );
            values.put(this._option_chosen_column_name, qa.getOptionChoosenId());
            values.put(this._comment_column_name, qa.getComment());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            values.put(this._date_created_column_name, dateFormat.format(new Date()));
            long id = db.insert(TABLE_QUESTIONS_ANSWERS, null, values);
            Log.d("reel", "insert answer id " + id);
        }
        db.close();
    }

    public long add(QuestionsAnswer qa) {
        if (qa == null) return -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(this._feedback_id_column_name, qa.getFeedbackId());
        values.put(this._question_id_column_name, qa.getQuestionId());
        values.put(this._option_chosen_column_name, qa.getOptionChoosenId());
        values.put(this._comment_column_name, qa.getComment());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        values.put(this._date_created_column_name, dateFormat.format(new Date()));
        long id = db.insert(TABLE_QUESTIONS_ANSWERS, null, values);
         db.close();
        if (id>0) return id;
        return -1;
    }

    public QuestionsAnswer get(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_QUESTIONS_ANSWERS,
                new String[]{
                        this._id_column_name,
                        this._feedback_id_column_name,
                        this._question_id_column_name,
                        this._option_chosen_column_name,
                        this._comment_column_name,
                        this._date_created_column_name
                },
                "id=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
            QuestionsAnswer r = new QuestionsAnswer();
            r.setId(cursor.getLong(0));
            r.setFeedbackId(cursor.getLong(1));
            r.setQuestionId(cursor.getLong(2));
            r.setOptionChoosenId(cursor.getLong(3));
            r.setComment(cursor.getString(4));
            r.setDateCreated(cursor.getString(5));
            cursor.close();
            db.close();
            return r;
        }
        return null;
    }

}
