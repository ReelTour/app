package gaftech.reeltour.models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import gaftech.reeltour.DatabaseHandler;

/**
 * Created by r.suleymanov on 01.07.2015.
 * email: ruslancer@gmail.com
 */

public class QuestionsDatabase extends DatabaseHandler {
    public static final String _id_column_name = "id";
    public static final String _tour_id_column_name = "tour_id";
    public static final String _external_id_column_name = "external_id";
    public static final String _message_column_name = "message";
    public static final String _author_id_column_name = "author_id";
    public static final String _show_options_column_name = "show_options";
    public static final String _date_modified_column_name = "date_modified";
    public static final String _date_created_column_name = "date_created";
    public static final String _date_synchronized_column_name = "date_synchronized";
    public static final String _sort_column_name = "sort";
    public QuestionsDatabase(Context context) {
        super(context);
    }
    public ArrayList<Question> getList() {
        ArrayList<Question> list = new ArrayList<Question>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_QUESTIONS, new String[]{}, this._tour_id_column_name + "=?",
        new String[]{String.valueOf(ToursDatabase.DEFAULT_TOUR_ID)}, null, null, "sort desc, id desc", null);
        while (cursor.moveToNext()) {
            Question q = new Question(cursor);
            if (q.getShowOptions()) {
                Cursor cursor1 = db.query(TABLE_QUESTIONS_OPTIONS, new String[]{},
                    QuestionsOptionsDatabase._question_id_column_name + "=?",
                    new String[]{ String.valueOf(q.getId()) }, null, null, "sort desc, id desc", null
                );
                while (cursor1.moveToNext()) { q.addOption(new QuestionsOption(cursor1)); }
                cursor1.close();
            }
            list.add(q);
        }
        cursor.close();
        return list;
    }
}