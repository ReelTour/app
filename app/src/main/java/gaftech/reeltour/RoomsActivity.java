package gaftech.reeltour;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import gaftech.reeltour.helpers.AudioRecorder;
import gaftech.reeltour.helpers.DateHelper;
import gaftech.reeltour.helpers.GHDialog;
import gaftech.reeltour.helpers.QuestionsListAdapter;
import gaftech.reeltour.helpers.SharedSession;
import gaftech.reeltour.models.Feedback;
import gaftech.reeltour.models.FeedbacksDatabase;
import gaftech.reeltour.models.Question;
import gaftech.reeltour.models.QuestionsAnswer;
import gaftech.reeltour.models.QuestionsAnswersDatabase;
import gaftech.reeltour.models.QuestionsDatabase;
import gaftech.reeltour.models.Room;
import gaftech.reeltour.models.RoomsDatabase;
import gaftech.reeltour.models.ToursDatabase;
import gaftech.reeltour.waiter.ControlActivity;

/**
 * Created by r.suleymanov on 28.06.2015.
 * email: ruslancer@gmail.com
 */

public class RoomsActivity extends ControlActivity implements View.OnClickListener, DialogInterface.OnClickListener {
    Dialog dialog;
    GHDialog endtourDialog;
    ArrayList<Room> rooms = null;
    int screen_height;
    int screen_width;
    AudioRecorder ar = null;
    SharedSession mSharedSession;

    QuestionsDatabase questionsDatabase;
    ArrayList<Question> questions;
    ListView questionsList;
    QuestionsListAdapter questionsAdapter;

    AlertDialog alertDialog;
    DateHelper datetime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Remove title bar


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);
        Button endTourBtn = (Button) findViewById(R.id.end_tour_button);
        endTourBtn.setOnClickListener(this);

        datetime = new DateHelper();

        mSharedSession = new SharedSession(this);
        RoomsDatabase rd = new RoomsDatabase(this);
        rooms = rd.getRooms(ToursDatabase.DEFAULT_TOUR_ID);

        //Button audioRec = (Button) findViewById(R.id.create_an_audio_btn);
        ar = new AudioRecorder(this, ToursDatabase.DEFAULT_TOUR_ID);

        if ( mSharedSession.isPro() ) {
            questionsDatabase = new QuestionsDatabase(this);
            questions = questionsDatabase.getList();
            Log.d("reel", "Questions: " + questions.toString());
            AlertDialog.Builder ag = new AlertDialog.Builder(this);
            ag.setTitle(R.string.record_audio_dialog_text);
            ag.setCancelable(true);
            ag.setPositiveButton(R.string.record_audio_dialog_yes, this);
            ag.setNegativeButton(R.string.record_audio_dialog_no, this);
            alertDialog = ag.create();
        }
    }

    protected void drawrooms() {
        LayoutInflater inflater = LayoutInflater.from(this);

        int margin = this.getResources().getDimensionPixelSize(R.dimen.room_item_margin);
        int count = this.getResources().getInteger(R.integer.room_item_count);
        int x = margin;
        int y = margin;
        RelativeLayout rc = (RelativeLayout) findViewById(R.id.roomsContainer);
        rc.removeAllViews();

        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        screen_height = metrics.heightPixels;
        screen_width = metrics.widthPixels;


        if (rooms != null) {
            for (Iterator<Room> it = rooms.iterator(); it.hasNext(); ) {
                Room item = it.next();
                Log.d("rooms", "Item is: " + item);
                LinearLayout v = (LinearLayout) inflater.inflate(R.layout.room_item1, null);
                TextView tw = (TextView) v.findViewWithTag("name");

                int width = 0;
                int height = 0;
                if (getResources().getConfiguration().orientation == 2) {
                    height = (screen_height / 2 - margin * 4);
                    width = (int) (height / 1.131367292225201 );
                } else {
                    width = ((screen_width - (margin * (count + 1))) / count);
                    height = (int) (width * 1.131367292225201);
                }

                ImageView im = (ImageView) v.findViewWithTag("image");
                if (im != null) {

                    Bitmap bmp =  ThumbnailUtils.createVideoThumbnail(item.getVideoUri(), MediaStore.Video.Thumbnails.MINI_KIND);
                    if (bmp != null) {
                        Drawable[] layers = new Drawable[2];
                        layers[0] = new BitmapDrawable(getResources(), bmp);
                        layers[1] = this.getResources().getDrawable(R.drawable.room_just_play);
                        LayerDrawable layerDrawable = new LayerDrawable(layers);
                        im.setImageDrawable(layerDrawable);
                    } else {
                        im.setImageDrawable(this.getResources().getDrawable(R.drawable.room_just_play));
                    }
                    im.getLayoutParams().height = height;
                    im.setMaxHeight(height);
                    im.requestLayout();
                }

               //
                if (tw != null) {
                    tw.setText(item.getName());
                }
                tw.measure(0, 0);

                Resources r = getResources();
                float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
                int twh = (int) (tw.getMeasuredHeight() + px);
                Log.d("measured twh", String.valueOf(twh) );
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height + twh);
                params.leftMargin = x;
                params.topMargin = y;
                Log.d("relative_layout", "width" + rc.getWidth());
                rc.addView(v, params);
                x += width + margin;
                if (x + width > screen_width) {
                    y += height + twh + margin;
                    x = margin;
                }

                v.setTag(item);
                v.setOnClickListener(this);
                //v.setOnTouchListener(this);
            }
        }
    }

            @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.drawrooms();

    }

    @Override
    protected void onResume() {
        super.onResume();
        dialog = new Dialog(RoomsActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.review_dialog);

        if ( mSharedSession.isPro() ) {
            endtourDialog = new GHDialog(RoomsActivity.this);
            endtourDialog.setTitle(R.string.question_dialog_header_text);
            endtourDialog.setContentView(R.layout.questions_dialog);
            questionsList = (ListView) endtourDialog.findViewById(R.id.questionsList);
            questionsAdapter = new QuestionsListAdapter(this, R.layout.questions_item, questions);
            questionsList.setAdapter(questionsAdapter);

            Button qasButton = (Button) endtourDialog.findViewById(R.id.submitDialog);
            qasButton.setOnClickListener(this);
            Button skipButton = (Button) endtourDialog.findViewById(R.id.skipSubmit);
            skipButton.setOnClickListener(this);

        }
        this.drawrooms();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
        }
        if (endtourDialog != null) {
            endtourDialog.dismiss();
        }
        if (ar != null) {
            ar.release();
        }
    }
    @Override
    public void onClick(View v) {
        Room r = (Room) v.getTag();
        if (r != null) {
            VideoView vw = (VideoView) dialog.findViewById(R.id.reviewVideoView);
            if (vw != null) {
                String path = r.getVideoUri();

                vw.setVideoPath(path);
                MediaController mc = new MediaController(this);
                mc.setAnchorView(vw);
                mc.setMediaPlayer(vw);
                vw.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // mp.setVolume(100, 100);
                        mp.setLooping(false);
                    }
                });
                vw.setMediaController(mc);
                vw.start();
                vw.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        dialog.hide();
                    }
                });

                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialog.show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    dialog.getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE );
                    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                }

            } else {
                Log.d("rooms", "no video view");
            }
        } else {
            Log.d("rooms", "Not room");
        }
        Feedback fb = new Feedback();
        String now = datetime.getCurrentDateFormatted();
        long feedback_id;
        FeedbacksDatabase fdb;

        switch (v.getId()) {
            case R.id.end_tour_button:
                Log.d("endtour", mSharedSession.getMembership());
                if ( mSharedSession.isPro() ){
                    endtourDialog.show();
                    //
                    alertDialog.show();
                } else {
                    endTour();
                }
                break;
            case R.id.skipSubmit:
                fb.setTourId(ToursDatabase.DEFAULT_TOUR_ID);
                fb.setAuthorId(mSharedSession.getID());
                fb.setEmail("");
                fb.setDateStart(now);
                fdb = new FeedbacksDatabase(this);
                feedback_id = fdb.add(fb);

                ar.setFeedback(feedback_id);
                ar.onRecord(false);
                mSharedSession.requestSync();
                endTour();
                break;
            case R.id.submitDialog:
                if (endtourDialog != null) {
                    ArrayList<QuestionsAnswer> qaList = new ArrayList<QuestionsAnswer>();
                    qaList.clear();
                    Log.d("reel", "current user " + mSharedSession.getLogin() + " with id " + mSharedSession.getID());
                    HashMap<Integer, QuestionsListAdapter.Answer> items = questionsAdapter.getChecked();
                    if (items.size()<questions.size()) {
                        Toast.makeText(this, "Please, answer all questions", Toast.LENGTH_LONG).show();
                    } else {
                        fb.setTourId(ToursDatabase.DEFAULT_TOUR_ID);
                        fb.setAuthorId(mSharedSession.getID());
                        fb.setEmail("");
                        fb.setDateStart(now);
                        fdb = new FeedbacksDatabase(this);

                        feedback_id = fdb.add(fb);
                        Log.d("reel", "Create new feedback " + feedback_id);
                        for (Map.Entry <Integer, QuestionsListAdapter.Answer> cursor: items.entrySet()) {
                            int index = cursor.getKey();
                            QuestionsListAdapter.Answer a = cursor.getValue();
                            Log.d("reel", "Answer for question " + a.question_id + " is " + a.picked_option_id + " with comment " + a.comment);
                            QuestionsAnswer qa = new QuestionsAnswer();
                            qa.setQuestionId(a.question_id);
                            qa.setFeedbackId(feedback_id);
                            qa.setComment(a.comment);
                            qa.setOptionChoosenId(a.picked_option_id);
                            qaList.add(qa);
                        }
                        Log.d("reel", "thank you");
                        QuestionsAnswersDatabase qadb = new QuestionsAnswersDatabase(this);
                        qadb.addList(qaList);
                        endtourDialog.dismiss();
                        ar.setFeedback(feedback_id);
                        ar.onRecord(false);
                        mSharedSession.requestSync();
                        endTour();
                    }
                }
                break;
        }
    }

    public void endTour() {
        Intent intent = new Intent(this, WaitingScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {

        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            // do something on back.
            if ( mSharedSession.isPro() ){
                endtourDialog.show();

                alertDialog.show();
            } else {
                endTour();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                Log.d("reel", "positive pressed");
                ar.onRecord(true);

            break;
            case DialogInterface.BUTTON_NEGATIVE:
                Log.d("reel", "negative pressed");

            break;
        }
        dialog.cancel();
    }
}
