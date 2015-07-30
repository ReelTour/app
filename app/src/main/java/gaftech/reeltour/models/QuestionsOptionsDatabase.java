package gaftech.reeltour.models;

import android.content.Context;

import gaftech.reeltour.DatabaseHandler;

/**
 * Created by r.suleymanov on 01.07.2015.
 * email: ruslancer@gmail.com
 */

public class QuestionsOptionsDatabase extends DatabaseHandler {
    public static final String _id_column_name = "id";
    public static final String _question_id_column_name = "question_id";
    public static final String _external_id_column_name = "external_id";
    public static final String _label_column_name = "label";
    public static final String _value_column_name = "value";
    public static final String _require_comment_column_name = "require_comment";
    public static final String _date_modified_column_name = "date_modified";
    public static final String _date_created_column_name = "date_created";
    public static final String _date_synchronized_column_name = "date_synchronized";
    public static final String _sort_column_name = "sort";
    public QuestionsOptionsDatabase(Context context) {
        super(context);
    }
}