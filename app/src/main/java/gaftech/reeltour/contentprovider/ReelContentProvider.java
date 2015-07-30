package gaftech.reeltour.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import gaftech.reeltour.DatabaseHandler;

/**
 * Created by r.suleymanov on 23.06.2015.
 * email: ruslancer@gmail.com
 */

public class ReelContentProvider extends ContentProvider {
    DatabaseHandler dbHelper;
    public static final String TOURS_PATH = "tours";;
    public static final String TOURS_PATH_FOR_ID = "tours/*";
    public static final String ROOMS_PATH = "rooms";
    public static final String ROOMS_PATH_FOR_ID = "rooms/*";
    public static final String RECORDINGS_PATH = "recordings";
    public static final String RECORDINGS_PATH_FOR_ID = "recordings/*";
    public static final String FEEDBACKS_PATH = "feedbacks";
    public static final String FEEDBACKS_PATH_FOR_ID = "feedbacks/*";
    public static final String QUESTIONS_ANSWERS_PATH = "questions_answers";
    public static final String QUESTIONS_ANSWERS_PATH_FOR_ID = "questions_answers/*";
    public static final int TOURS_PATH_TOKEN = 100;
    public static final int TOURS_PATH_FOR_ID_TOKEN = 101;
    public static final int ROOMS_PATH_TOKEN = 200;
    public static final int ROOMS_PATH_FOR_ID_TOKEN = 201;
    public static final int RECORDINGS_PATH_TOKEN = 300;
    public static final int RECORDINGS_PATH_FOR_ID_TOKEN = 301;
    public static final int QUESTIONS_ANSWERS_PATH_TOKEN = 400;
    public static final int QUESTIONS_ANSWERS_PATH_FOR_ID_TOKEN = 401;
    public static final int FEEDBACKS_PATH_TOKEN = 500;
    public static final int FEEDBACKS_PATH_FOR_ID_TOKEN = 501;
    public static final UriMatcher URI_MATCHER = buildUriMatcher();
    // Uri Matcher for the content provider
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(ReelContentProviderContract.AUTHORITY, TOURS_PATH, TOURS_PATH_TOKEN);
        matcher.addURI(ReelContentProviderContract.AUTHORITY, TOURS_PATH_FOR_ID, TOURS_PATH_FOR_ID_TOKEN);
        matcher.addURI(ReelContentProviderContract.AUTHORITY, ROOMS_PATH, ROOMS_PATH_TOKEN);
        matcher.addURI(ReelContentProviderContract.AUTHORITY, ROOMS_PATH_FOR_ID, ROOMS_PATH_FOR_ID_TOKEN);
        matcher.addURI(ReelContentProviderContract.AUTHORITY, RECORDINGS_PATH, RECORDINGS_PATH_TOKEN);
        matcher.addURI(ReelContentProviderContract.AUTHORITY, RECORDINGS_PATH_FOR_ID, RECORDINGS_PATH_FOR_ID_TOKEN);
        matcher.addURI(ReelContentProviderContract.AUTHORITY,  QUESTIONS_ANSWERS_PATH,  QUESTIONS_ANSWERS_PATH_TOKEN);
        matcher.addURI(ReelContentProviderContract.AUTHORITY,  QUESTIONS_ANSWERS_PATH_FOR_ID,  QUESTIONS_ANSWERS_PATH_FOR_ID_TOKEN);
        matcher.addURI(ReelContentProviderContract.AUTHORITY,  FEEDBACKS_PATH,  FEEDBACKS_PATH_TOKEN);
        matcher.addURI(ReelContentProviderContract.AUTHORITY,  FEEDBACKS_PATH_FOR_ID,  FEEDBACKS_PATH_FOR_ID_TOKEN);
        return matcher;
    }
    @Override
    public boolean onCreate() {
        Context ctx = getContext();
        dbHelper = new DatabaseHandler(ctx);
        return true;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            // retrieve tv shows list
            case TOURS_PATH_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(DatabaseHandler.TABLE_TOURS);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case TOURS_PATH_FOR_ID_TOKEN: {
                int tvShowId = (int) ContentUris.parseId(uri);
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(DatabaseHandler.TABLE_TOURS);
                builder.appendWhere(DatabaseHandler.TABLE_TOURS_ID + "=" + tvShowId);
                return builder.query(db, projection, selection,selectionArgs, null, null, sortOrder);
            }
            case ROOMS_PATH_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(DatabaseHandler.TABLE_ROOMS);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case ROOMS_PATH_FOR_ID_TOKEN: {
                int tvShowId = (int) ContentUris.parseId(uri);
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(DatabaseHandler.TABLE_ROOMS);
                builder.appendWhere(DatabaseHandler.TABLE_ROOMS_ID + "=" + tvShowId);
                return builder.query(db, projection, selection,selectionArgs, null, null, sortOrder);
            }
            case RECORDINGS_PATH_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(DatabaseHandler.TABLE_RECORDINGS);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case RECORDINGS_PATH_FOR_ID_TOKEN: {
                int tvShowId = (int) ContentUris.parseId(uri);
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(DatabaseHandler.TABLE_RECORDINGS);
                builder.appendWhere(DatabaseHandler.TABLE_RECORDINGS_ID + "=" + tvShowId);
                return builder.query(db, projection, selection,selectionArgs, null, null, sortOrder);
            }
            case QUESTIONS_ANSWERS_PATH_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(DatabaseHandler.TABLE_QUESTIONS_ANSWERS);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case QUESTIONS_ANSWERS_PATH_FOR_ID_TOKEN: {
                int tvShowId = (int) ContentUris.parseId(uri);
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(DatabaseHandler.TABLE_QUESTIONS_ANSWERS);
                builder.appendWhere(DatabaseHandler.TABLE_QUESTIONS_ANSWERS_ID + "=" + tvShowId);
                return builder.query(db, projection, selection,selectionArgs, null, null, sortOrder);
            }
            case FEEDBACKS_PATH_TOKEN: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(DatabaseHandler.TABLE_FEEDBACKS);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case FEEDBACKS_PATH_FOR_ID_TOKEN: {
                int tvShowId = (int) ContentUris.parseId(uri);
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(DatabaseHandler.TABLE_FEEDBACKS);
                builder.appendWhere(DatabaseHandler.TABLE_FEEDBACKS_ID + "=" + tvShowId);
                return builder.query(db, projection, selection,selectionArgs, null, null, sortOrder);
            }
            default:
                return null;
        }
    }
    @Override
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case TOURS_PATH_TOKEN:
                return ReelContentProviderContract.CONTENT_TYPE_DIR;
            case TOURS_PATH_FOR_ID_TOKEN:
                return ReelContentProviderContract.CONTENT_TYPE_ITEM;
            case ROOMS_PATH_TOKEN:
                return ReelContentProviderContract.CONTENT_TYPE_DIR;
            case ROOMS_PATH_FOR_ID_TOKEN:
                return ReelContentProviderContract.CONTENT_TYPE_ITEM;
            case RECORDINGS_PATH_TOKEN:
                return ReelContentProviderContract.CONTENT_TYPE_DIR;
            case RECORDINGS_PATH_FOR_ID_TOKEN:
                return ReelContentProviderContract.CONTENT_TYPE_ITEM;
            case QUESTIONS_ANSWERS_PATH_TOKEN:
                return ReelContentProviderContract.CONTENT_TYPE_DIR;
            case QUESTIONS_ANSWERS_PATH_FOR_ID_TOKEN:
                return ReelContentProviderContract.CONTENT_TYPE_ITEM;
            case FEEDBACKS_PATH_TOKEN:
                return ReelContentProviderContract.CONTENT_TYPE_DIR;
            case FEEDBACKS_PATH_FOR_ID_TOKEN:
                return ReelContentProviderContract.CONTENT_TYPE_ITEM;
            default:
                throw new UnsupportedOperationException("URI " + uri + " is not supported.");
        }
    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
