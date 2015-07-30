package gaftech.reeltour.helpers;

import android.app.AlarmManager;
import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import gaftech.reeltour.R;
import gaftech.reeltour.models.Recording;
import gaftech.reeltour.models.RecordingDatabase;

/**
 * Created by r.suleymanov on 23.06.2015.
 * email: ruslancer@gmail.com
 */

public class AudioRecorder implements MediaRecorder.OnInfoListener {
    private static String mFileName = null;
    boolean mStartRecording = true;
    private Context _context = null;
    private Button mRecordButton = null;
    private MediaRecorder mRecorder = null;
    private AlarmManager am = null;
    private long tour_id = 0;
    private long feedback_id;
    private RecordingDatabase recordings;

    public void setFeedback(long feedback_id) {this.feedback_id = feedback_id; }
    public boolean getStartRecording() {
        return mStartRecording;
    }
    public void setStartRecording(boolean b){
        mStartRecording = b;
    }

    public AudioRecorder(Context context, long tour_id) {
        this._context = context;
        this.recordings = new RecordingDatabase(context);
        this.tour_id = tour_id;
    }

    public AudioRecorder(Context context, Button btn, long tour_id) {
        this._context = context;
        this.recordings = new RecordingDatabase(context);
        this.mRecordButton = btn;
        this.tour_id = tour_id;
        this.mRecordButton.setText(R.string.create_an_audio_btn_label);

        this.mRecordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Button btn = (Button)v;
                onRecord(getStartRecording());
                setStartRecording(!getStartRecording());
            }
        });
    }
    public void release() {
        if (mRecorder != null) {
            mRecorder.stop();     // stop recording
            mRecorder.reset();    // set state to idle
            mRecorder.release();
            mRecorder = null;
        }
    }
    public void onRecord(boolean start) {
        if (start) {
            startRecording();
            if ( this.mRecordButton != null ) this.mRecordButton.setText(R.string.stop_an_audio_btn_label);
        //    Toast.makeText(_context, R.string.audio_record_started, Toast.LENGTH_LONG).show();
        } else {
            if (mFileName != null && mFileName.length()>0) {
                stopRecording();
                if (this.mRecordButton != null)
                    this.mRecordButton.setText(R.string.create_an_audio_btn_label);
                String mess = _context.getString(R.string.audio_record_stopped);
                mess = mess.replace("#FILE#", mFileName);
            }
        //    Toast.makeText(_context,mess, Toast.LENGTH_LONG).show();
            mFileName = "";
        }
    }
    public  void startRecording() {
        File audiosDirectory = new File("/sdcard/reeltour_audios/");
        // have the object build the directory structure, if needed.
        audiosDirectory.mkdirs();
        mFileName = audiosDirectory.getAbsolutePath();
        //Write code for the folder exist condition
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        mFileName += "/audio" + currentDateandTime + ".3gp";

        Log.d("start recording", mFileName);
        File file = new File(mFileName);
        try {
            file.createNewFile();
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(file.getAbsolutePath());
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setMaxDuration(1800000); //
            mRecorder.setOnInfoListener(this);
            //mRecorder.setAudioEncodingBitRate(16);
            //mRecorder.setAudioSamplingRate(44100);
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("Recorder", "prepare() failed");
            }
            mRecorder.start();
            //
        } catch (IOException e) {
            Toast.makeText(_context, R.string.error_starting_audio_record, Toast.LENGTH_LONG).show();
        }
    }
    public void stopRecording() {
        if (mRecorder == null) return;
        mRecorder.stop();
        mRecorder.reset();    // set state to idle
        mRecorder.release();
        mRecorder = null;
        if (mFileName.length()>0) {
            Log.d("reel", "Set recorging uri"+mFileName);
            Recording recording = new Recording();
            recording.setRecordingUri(mFileName);
            recording.setFeedbackId(this.feedback_id);
            this.recordings.addRecording(recording);
        }
    }

    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            Log.v("VIDEOCAPTURE","Maximum Duration Reached");
            onRecord(false);
        }
    }
}