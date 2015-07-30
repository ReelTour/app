package gaftech.reeltour;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import gaftech.reeltour.models.Room;
import gaftech.reeltour.models.RoomsDatabase;
import gaftech.reeltour.models.ToursDatabase;

/**
 * Created by r.suleymanov on 28.06.2015.
 * email: ruslancer@gmail.com
 */

public class SelectRoomActivity extends Activity implements View.OnClickListener {
    private static String videoUri = "";
    private Dialog dialog = null;
    private boolean editing = false;
    private ArrayList<Room> rooms;
    private int current = 0;
    private RoomsDatabase rooms_database;
    static final int REQUEST_VIDEO_CAPTURE = 2;
    static final int PICK_VIDEO = 4;

    Button prev;
    Button delRoom;
    String mCurrentVideoPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_room);
        Button endBtn   = (Button) findViewById(R.id.end_room_btn);
        Button nextBtn  = (Button) findViewById(R.id.next_room_btn);
        Button tp       = (Button) findViewById(R.id.record_review_btn);
        this.prev     = (Button) findViewById(R.id.room_video_preview_btn);
        Button pv       = (Button) findViewById(R.id.loadRoomFromVideosBtn);
        Button p = (Button) findViewById(R.id.prev_room_btn);
        delRoom = (Button)findViewById(R.id.delete_room_btn);
        delRoom.setEnabled(false);
        endBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        tp.setOnClickListener(this);
        this.prev.setOnClickListener(this);
        pv.setOnClickListener(this);
        p.setOnClickListener(this);
        delRoom.setOnClickListener(this);
        rooms_database = new RoomsDatabase(this);
        if ( getIntent().getBooleanExtra("edit", false) ) {
            editing  = true;
            rooms = rooms_database.getRooms(ToursDatabase.DEFAULT_TOUR_ID);
            if (rooms != null) {
                Log.d("loading rooms", rooms.toString());
                fillCurrent(current);
            } else {
                rooms = new ArrayList<Room>();
            }
        } else {
            editing = false;
            rooms = new ArrayList<Room>();
        }
        findViewById(R.id.prev_room_btn).setEnabled((rooms.size()>0 && current>0));
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        dialog = new Dialog(SelectRoomActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.review_dialog);
    }
    @Override
    protected void onPause() {
        if (dialog != null) { dialog.dismiss(); }
        super.onPause();
    }
    public void takeVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }
    private File createVideoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "MPG_" + timeStamp + "_";
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(mediaStorageDir,"/reel-tour/rooms/videos/");
        if (!storageDir.exists()) storageDir.mkdirs();
        File video = File.createTempFile(videoFileName, ".mp4", storageDir);
        mCurrentVideoPath = video.getAbsolutePath();
        return video;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_VIDEO && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, R.string.video_capture_error, Toast.LENGTH_LONG).show();
                return;
            }
            try {
                Uri selected = data.getData();
                InputStream inputStream = getBaseContext().getContentResolver().openInputStream(selected);
                File videoFile = createVideoFile();
                this.prev.setEnabled(false);
                copyInputStreamToFile(inputStream, videoFile, this.prev);
                this.videoUri = videoFile.getAbsolutePath();
            } catch (Exception e) {
                Toast.makeText(this, "Can't create file", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    Uri selected = data.getData();
                    InputStream inputStream = getBaseContext().getContentResolver().openInputStream(selected);
                    File videoFile = createVideoFile();
                    this.prev.setEnabled(false);
                    copyInputStreamToFile(inputStream, videoFile, this.prev);
                    this.videoUri = videoFile.getAbsolutePath();
                } catch (Exception e) {
                    Toast.makeText(this, "Can't create file", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, R.string.video_capture_error, Toast.LENGTH_LONG).show();
            }
        }
    }
    private void copyInputStreamToFile(final InputStream in,final File file, final Button btn ) {
        btn.setEnabled(false);
        AsyncTask a = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    OutputStream out = new FileOutputStream(file);
                    byte[] buf = new byte[1024];
                    int len;
                    while((len=in.read(buf))>0){
                        out.write(buf,0,len);
                    }
                    out.close();
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            protected void onPostExecute(Object o) {
                Log.d("reel", "onpostexecute");
                super.onPostExecute(o);
                if (btn != null) btn.setEnabled(true);
            }
        };
        a.execute();
    }
    public int saveRoom () {
        EditText nameField = (EditText)findViewById(R.id.roomText);
        String name = (nameField != null)?nameField.getText().toString():"";
        if (this.videoUri.length()>0 && name.length()>0) {
            long id = 0;
            Log.d("rooms list", rooms.toString());
            if (current<rooms.size()) {
                Room rm = rooms.get(current);
                if (rm != null) {
                    id = rm.getId();
                }
            }
            Room r = new Room(id, ToursDatabase.DEFAULT_TOUR_ID, name, this.videoUri, null);
            RoomsDatabase rd = new RoomsDatabase(this);
            long res = rd.addRoom(r);
            if (res != -1) {
                if (r.getId()==0) {
                    r.setId(res);
                    if (current<rooms.size()) {
                        rooms.set(current, r);
                        Log.d("rooms array list set", String.valueOf(current));
                    } else {
                        rooms.add(r);
                        Log.d("rooms array list add", String.valueOf(r.getId()));
                    }
                }
                Toast.makeText(this, R.string.room_successfully_created, Toast.LENGTH_LONG).show();
                return 0;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    private void pickVideo() {
        //Use MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, PICK_VIDEO);
        } else {
            Toast.makeText(this, R.string.video_capture_error, Toast.LENGTH_LONG).show();
        }
    }
    private void fillCurrent(int current) {
        delRoom.setEnabled(false);
        if (current<rooms.size()) {
            Room cr = rooms.get(current);
            if (cr != null) {
                ((EditText) findViewById(R.id.roomText)).setText(cr.getName());
                this.videoUri = cr.getVideoUri();
                delRoom.setEnabled(true);

                Log.d("current room", this.videoUri);
            }
        } else {
            ((EditText) findViewById(R.id.roomText)).setText("");
            this.videoUri = "";
        }
    }
    @Override
    public void onClick(View v) {
        EditText nameField = (EditText)findViewById(R.id.roomText);
        switch (v.getId()) {

            case R.id.loadRoomFromVideosBtn:
                this.pickVideo();
                break;
            case R.id.room_video_preview_btn:
                String path = this.videoUri;
                if (path.length() > 0) {
                    VideoView vw = (VideoView) dialog.findViewById(R.id.reviewVideoView);
                    if (vw != null) {
                        vw.setVideoPath(path);
                        MediaController mc = new MediaController(this);
                        mc.setAnchorView(vw);
                        mc.setMediaPlayer(vw);
                        vw.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.setVolume(100, 100);
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
                        dialog.show();
                    }
                } else {
                    Toast.makeText(this, R.string.no_room_video_to_play, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.record_review_btn:
                this.takeVideo();
                break;
            case R.id.delete_room_btn:
                if (current<rooms.size()) {
                    Room r = rooms.get(current);
                    if (r != null && r.getId()>0) {
                        rooms_database.deleteRoom(r.getId());
                        rooms = rooms_database.getRooms(ToursDatabase.DEFAULT_TOUR_ID);
                        if (current > 0) current--;
                        fillCurrent(current);
                    }
                }
                break;
            case R.id.prev_room_btn:
                if (current>0) {
                    current--;
                    Log.d("set current", String.valueOf(current));
                    fillCurrent(current);
                    if (current==0) {
                        findViewById(R.id.prev_room_btn).setEnabled(false);
                    }
                } else {
                    findViewById(R.id.prev_room_btn).setEnabled(false);
                }
                break;
            case R.id.next_room_btn:
                //save this room
                if (this.saveRoom() == 0) {
                    nameField.setText("");
                    videoUri = "";
                    current++;
                    rooms = rooms_database.getRooms(ToursDatabase.DEFAULT_TOUR_ID);
                    fillCurrent(current);
                    findViewById(R.id.prev_room_btn).setEnabled(true);
                }
                break;
            case R.id.end_room_btn:
                String name = nameField.getText().toString();
                if (name.length()>0) {
                    if (this.saveRoom() == 0) {
                        Intent intent = new Intent(this, WaitingScreenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else{
                        Toast.makeText(this, R.string.error_saving_room, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Intent intent = new Intent(this, WaitingScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }
}
