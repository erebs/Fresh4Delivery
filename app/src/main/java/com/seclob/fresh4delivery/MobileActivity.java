package com.seclob.fresh4delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;
import com.seclob.fresh4delivery.libraries.JRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MobileActivity extends AppCompatActivity {

    EditText Mobile;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile);
        getSupportActionBar().hide();
        Mobile = findViewById(R.id.mobile_mobile);
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);

    }

    public void ContinueBtn(View v)
    {
        if (Mobile.length()==10)
            NumberCheckApi();
                    else
            showMsg("Please enter your 10 digit mobile number!");

    }

    public void EmailBtn(View v)
    {
        Intent i = new Intent(this,LoginActivity.class);
        i.putExtra("phone","");
        startActivity(i);
    }

    public void NumberCheckApi()
    {
        LinearLayout Loader = findViewById(R.id.progress_horizontal);
        Loader.setVisibility(View.VISIBLE);
        Mobile.setEnabled(false);
        JRequest jRequest;

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("number", Mobile.getText().toString());

            jRequest = new JRequest(RequestJson, "customer/check/number", true, this, new JRequest.TaskCompleteListener(){
                @Override
                public void onTaskComplete(String result) throws JSONException
                {
                    Loader.setVisibility(View.GONE);
                    Mobile.setEnabled(true);


                    try
                    {
                        JSONObject Res = new JSONObject(result);
                        String sts = Res.getString("sts");
                        String msg = Res.getString("msg");

                        if (sts.equalsIgnoreCase("01"))
                        {

                            if (msg.equalsIgnoreCase("Number Already Exists."))
                            {
                                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                                i.putExtra("phone",Mobile.getText().toString());
                                startActivity(i);
                            }else
                            {
//                                String userObj = Res.getString("user");
//                                JSONObject UserObj = new JSONObject(userObj);
//
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                FirebaseMessaging.getInstance().subscribeToTopic("db"+UserObj.getString("id"));
//                                editor.putString("id", UserObj.getString("id"));
//                                editor.putString("email", UserObj.getString("email"));
//                                OneSignal.sendTag("userid", "db"+UserObj.getString("id"));
//                                OneSignal.setExternalUserId(UserObj.getString("id"));
//                                editor.putString("name", UserObj.getString("name"));
//                                editor.putString("phone", Mobile.getText().toString());
//                                editor.apply();


                            }

                        }

                        else
                        {
                            Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
                            i.putExtra("phone",Mobile.getText().toString());
                            startActivity(i);
                       }


                    }
                    catch (Exception e)
                    { Log.e("catcherror", e + "d"); }
                }
            });
            jRequest.execute();
        }
        catch (JSONException e)
        { e.printStackTrace(); }
    }

    public void showMsg(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}