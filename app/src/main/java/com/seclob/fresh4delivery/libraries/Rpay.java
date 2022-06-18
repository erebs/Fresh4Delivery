package com.seclob.fresh4delivery.libraries;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.seclob.fresh4delivery.R;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Rpay extends AsyncTask<String, Void, String> {

    JSONObject params;
    Context context;
    private TaskCompleteListener mTaskCompleteListener;


    public Rpay(JSONObject params, Context context, TaskCompleteListener listener) {
        this.params = params;
        this.context = context;
        mTaskCompleteListener = listener;
    }


    @Override
    protected String doInBackground(String... urls) {
        final MediaType JSON
                = MediaType.get("application/json; charset=utf-8");
        String url = "https://api.razorpay.com/v1/orders";
        Log.i("Rpay - Params", String.valueOf(params));
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, String.valueOf(params));
        Request request = new Request.Builder()
                .url(url)
                .header("dfd","dasdsa")
                .header("content-type","application/json")
                .post(body)
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

}
