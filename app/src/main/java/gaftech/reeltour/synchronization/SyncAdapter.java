package gaftech.reeltour.synchronization;

/**
 * Created by r.suleymanov on 26.06.2015.
 * email: ruslancer@gmail.com
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import gaftech.reeltour.models.Feedback;
import gaftech.reeltour.models.FeedbacksDatabase;
import gaftech.reeltour.models.QuestionsAnswer;
import gaftech.reeltour.models.Recording;
import gaftech.reeltour.models.RecordingDatabase;
import gaftech.reeltour.contentprovider.ReelContentProviderContract;
import gaftech.reeltour.models.Room;
import gaftech.reeltour.models.RoomsDatabase;
import gaftech.reeltour.models.Tour;
import gaftech.reeltour.models.ToursDatabase;
import gaftech.reeltour.authentification.AccountGeneral;
import gaftech.reeltour.reelapi.ReelApiManager;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    // Global variables
    // Define a variable to contain a content resolver instance
    private AccountManager mAccountManager;
   // private ContentResolver mContentResolver;
    private ReelApiManager mApiManager;
    private Context context;

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        this.context = context;
        mAccountManager  = AccountManager.get(context);
       // mContentResolver = context.getContentResolver();
        mApiManager = new ReelApiManager(context);
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        //mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d("reeltour", "onPerformSync for account[" + account.name + "]");
        try {
            String authToken = mAccountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_API, true);
            Log.d("reel", authToken);
            String uid = mAccountManager.getUserData(account, AccountGeneral.USER_ID_FIELD);

            if (uid == null) throw new Exception();
            Log.d("reel", "Current user has id: " + uid);
            long author_id = Integer.valueOf(uid);
            long extid = 0;
            Tour tour = null;
            //tours
            ArrayList<Tour> localTours = new ArrayList<Tour>();
            Cursor tours = provider.query(ReelContentProviderContract.TOURS_URI, null, null, null, null);
            if (tours != null) {
                while (tours.moveToNext()) {
                    tour = Tour.fromCursor(tours);
                    Log.d("reel", "Checking tour: " + tour.getAddress());
                    if (tour.getAuthorId()==author_id || tour.getAuthorId()<=0) {
                        Boolean add = false;
                        if (tour.getDateModified()!=null && tour.getDateSynchronized()!=null) {
                            add = tour.getDateModified().after(tour.getDateSynchronized());
                            Log.d("reel", "Last update check: "+tour.getDateModified().toString()+" and "+tour.getDateSynchronized().toString());
                            Log.d("reel", (add)?"Needs to be updated": "No need to update");
                        } else add = true;
                        if (add) {
                            Log.d("reel", "Adding tour for update: " + tour.getAddress());
                            localTours.add(tour);
                        }
                    } else {
                        Log.d("reel", "Wrong author: " + tour.getAuthorId());
                    }
                }
                tours.close();
            }
            if (localTours.size() > 0) {
                extid = mApiManager.putTour(authToken, tour);
                if ( extid > 0 ) {
                    Log.d("reel", "External id retrieved: " + extid);
                    ToursDatabase td  = new ToursDatabase(context);
                    td.setSynchronized(tour.getId(), extid);
                } else {
                    Log.d("reel", "No external id, something went wrong");
                }
            }
            if (extid == 0) {
                if (tour.getExternalId() == 0)
                    throw new Exception("Couldn't find active tour");
                else
                    extid = tour.getExternalId();
            }
            Thread.sleep(2000);
            //rooms update
            Log.d("reel", "Start Rooms update");
            Log.d("reel", "Current tour " + tour.getId());
            ArrayList<Room> localRooms = new ArrayList<Room>();
            String[] selectionArgs = new String[1];
            String selection = "tour_id = ?";
            selectionArgs[0] = String.valueOf(tour.getId());
            Cursor rooms = provider.query(ReelContentProviderContract.ROOMS_URI,null, selection,  selectionArgs, null);
            if (rooms != null) {
                Log.d("reel", "Rooms retrieved " + String.valueOf(rooms.getCount()) );
                while (rooms.moveToNext()) {
                    Room room = Room.fromCursor(rooms);
                    Log.d("reel", "Room added: " + room.getName());                    boolean add = false;
                    if (room.getDateModified()!=null && room.getDateSynchronized()!=null) {
                        add = room.getDateModified().after(room.getDateSynchronized());
                    } else add = true;
                    if (add) {
                        localRooms.add(room);
                    }
                }
                rooms.close();
            }
            if (localRooms.size() > 0) {
                for (int i = 0; i < localRooms.size(); i++) {
                    int room_extid = mApiManager.putRoom(authToken, localRooms.get(i), extid);
                    if (room_extid > 0) {
                        Log.d("reel", "External id retrieved: " + extid);
                        RoomsDatabase rd = new RoomsDatabase(context);
                        rd.setSynchronized(localRooms.get(i).getId(), room_extid);
                    } else {
                        Log.d("reel", "No external id, something went wrong");
                    }
                }
            }
            Thread.sleep(2000);
            //feedbacks update
            Log.d("reel", "Start feedback update");
            ArrayList<Feedback> localFeedbacks = new ArrayList<Feedback>();
            selectionArgs = new String[2];
            String selection_feedbacks = "tour_id = ? and author_id = ?";
            selectionArgs[0] = String.valueOf(tour.getId());
            selectionArgs[1] = String.valueOf(author_id);
            Cursor feedbacks = provider.query(ReelContentProviderContract.FEEDBACKS_URI, null, selection_feedbacks,selectionArgs, null);
            if (feedbacks != null) {
                while (feedbacks.moveToNext()) {
                    Feedback fb = new Feedback(feedbacks);
                    boolean add = false;
                    if (fb.getDateModified()!=null && fb.getDateSynchronized()!=null) {
                        add = fb.getDateModified().after(fb.getDateSynchronized());
                    } else add = true;
                    if (add) {
                        localFeedbacks.add(fb);
                        Log.d("reel", "Feedback added :" + fb.getId());
                    }
                }
            }
            feedbacks.close();
            if (localFeedbacks.size() > 0) {
                for (int i = 0; i < localFeedbacks.size(); i++) {
                    String[] selArgs = new String[1];
                    String selection_answers = "feedback_id=?";
                    selArgs[0] = String.valueOf(localFeedbacks.get(i).getId());
                    Log.d("reel", "Look for answers for feedback " + localFeedbacks.get(i).getId());
                    Cursor answers = provider.query(ReelContentProviderContract.QUESTIONS_ANSWERS_URI, null,
                            selection_answers, selArgs, null);
                    Log.d("reel", answers.toString());
                    if (answers != null) {
                        while (answers.moveToNext()) {
                            QuestionsAnswer qa = new QuestionsAnswer(answers);
                            localFeedbacks.get(i).putAnswer(qa);
                            Log.d("reel", "Add answer to feedback for question " + qa.getQuestionId());
                        }
                        answers.close();
                    }
                    int rec_extid = mApiManager.putFeedback(authToken, localFeedbacks.get(i), extid);
                    if (rec_extid > 0) {
                        Log.d("reel", "External id retrieved: " + extid);
                        FeedbacksDatabase rd1 = new FeedbacksDatabase(context);
                        rd1.setSynchronized(localFeedbacks.get(i).getId(), rec_extid);
                        localFeedbacks.get(i).setExternalId(rec_extid);
                    } else {
                        Log.d("reel", "No external id, something went wrong");
                    }
                }
            }
            Thread.sleep(2000);
            //recorgings update
            Log.d("reel", "Start Recordings update");
            ArrayList<Recording> localRecordings = new ArrayList<Recording>();
            selectionArgs = new String[1];
            String selection_recordings = "feedback_id > ?";
            selectionArgs[0] = String.valueOf(0);
            Cursor recordings = provider.query(ReelContentProviderContract.RECORDINGS_URI, null,
                    selection_recordings,
                    selectionArgs, null);
            if (recordings != null) {
                while (recordings.moveToNext()) {
                    Recording rec = Recording.fromCursor(recordings);
                    long fint = rec.getFeedbackId();
                    selectionArgs = new String[1];
                    String selection_feedback = "id=?";
                    selectionArgs[0] = String.valueOf(fint);
                    Cursor feedback = provider.query(ReelContentProviderContract.FEEDBACKS_URI, null,
                            selection_feedback, selectionArgs, null);
                    Feedback fb = null;
                    if (feedback != null) {
                        feedback.moveToNext();
                        fb = new Feedback(feedback);
                    }
                    feedback.close();
                    if (fb==null || fb.getExternalId()<=0) continue;
                    rec.setFeedbackExtId(fb.getExternalId());

                    Log.d("reel", "Recording added, uri:" + rec.getRecordingUri());
                    boolean add = false;
                    if (rec.getDateModified() != null && rec.getDateSynchronized() != null) {
                        add = rec.getDateModified().after(rec.getDateSynchronized());
                    } else add = true;
                    if (add) {
                        localRecordings.add(rec);
                    }
                }
                recordings.close();
            }
            if (localRecordings.size() > 0) {
                for (int i = 0; i < localRecordings.size(); i++) {
                    long rec_extid = mApiManager.putRecording(authToken, localRecordings.get(i));
                    if (rec_extid > 0) {
                        Log.d("reel", "External id retrieved: " + extid);
                        RecordingDatabase rd1 = new RecordingDatabase(context);
                        rd1.setSynchronized(localRecordings.get(i).getId(), rec_extid);
                    } else {
                        Log.d("reel", "No external id, something went wrong");
                    }
                }
            }//

            /*
            //answers update
            Log.d("reel", "Start Answers update");
            ArrayList<QuestionsAnswer> localAnswers = new ArrayList<QuestionsAnswer>();
            selectionArgs = new String[1];
            String selection_answers = "tour_id = ?";
            selectionArgs[0] = String.valueOf(tour.getId());
            Cursor answers = provider.query(ReelContentProviderContract.QUESTIONS_ANSWERS_URI, null, selection,selectionArgs, null);
            if (answers != null) {
                while (answers.moveToNext()) {
                    QuestionsAnswer qa = new QuestionsAnswer(answers);
                    Log.d("reel", "Answer added, uri:" + qa.getOptionChoosenId());
                    boolean add = false;
                    if (qa.getDateModified()!=null && qa.getDateSynchronized()!=null) {
                        add = qa.getDateModified().after(qa.getDateSynchronized());
                    } else add = true;
                    if (add) {
                        localAnswers.add(qa);
                    }
                }
                answers.close();
            }
            if (localAnswers.size() > 0) {
                for (int i = 0; i < localAnswers.size(); i++) {
                    int rec_extid = mApiManager.putAnswer(authToken, localAnswers.get(i), extid);
                    if (rec_extid > 0) {
                        Log.d("reel", "External id retrieved: " + extid);
                        QuestionsAnswersDatabase rd1 = new QuestionsAnswersDatabase(context);
                        rd1.setSynchronized(localAnswers.get(i).getId(), rec_extid);
                    } else {
                        Log.d("reel", "No external id, something went wrong");
                    }
                }
            }//
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}