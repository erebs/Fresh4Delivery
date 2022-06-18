package com.seclob.fresh4delivery.libraries;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.seclob.fresh4delivery.R;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SmsRequest extends AsyncTask<String, Void, String> {

    String urlSuffix;
    String Msg,Numbers;
    String SenderID = "FRSDLY";
    Context context;
    private TaskCompleteListener mTaskCompleteListener;


    public SmsRequest(String Msg, String Numbers, Context context, TaskCompleteListener listener) {
        this.Msg = Msg;
        this.Numbers = Numbers;
        this.context = context;
        mTaskCompleteListener = listener;
    }


    @Override
    protected String doInBackground(String... urls) {
        final MediaType JSON
                = MediaType.get("application/json; charset=utf-8");
        try {
            urlSuffix = "&message="+encodeValue(Msg)+
                        "&destination="+Numbers+
                        "&sender="+SenderID;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = context.getString(R.string.sms_url)+urlSuffix;
        Log.i("SMS Data", url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        String result = "";
        try {
            response = client.newCall(request).execute();
            result = response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Catch",e+"");
        }
        return result;
    }

    protected void onPostExecute(String result) {
        Log.i("Response - ", result);
        if (result != null) {
            try {
                mTaskCompleteListener.onTaskComplete(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // define the interface
    public interface TaskCompleteListener {
        void onTaskComplete(String result) throws JSONException;
    }

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

}
