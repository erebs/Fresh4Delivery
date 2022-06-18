package com.seclob.fresh4delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.libraries.SmsRequest;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

public class PasswordActivity extends AppCompatActivity {

    String phone, from, otp="";
    TextView BackText,Headline,BtnText;
    TextView MobileText;
    SmsRequest smsRequest;
    SharedPreferences sharedPreferences;
    EditText OTP, Pass, NPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);

        OTP = findViewById(R.id.pass_otp);
        Pass = findViewById(R.id.pass_passedit);
        NPass = findViewById(R.id.pass_npass);

        BackText = findViewById(R.id.pass_backtext);
        Headline = findViewById(R.id.pass_headline);
        BtnText = findViewById(R.id.pass_btntext);
        MobileText = findViewById(R.id.mobile_pass_view);

        try {
            Intent intent = getIntent();
            phone = intent.getStringExtra("phone");
            from = intent.getStringExtra("from");
        } catch (Exception e) {
            Log.e("getIntent Error", e + "");
        }
        MobileText.setText("+91 "+phone);
        if(phone.length()==10 && from.equalsIgnoreCase("Login"))
        {
            BackText.setText("Reset Password");
            Headline.setText("Reset Password");
            BtnText.setText("Reset");
        }
        else if(phone.length()==10 && from.equalsIgnoreCase("Register"))
        {
            BackText.setText("Set Password");
            Headline.setText("Verify");
            BtnText.setText("Continue");
        }

        try {
            genOtp();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public void NextBtn(View view)
    {

            if (OTP.length()==4 && OTP.getText().toString().equalsIgnoreCase(otp))
            {

                    if (Pass.getText().toString().equalsIgnoreCase(NPass.getText().toString()))
                    {
                        if (from.equalsIgnoreCase("Login"))
                            ChangePassApi();
                        else
                            RegApi();
                    }
                    else
                    {
                        showMsg("Password confirmation doesn't match Password!");
                    }
            }
            else
                showMsg("Invalid OTP!");
    }

    public void genOtp() throws UnsupportedEncodingException {

        LinearLayout Loader = findViewById(R.id.progress_horizontal);
        Loader.setVisibility(View.VISIBLE);
        Random rand = new Random();
        otp = String.format("%04d", rand.nextInt(10000));
        String otpMsg="";
        otpMsg = otp+" is the OTP for your Fresh4Delivery app. Please do not share this with anyone. ";

        smsRequest = new SmsRequest(otpMsg, phone, this, new SmsRequest.TaskCompleteListener(){
            @Override
            public void onTaskComplete(String result) throws JSONException
            {
                Loader.setVisibility(View.GONE);

                if (result.contains("Submitted"))
                {
                    showMsg("OTP was successfully sent");
                }

            }
        });
        smsRequest.execute();

    }

    public void ChangePassApi()
    {
        LinearLayout Loader = findViewById(R.id.progress_horizontal);
        Loader.setVisibility(View.VISIBLE);
        JRequest jRequest;
        OTP.setEnabled(false);
        Pass.setEnabled(false);
        NPass.setEnabled(false);

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("mobile", phone);
            RequestJson.put("password", Pass.getText().toString());

            jRequest = new JRequest(RequestJson, "2.0/password", true, this, new JRequest.TaskCompleteListener(){
                @Override
                public void onTaskComplete(String result) throws JSONException
                {
                    Loader.setVisibility(View.GONE);
                    OTP.setEnabled(true);
                    Pass.setEnabled(true);
                    NPass.setEnabled(true);
                    try
                    {
                        JSONObject Res = new JSONObject(result);
                        String sts = Res.getString("sts");
                        String msg = Res.getString("msg");

                        if (sts.equalsIgnoreCase("01"))
                        {

                            String userObj = Res.getString("user");
                            JSONObject UserObj = new JSONObject(userObj);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("id", UserObj.getString("id"));
                            editor.putString("email", UserObj.getString("email"));
                            editor.putString("name", UserObj.getString("name"));
                            editor.putString("phone", UserObj.getString("mobile"));
                            editor.apply();

                            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                            i.putExtra("phone",phone);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);


                        }

                        else
                        {
                            showMsg(msg);
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


    public void RegApi()
    {
        LinearLayout Loader = findViewById(R.id.progress_horizontal);
        Loader.setVisibility(View.VISIBLE);
        JRequest jRequest;

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("email", sharedPreferences.getString("temail",""));
            RequestJson.put("name", sharedPreferences.getString("tname",""));
            RequestJson.put("mobile", phone);
            RequestJson.put("password", Pass.getText().toString());

            jRequest = new JRequest(RequestJson, "2.0/register", true, this, new JRequest.TaskCompleteListener(){
                @Override
                public void onTaskComplete(String result) throws JSONException
                {
                    Loader.setVisibility(View.GONE);

                    try
                    {
                        JSONObject Res = new JSONObject(result);
                        String sts = Res.getString("sts");
                        String msg = Res.getString("msg");

                        if (sts.equalsIgnoreCase("01"))
                        {

                            String userObj = Res.getString("user");
                            JSONObject UserObj = new JSONObject(userObj);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("id", UserObj.getString("id"));
                            editor.putString("email", UserObj.getString("email"));
                            editor.putString("name", UserObj.getString("name"));
                            editor.putString("phone", UserObj.getString("mobile"));
                            editor.apply();

                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);


                        }

                        else
                        {
                            showMsg(msg);
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
        CookieBar.build(this)
                .setMessage(msg)
                .setBackgroundColor(R.color.lite_green)
                .setCookiePosition(CookieBar.BOTTOM)
                .show();
    }

    public void GoBack(View v)
    {
        super.onBackPressed();
    }
}