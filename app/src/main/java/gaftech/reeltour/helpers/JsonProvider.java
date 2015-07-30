package gaftech.reeltour.helpers;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.provider.Settings;
import android.util.Log;
import android.webkit.MimeTypeMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.List;

/**
 * Created by r.suleymanov on 23.06.2015.
 * email: ruslancer@gmail.com
 */

public class JsonProvider {
    protected Context _context;
    protected String ANDROID_ID;
    private static HttpClient client;
    public JsonProvider(Context context) {
        this._context = context;
        this.ANDROID_ID = Settings.Secure.getString(this._context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
    public JSONObject getJSON(String address, List<NameValuePair> params, List<NameValuePair> files) throws Exception{
        client = AndroidHttpClient.newInstance("Awesome User Agent V/1.0");
        StringBuilder builder = new StringBuilder();
        //HttpConnectionParams.setConnectionTimeout(client.getParams(), 300000);
        //HttpConnectionParams.setSoTimeout(client.getParams(), 300000);
        HttpPost httppost = new HttpPost(address);
        if (files != null && files.size()>0) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            for (int i=0; i<params.size(); i++) {
                String name = params.get(i).getName();
                String val = params.get(i).getValue();
                Log.d("add text body ", name+"="+val);
                entityBuilder.addTextBody(name, val);
            }
            for (int i=0; i<files.size(); i++) {
                String name = files.get(i).getName();
                ContentType mime = ContentType.create(getMimeType(files.get(i).getValue()));
                String path = files.get(i).getValue();
                Log.d("reel", "opening file "+path);
                File f = new File(path);
                if (f.exists()) {
                    Log.d("reel", "attached file "+name);
                    entityBuilder.addPart(name, new FileBody(f));
                } else {
                    Log.d("reel", "file skipped - cannot open it");
                }
            }
            HttpEntity entity = entityBuilder.build();
            httppost.setEntity(entity);
        } else {
            httppost.setEntity(new UrlEncodedFormEntity(params));
        }
        HttpResponse response = client.execute(httppost);
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if(statusCode == 200) {
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
        }
        Thread.sleep(2);
        if (builder.toString().length()>0) {
            Log.d("reel", "response received: " + builder.toString());
            JSONObject json = new JSONObject(builder.toString());
            return json;
        } else
            return null;
    }
    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
