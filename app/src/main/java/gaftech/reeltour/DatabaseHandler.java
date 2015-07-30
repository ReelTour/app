package gaftech.reeltour;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import gaftech.reeltour.models.ToursDatabase;

/**
 * Created by r.suleymanov on 10.04.2015.
 * email: ruslancer@gmail.com
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "realty";
    private static final int DATABASE_VERSION = 100025;
    private static DatabaseHandler sInstance;

   // protected static final String TABLE_AGENTS = "agents";
    public static final String TABLE_TOURS  = "tours";
    public static final String TABLE_ROOMS = "rooms";
    public static final String TABLE_RECORDINGS = "recordings";
    public static final String TABLE_QUESTIONS = "questions";
    public static final String TABLE_QUESTIONS_OPTIONS = "questions_options";
    public static final String TABLE_QUESTIONS_ANSWERS = "questions_answers";
    public static final String TABLE_FEEDBACKS = "feedbacks";

    public static final String TABLE_TOURS_ID = "id";
    public static final String TABLE_ROOMS_ID = "id";
    public static final String TABLE_RECORDINGS_ID = "id";
    public static final String TABLE_QUESTIONS_ANSWERS_ID="id";
    public static final String TABLE_FEEDBACKS_ID="id";

    public static synchronized DatabaseHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    private static final String createToursTable = "create table "+TABLE_TOURS+" (" +
            "id integer primary key autoincrement," +
            "external_id integer default null," +
            "name varchar(255)," +
            "address varchar(255) not null," +
            "author_id integer not null," +
            "background_image_uri varchar(255)," +
            "greetings_video_uri varchar(255)," +
            "date_modified DATETIME DEFAULT CURRENT_TIMESTAMP,"+
            "image_modified DATETIME DEFAULT CURRENT_TIMESTAMP,"+
            "video_modified DATETIME DEFAULT CURRENT_TIMESTAMP,"+
            "date_synchronized DATETIME default null"+
            ");";
    private static final String createRoomsTable = "create table "+TABLE_ROOMS+" (" +
            "id integer primary key autoincrement," +
            "tour_id integer NOT NULL,"+
            "name varchar(255)," +
            "video_uri varchar(255)," +
            "background_image_uri varchar(255)," +
            "external_id integer DEFAULT NULL," +
            "date_modified DATETIME DEFAULT CURRENT_TIMESTAMP,"+
            "video_modified DATETIME DEFAULT CURRENT_TIMESTAMP,"+
            "date_synchronized DATETIME DEFAULT NULL"+
            ");";
    private static final String createRecordingsTable = "create table "+TABLE_RECORDINGS+" ("+
            "id integer primary key autoincrement,"+
            "feedback_id integer not null,"+
            "recording_uri varchar(255),"+
            "external_id integer DEFAULT NULL,"+
            "date_modified DATETIME DEFAULT CURRENT_TIMESTAMP,"+
            "date_created DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "date_synchronized DATETIME DEFAULT NULL" +
            ");";

    private static final String createQuestionsOptionsTable = "create table "+TABLE_QUESTIONS_OPTIONS+" ("+
            "id integer primary key autoincrement,"+
            "question_id integer not null,"+
            "external_id integer DEFAULT NULL,"+
            "label varchar(255) not null,"+
            "value varchar(255) not null,"+
            "sort int not null default 0,"+
            "require_comment integer default 0,"+
            "date_modified DATETIME DEFAULT CURRENT_TIMESTAMP,"+
            "date_created DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "date_synchronized DATETIME DEFAULT NULL" +
            ");";
    private static final String createFeedbacks = "create table "+TABLE_FEEDBACKS+" ("+
            "id integer primary key autoincrement,"+
            "tour_id integer not null,"+
            "start_date DATETIME NOT NULL," +
            "external_id integer DEFAULT NULL,"+
            "author_id int not null,"+
            "email varchar(255) default null,"+
            "date_modified DATETIME DEFAULT CURRENT_TIMESTAMP,"+
            "date_created DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "date_synchronized DATETIME DEFAULT NULL" +
            ");";
    private static final String createQuestionsAnswers = "create table "+TABLE_QUESTIONS_ANSWERS+" ("+
            "id integer primary key autoincrement,"+
            "question_id integer not null,"+
            "feedback_id integer not null,"+
            "option_chosen integer default null,"+
            "comment varchar(255) default null,"+
            "date_created DATETIME DEFAULT CURRENT_TIMESTAMP"+
            ");";
    private static final String createQuestionsTable = "create table "+TABLE_QUESTIONS+" ("+
            "id integer primary key autoincrement,"+
            "tour_id integer not null,"+
            "external_id integer DEFAULT NULL,"+
            "message varchar(255) not null,"+
            "author_id int not null,"+
            "show_options integer default 0," +
            "sort int not null default 0,"+
            "date_modified DATETIME DEFAULT CURRENT_TIMESTAMP,"+
            "date_created DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "date_synchronized DATETIME DEFAULT NULL" +
            ");";
    private static final String fillQuestions = "INSERT INTO "+TABLE_QUESTIONS+" (" +
            "id, tour_id, external_id, message, author_id, show_options, sort" +
            ") VALUES " +
            "(1, "+ ToursDatabase.DEFAULT_TOUR_ID+", 0, 'What did you think of the condition of the home?', 1, 1, 5)," +
            "(2, "+ToursDatabase.DEFAULT_TOUR_ID+", 0,'What did you think of the price?', 1, 1, 4)," +
            "(3, "+ToursDatabase.DEFAULT_TOUR_ID+", 0,'Can you see yourself living in this home?', 1, 1, 3)," +
            "(4, "+ToursDatabase.DEFAULT_TOUR_ID+", 0,'Would you consider making an offer?', 1, 1, 2)," +
            "(5, "+ToursDatabase.DEFAULT_TOUR_ID+", 0,'Any other comments or concerns?', 1, 1, 1)," +
            "(6, "+ToursDatabase.DEFAULT_TOUR_ID+", 0,'Buyers Agents Name', 1, 0, 0);";

    private static final String fillQuestionsOptions = "INSERT INTO "+TABLE_QUESTIONS_OPTIONS+" (" +
            "id, question_id, external_id, label, value, require_comment, sort" +
            ") VALUES "+
            "(1, 1, 0, 'Excellent', 'excellent', 0, 5)," +
            "(2, 1, 0, 'Good', 'good', 0, 4)," +
            "(3, 1, 0, 'Fair', 'fair', 0, 3)," +
            "(4, 1, 0, 'Needs Work', 'needs_work', 0, 2)," +
            "(5, 1, 0, 'Poor', 'poor', 0, 1)," +
            "(6, 2, 0, 'Under Market Value', 'under_market_value', 0, 4)," +
            "(8, 2, 0, 'At Market Value', 'at_market_value', 0, 2)," +
            "(9, 2, 0, 'Over Market Value', 'over_maket_value', 0, 1)," +
            "(10, 3, 0, 'Yes', 'yes', 0, 2)," +
            "(11, 3, 0, 'No', 'no', 0, 1)," +
            "(12, 4, 0, 'Yes', 'yes', 0, 2)," +
            "(13, 4, 0, 'No', 'no', 0, 1)," +
            "(14, 5, 0, 'Yes', 'yes', 1, 2)," +
            "(15, 5, 0, 'No', 'no', 0, 1);";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_TOURS);
        db.execSQL("drop table if exists " + TABLE_ROOMS);
        db.execSQL("drop table if exists " + TABLE_RECORDINGS);
        db.execSQL("drop table if exists " + TABLE_QUESTIONS);
        db.execSQL("drop table if exists " + TABLE_QUESTIONS_OPTIONS);
        db.execSQL("drop table if exists " + TABLE_QUESTIONS_ANSWERS);
        db.execSQL("drop table if exists " + TABLE_FEEDBACKS);
        this.onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createToursTable);
        db.execSQL(createRoomsTable);
        db.execSQL(createRecordingsTable);
        db.execSQL(createQuestionsTable);
        db.execSQL(createQuestionsOptionsTable);
        db.execSQL(createQuestionsAnswers);
        db.execSQL(createFeedbacks);

        db.execSQL(fillQuestions);
        db.execSQL(fillQuestionsOptions);
    }

}
