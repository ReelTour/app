package gaftech.reeltour.reelapi;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import gaftech.reeltour.helpers.DateHelper;
import gaftech.reeltour.models.Feedback;
import gaftech.reeltour.helpers.JsonProvider;
import gaftech.reeltour.models.QuestionsAnswer;
import gaftech.reeltour.models.Recording;
import gaftech.reeltour.models.Room;
import gaftech.reeltour.models.Tour;

/**
 * Created by r.suleymanov on 28.06.2015.
 * email: ruslancer@gmail.com
 */

public class ReelApiManager extends JsonProvider {
    private static final String API_URL  = "http://myreeltour.com/api.json";
    private static final String SECRET = "*5BUYj=,CW~Du^L}";
    public static final String CODE_FIELD = "code";
    public static final String TOKEN_FIELD = "token";
    public static final String AUTH_ID_FIELD = "id";
    public static final String TOUR_ID_FIELD = "tour_id";
    public static final String ROOM_ID_FIELD = "room_id";
    public static final String ANSWER_ID_FIELD = "answer_id";
    public static final String FEEDBACK_ID_FIELD = "feedback_id";
    public static final String RECORDING_ID_FIELD = "recording_id";
    public static final String MEMBERSHIP_FIELD = "membership";

    DateHelper datetime;

    public ReelApiManager(Context context) {
        super(context);
        datetime = new DateHelper();
    }
    private String getHash(List<NameValuePair> list) {
        Comparator<NameValuePair> comp = new Comparator<NameValuePair>() {        // solution than making method synchronized
            @Override
            public int compare(NameValuePair p1, NameValuePair p2) {
                return p1.getName().compareTo(p2.getName());
            }
        };
        Collections.sort(list, comp);
        String res = "";
        for (int i=0; i<list.size(); i++) {
            String name = list.get(i).getName();
            String value = list.get(i).getValue();
            if (name != "hash") { res += name+"="+value+"&"; }
        }; res += this.SECRET;
        Log.d("reel", "get hash for " + res);
        return JsonProvider.md5(res);
    }

    public ReelApiSession userSignIn(String login, String password, String authTokenType) throws Exception {
        //http://myreeltour.com/api.json?action=signin&login=testaccount&password=warrior1026&device=andriod4.0&hash=28aefc052d0ba5c02882f6aa7c36c3ba
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("action", "signin"));
        nameValuePair.add(new BasicNameValuePair("login", login));
        nameValuePair.add(new BasicNameValuePair("password", password));
        nameValuePair.add(new BasicNameValuePair("device", this.ANDROID_ID));
        nameValuePair.add(new BasicNameValuePair("hash", getHash(nameValuePair)));
        JSONObject json = this.getJSON(API_URL, nameValuePair, null);

        ReelApiSession session = new ReelApiSession();
        if (Integer.valueOf(json.get(this.CODE_FIELD).toString())==200) {
            session.setToken(json.get(this.TOKEN_FIELD).toString());
            session.setMembership(json.getJSONObject(this.MEMBERSHIP_FIELD).get("name").toString());
            session.setId(Integer.valueOf(json.get(this.AUTH_ID_FIELD).toString()) );
        }
        return session;
    }
    public int putRecording(String token, Recording recording) throws Exception{
        long feedback_ext_id = recording.getFeedbackExtId();
        if (recording == null || feedback_ext_id<=0) return -1;
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("token", token));
        nameValuePair.add(new BasicNameValuePair("action", "putrecording"));
        if (recording.getExternalId()>0) {
            nameValuePair.add(new BasicNameValuePair("extid", String.valueOf(recording.getExternalId()) ));
        }
        nameValuePair.add(new BasicNameValuePair("feedback_ext_id", String.valueOf(feedback_ext_id) ));
        nameValuePair.add(new BasicNameValuePair("recording_uri", recording.getRecordingUri()));
        nameValuePair.add(new BasicNameValuePair("date_modified", datetime.dateToStr(recording.getDateModified()) ));
        nameValuePair.add(new BasicNameValuePair("hash", getHash(nameValuePair)));
        List files = new ArrayList<NameValuePair>(1);
        if (recording.getRecordingUri().length()>0) {
            String url =  recording.getRecordingUri();
            files.add(new BasicNameValuePair("recording", url) );
        }
        JSONObject json = this.getJSON(API_URL, nameValuePair, files);
        if (json != null) {
            Log.d("reel", "JSON response on put recording " + json.toString());
            if ( Integer.valueOf(json.get(this.CODE_FIELD).toString())==200 ) {
                String id_str = json.get(this.RECORDING_ID_FIELD).toString();
                return (id_str != null)?Integer.valueOf(id_str):-1;
            }
        }
        return -1;
    }
    public int putRoom(String token, Room room, long tour_ext_id) throws Exception{
        if (room == null || tour_ext_id<=0) return -1;
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("token", token));
        nameValuePair.add(new BasicNameValuePair("action", "putroom"));
        if (room.getExternalId()>0) {
            nameValuePair.add(new BasicNameValuePair("extid", String.valueOf(room.getExternalId()) ));
        }
        nameValuePair.add(new BasicNameValuePair("name", room.getName()));
        nameValuePair.add(new BasicNameValuePair("tour_ext_id", String.valueOf(tour_ext_id) ));
       // nameValuePair.add(new BasicNameValuePair("image_uri", room.getBgImageUri()));
        nameValuePair.add(new BasicNameValuePair("video_uri", room.getVideoUri()));
        nameValuePair.add(new BasicNameValuePair("date_modified", datetime.dateToStr(room.getDateModified()) ));
        nameValuePair.add(new BasicNameValuePair("hash", getHash(nameValuePair)));

        /*
        List files = new ArrayList<NameValuePair>(1);
        if (room.getVideoUri().length()>0) {
            String url =  room.getVideoUri();
            if (room.getVideoModified() != null && room.getDateSynchronized() != null) {
                Date midate = room.getVideoModified();
                if (midate.after(room.getDateSynchronized())) {
                    files.add(new BasicNameValuePair("video", url) );
                }
            } else {
                files.add(new BasicNameValuePair("video", url) );
            }
        }
        */

        JSONObject json = this.getJSON(API_URL, nameValuePair, null);
        if (json != null) {
            Log.d("reel", "JSON response on put room " + json.toString());
            if ( Integer.valueOf(json.get(this.CODE_FIELD).toString())==200 ) {
                String id_str = json.get(this.ROOM_ID_FIELD).toString();
                return (id_str != null)?Integer.valueOf(id_str):-1;
            }
        }
        return -1;
    }
    public int putTour(String token, Tour tour) throws Exception {
        if (tour == null) return -1;
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("token", token));
        nameValuePair.add(new BasicNameValuePair("action", "puttour"));
        if (tour.getExternalId()>0) {
            nameValuePair.add(new BasicNameValuePair("extid", String.valueOf(tour.getExternalId()) ));
        }
        nameValuePair.add(new BasicNameValuePair("name", tour.getAddress()));
        nameValuePair.add(new BasicNameValuePair("address", tour.getAddress()));
        nameValuePair.add(new BasicNameValuePair("background_image_uri", tour.getBackgroundImageUri()));
        nameValuePair.add(new BasicNameValuePair("greetings_video_uri", tour.getGreetingsVideoUri()));
        nameValuePair.add(new BasicNameValuePair("date_modified", datetime.dateToStr(tour.getDateModified()) ));
        nameValuePair.add(new BasicNameValuePair("hash", getHash(nameValuePair)));

        List files = new ArrayList<NameValuePair>(1);
        if (tour.getBackgroundImageUri().length()>0) {
            if (tour.getImageModified() != null && tour.getDateSynchronized() != null) {
                Date midate = tour.getImageModified();
                if (midate.after(tour.getDateSynchronized())) {
                    files.add(new BasicNameValuePair("bg_image", tour.getBackgroundImageUri()));
                }
            } else {
                files.add(new BasicNameValuePair("bg_image", tour.getBackgroundImageUri()));
            }
        }
        /*
        if (tour.getGreetingsVideoUri().length()>0) {
            if (tour.getVideoModified() != null  && tour.getDateSynchronized() != null) {
                Date mvdate = tour.getVideoModified();
                if (mvdate.after(tour.getDateSynchronized())) {
                    files.add(new BasicNameValuePair("video", tour.getGreetingsVideoUri()));
                }
            } else {
                files.add(new BasicNameValuePair("video", tour.getGreetingsVideoUri()));
            }
        }
        */
        JSONObject json = this.getJSON(API_URL, nameValuePair, files);
        if (json != null) {
            Log.d("reel", "JSON response on put room " + json.toString());
            if (Integer.valueOf(json.get(this.CODE_FIELD).toString()) == 200){
                String id_str = json.get(this.TOUR_ID_FIELD).toString();
                return (id_str != null) ? Integer.valueOf(id_str) : -1;
            }
        }
        return -1;
    }

    public int putFeedback(String token, Feedback feedback, long tour_ext_id) throws  Exception {
        if (feedback == null) return -1;
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("token", token));
        nameValuePair.add(new BasicNameValuePair("action", "putfeedback"));
        if (feedback.getExternalId()>0) {
            nameValuePair.add(new BasicNameValuePair("extid", String.valueOf(feedback.getExternalId()) ));
        }
        ArrayList<QuestionsAnswer> qa = feedback.getAnswers();
        Log.d("reel", "feedback includes answers " + qa.size());
        if (qa.size()>0) {
            List<String> json_fields = new ArrayList<String>();
            for (int i=0; i<qa.size(); i++) {
                String str = "{\"questionId\":\""+qa.get(i).getQuestionId()+
                        "\", \"optionId\":\""+qa.get(i).getOptionChoosenId()+
                        "\"";
                if (qa.get(i).getComment() != null) {
                   str += ",\"comment\":\"" + qa.get(i).getComment() + "\"}";
                } else {
                   str +="}";
                }
                json_fields.add( str );
            }
            StringBuilder builder = new StringBuilder();
            builder.append(json_fields.remove(0));
            for( String s : json_fields) {
                builder.append( ", ");
                builder.append( s);
            }
            String json = "["+builder.toString()+"]";
            Log.d("reel", "Add answers as json " + json);


            nameValuePair.add(new BasicNameValuePair("answers_json", new String( Base64.encode(json.getBytes(), Base64.URL_SAFE) ) ));
        }
        nameValuePair.add(new BasicNameValuePair("tour_ext_id", String.valueOf(tour_ext_id) ));
        nameValuePair.add(new BasicNameValuePair("author_id", String.valueOf(feedback.getAuthorId()) ));
        nameValuePair.add(new BasicNameValuePair("date_start", datetime.dateToStr(feedback.getDateStart()) ));
        nameValuePair.add(new BasicNameValuePair("date_created", datetime.dateToStr(feedback.getDateCreated()) ));
        nameValuePair.add(new BasicNameValuePair("date_modified", datetime.dateToStr(feedback.getDateModified()) ));
        nameValuePair.add(new BasicNameValuePair("email", feedback.getEmail()));
        nameValuePair.add(new BasicNameValuePair("hash", getHash(nameValuePair)));
        JSONObject json = this.getJSON(API_URL, nameValuePair, null);
        if (json != null) {
            Log.d("reel", "JSON response on put feedback " + json.toString());
            if (Integer.valueOf(json.get(this.CODE_FIELD).toString()) == 200){
                String id_str = json.get(this.FEEDBACK_ID_FIELD).toString();
                return (id_str != null) ? Integer.valueOf(id_str) : -1;
            }
        }
        return -1;
    }
 /*
    public int putAnswer(String token, QuestionsAnswer answer, long tour_ext_id) throws Exception {
        if (answer == null) return -1;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.ENGLISH);
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("token", token));
        nameValuePair.add(new BasicNameValuePair("action", "putanswer"));
        if (answer.getExternalId()>0) {
            nameValuePair.add(new BasicNameValuePair("extid", String.valueOf(answer.getExternalId()) ));
        }
        nameValuePair.add(new BasicNameValuePair("tour_id", String.valueOf(tour_ext_id) ));
        nameValuePair.add(new BasicNameValuePair("author_id", String.valueOf(answer.getAuthorId()) ));
        nameValuePair.add(new BasicNameValuePair("question_id", String.valueOf(answer.getQuestionId()) ));
        nameValuePair.add(new BasicNameValuePair("chosen_option_id", String.valueOf(answer.getOptionChoosenId()) ));
        nameValuePair.add(new BasicNameValuePair("comment", String.valueOf(answer.getComment()) ));
        nameValuePair.add(new BasicNameValuePair("date_start", format.format(answer.getDateStart()) ));
        nameValuePair.add(new BasicNameValuePair("date_created", format.format(answer.getDateCreated()) ));
        nameValuePair.add(new BasicNameValuePair("date_modified", format.format(answer.getDateModified()) ));
        nameValuePair.add(new BasicNameValuePair("hash", getHash(nameValuePair)));

        JSONObject json = this.getJSON(API_URL, nameValuePair, null);
        if (json != null) {
            Log.d("reel", "JSON response on put answer " + json.toString());
            if (Integer.valueOf(json.get(this.CODE_FIELD).toString()) == 200){
                String id_str = json.get(this.ANSWER_ID_FIELD).toString();
                return (id_str != null) ? Integer.valueOf(id_str) : -1;
            }
        }
        return -1;
    }
*/
}
