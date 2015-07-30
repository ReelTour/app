package gaftech.reeltour;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import gaftech.reeltour.helpers.BitmapResizer;
import gaftech.reeltour.helpers.SharedSession;
import gaftech.reeltour.models.Tour;
import gaftech.reeltour.models.ToursDatabase;

/**
 * Created by r.suleymanov on 28.06.2015.
 * email: ruslancer@gmail.com
 */

public class CreateTourActivity extends Activity implements View.OnClickListener {
    //ImageView bgGreetingsImageView = null;
    private static String imageUri = "";
    private static String videoUri = "";
    //private SessionManagerActivity session = null;
    private Dialog dialog = null;
    private Dialog dialog_im = null;
    ToursDatabase tour_database;
    Tour current_tour;
    EditText addText;
    Boolean editing = false;

    SharedSession mSharedSession;

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;
    static final int PICK_IMAGE = 3;
    static final int PICK_VIDEO = 4;
    String mCurrentPhotoPath = null;
    String mCurrentVideoPath = null;

    Button bgPrevBtn;
    Button greetingsVideoPrevBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);
        //find views
        Button tkBgImage          = (Button) findViewById(R.id.take_bg_pic_btn);
        Button recGreetings       = (Button) findViewById(R.id.record_greetings_btn);
        Button ctSave             = (Button) findViewById(R.id.create_tour_save_btn);
        Button ctClear            = (Button) findViewById(R.id.create_tour_clear_btn);
        this.bgPrevBtn            = (Button) findViewById(R.id.bgImagePreviewBtn);
        this.greetingsVideoPrevBtn= (Button) findViewById(R.id.greetingsVideoPreviewBtn);
        //bgGreetingsImageView    = (ImageView) findViewById(R.id.bgGreetingsImageView);
        Button pickFromGalleryBtn = (Button) findViewById(R.id.pickBgFromGalleryBtn);
        Button loadFromVideosBtn  = (Button) findViewById(R.id.loadFromVideosBtn);
        addText = (EditText)findViewById(R.id.addressText);
        //setup listeners
        tkBgImage.setOnClickListener(this);
        recGreetings.setOnClickListener(this);
        ctSave.setOnClickListener(this);
        ctClear.setOnClickListener(this);
        this.bgPrevBtn.setOnClickListener(this);

        Log.d("reel", this.greetingsVideoPrevBtn.toString());
        this.greetingsVideoPrevBtn.setOnClickListener(this);
        pickFromGalleryBtn.setOnClickListener(this);
        loadFromVideosBtn.setOnClickListener(this);
        //create objects
        // session       = new SessionManagerActivity(this);
        mSharedSession = new SharedSession(this);
        Log.d("Current login is ", mSharedSession.getLogin());
        Log.d("Current membership is ", mSharedSession.getMembership());
        Log.d("Current user id is ", String.valueOf(mSharedSession.getID()) );

        tour_database = new ToursDatabase(this);
        if ( getIntent().getBooleanExtra("edit", false) ) {
            editing       = true;
            current_tour  = tour_database.getCurrentTour();
            imageUri      = current_tour.getBackgroundImageUri();
            videoUri      = current_tour.getGreetingsVideoUri();
            addText.setText(current_tour.getAddress());
            Log.d("reel", imageUri);
        } else {
            editing  = false;
            imageUri = "";
            videoUri = "";
            addText.setText(null);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        dialog = new Dialog(CreateTourActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.review_dialog);
        dialog_im = new Dialog(CreateTourActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog_im.setContentView(R.layout.image_preview_dialog);
    }
    @Override
    protected void onPause() {
        if (dialog_im != null) { dialog_im.dismiss();  }
        if (dialog != null) {  dialog.dismiss(); }
        super.onPause();
    }
    public void takeVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }
    public void takePicture() {
        this.dispatchTakePictureIntent();
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
    private void pickPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        } else {
            Toast.makeText(this, "Couldn't start intent", Toast.LENGTH_LONG).show();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, R.string.photo_capture_error, Toast.LENGTH_LONG).show();
            } // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

        } else {
            Toast.makeText(this, R.string.photo_capture_error, Toast.LENGTH_LONG).show();
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
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(mediaStorageDir,"/reel-tour/pics/");
        if (!storageDir.exists()) storageDir.mkdirs();
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private File createVideoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "MPG_" + timeStamp + "_";
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(mediaStorageDir,"/reel-tour/videos/");
        if (!storageDir.exists()) storageDir.mkdirs();
        File video = File.createTempFile(videoFileName, ".mp4", storageDir);
        mCurrentVideoPath = video.getAbsolutePath();
        return video;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("reel", "currentImagePath " + mCurrentPhotoPath);
        if (requestCode == PICK_VIDEO && resultCode == RESULT_OK) {

            if (data == null) {
                Toast.makeText(this, R.string.video_capture_error, Toast.LENGTH_LONG).show();
                return;
            }
            //Uri selected = data.getData();
            //this.videoUri = selected.toString();
            try {
                Uri selected = data.getData();
                InputStream inputStream = getBaseContext().getContentResolver().openInputStream(selected);
                File videoFile = createVideoFile();
                this.greetingsVideoPrevBtn.setEnabled(false);
                copyInputStreamToFile(inputStream, videoFile, this.greetingsVideoPrevBtn);
                this.videoUri = videoFile.getAbsolutePath();
            } catch (Exception e) {
                Toast.makeText(this, "Can't create file", Toast.LENGTH_LONG).show();
            }

            Log.d("video uri", this.videoUri);
        }
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, R.string.photo_capture_error, Toast.LENGTH_LONG).show();
                return;
            }
            try {
                Uri selected = data.getData();
                InputStream inputStream = getBaseContext().getContentResolver().openInputStream(selected);
                File photoFile = createImageFile();
                this.bgPrevBtn.setEnabled(false);
                copyInputStreamToFile(inputStream, photoFile, this.bgPrevBtn);
                this.imageUri = photoFile.getAbsolutePath();
            } catch (Exception e) {
                Toast.makeText(this, "Can't create file", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (mCurrentPhotoPath != null) {
                this.imageUri = mCurrentPhotoPath; //photoFile.getAbsolutePath();
            }
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
              //  Uri videoUri = data.getData();
              //  this.videoUri = videoUri.toString();
                try {
                    Uri selected = data.getData();
                    InputStream inputStream = getBaseContext().getContentResolver().openInputStream(selected);
                    File videoFile = createVideoFile();
                    this.greetingsVideoPrevBtn.setEnabled(false);
                    copyInputStreamToFile(inputStream, videoFile, this.greetingsVideoPrevBtn);
                    this.videoUri = videoFile.getAbsolutePath();
                } catch (Exception e) {
                    Toast.makeText(this, "Can't create file", Toast.LENGTH_LONG).show();
                }
            } else {
                // no data
                Toast.makeText(this, R.string.video_capture_error, Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onClick(View v) {
        Log.d("reel", String.valueOf(v.getId()));

        switch (v.getId()) {
            case R.id.pickBgFromGalleryBtn:
                this.pickPicture();
                break;
            case R.id.loadFromVideosBtn:
                this.pickVideo();
                break;
            case R.id.bgImagePreviewBtn:
                Log.d("realty",  "preview pressed");

                String im = this.imageUri;

                if (im.length() > 0) {
                    ImageView iv = (ImageView) dialog_im.findViewById(R.id.imagePreviewView);
                    if (iv != null) {
                        BitmapResizer br = new BitmapResizer(this);
                        Bitmap imageBitmap = BitmapFactory.decodeFile(im);
                        iv.setImageBitmap(br.getResizedBitmap(imageBitmap));
                        Button b = (Button) dialog_im.findViewById(R.id.imagePreviewCloseBtn);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog_im.hide();
                            }
                        });
                        dialog_im.show();
                    }

                } else {
                    Toast.makeText(this, R.string.no_background_image_to_preview, Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.greetingsVideoPreviewBtn:
                String path = this.videoUri;
                Log.d("reel", "preview "+path);
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
                    Toast.makeText(this, R.string.no_greetings_video_to_play, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.take_bg_pic_btn:
                this.takePicture();
                break;
            case R.id.record_greetings_btn:
                this.takeVideo();
                break;
            case R.id.create_tour_save_btn:
                String address = addText.getText().toString();
                if (this.imageUri.length() > 0 && this.videoUri.length() > 0 && address.length() > 0) {
                    //User u = session.getCurrentUser();
                    long author_id = mSharedSession.getID(); // u.getId();

                    Tour t = new Tour(ToursDatabase.DEFAULT_TOUR_ID, "", address, author_id, this.imageUri, this.videoUri);
                    ToursDatabase tb = new ToursDatabase(this);
                    if (tb.setCurrentTour(t, !this.editing) == 0) {
                        Intent intent = new Intent(this, SelectRoomActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("edit", editing);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(this, R.string.create_tour_all_fields_required, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.create_tour_clear_btn:
                this.imageUri = "";
                this.videoUri = "";
                addText.setText("");
                break;
        }
    }
}
