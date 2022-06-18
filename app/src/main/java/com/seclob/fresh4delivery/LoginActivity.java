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

import com.seclob.fresh4delivery.libraries.JRequest;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    String phone;
    EditText MorE,Pass;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        MorE = findViewById(R.id.login_more);
        Pass = findViewById(R.id.login_pass);
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);

        try {
            Intent intent = getIntent();
            phone = intent.getStringExtra("phone");
        } catch (Exception e) {
            Log.e("getIntent Error", e + "");
        }

        if(phone.length()==10)
            MorE.setText(phone);

    }

    public void ForgotBtn(View v)
    {
        if(MorE.getText().toString().matches("\\d+(?:\\.\\d+)?") && MorE.length()==10)
        {
            Intent i = new Intent(this,PasswordActivity.class);
            i.putExtra("phone",MorE.getText().toString());
            i.putExtra("from","Login");
            startActivity(i);
        }else
        {
            GetNumberApi();
        }
    }

    public void LoginBtn(View view)
    {
        if (MorE.length()>3 && Pass.length()>2)
        {
            LoginApi();
        }
        else
            showMsg("Some required fields are empty!");
    }

    public void GetNumberApi()
    {
        LinearLayout Loader = findViewById(R.id.progress_horizontal);
        Loader.setVisibility(View.VISIBLE);
        JRequest jRequest;
        MorE.setEnabled(false);
        Pass.setEnabled(false);

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("email", MorE.getText().toString());

            jRequest = new JRequest(RequestJson, "customer/get/number", true, this, new JRequest.TaskCompleteListener(){
                @Override
                public void onTaskComplete(String result) throws JSONException
                {
                    Loader.setVisibility(View.GONE);
                    MorE.setEnabled(true);
                    Pass.setEnabled(true);
                    try
                    {
                        JSONObject Res = new JSONObject(result);
                        String sts = Res.getString("sts");
                        String msg = Res.getString("msg");

                        if (sts.equalsIgnoreCase("01"))
                        {

                                Intent i = new Intent(getApplicationContext(),PasswordActivity.class);
                            i.putExtra("phone",Res.getString("number"));
                            i.putExtra("from","Login");
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


    public void LoginApi()
    {
        LinearLayout Loader = findViewById(R.id.progress_horizontal);
        Loader.setVisibility(View.VISIBLE);
        JRequest jRequest;
        MorE.setEnabled(false);
        Pass.setEnabled(false);

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("emailormobile", MorE.getText().toString());
            RequestJson.put("password", Pass.getText().toString());

            jRequest = new JRequest(RequestJson, "2.0/login", true, this, new JRequest.TaskCompleteListener(){
                @Override
                public void onTaskComplete(String result) throws JSONException
                {
                    Loader.setVisibility(View.GONE);
                    MorE.setEnabled(true);
                    Pass.setEnabled(true);
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
                                editor.putString("user_id", UserObj.getString("id"));
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