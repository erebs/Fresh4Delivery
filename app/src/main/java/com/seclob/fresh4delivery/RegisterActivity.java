package com.seclob.fresh4delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.aviran.cookiebar2.CookieBar;

public class RegisterActivity extends AppCompatActivity {

    EditText Name,Mobile,Email;
    String phone;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);

        Name = findViewById(R.id.reg_name);
        Mobile = findViewById(R.id.reg_mobile);
        Email = findViewById(R.id.reg_email);

        try {
            Intent intent = getIntent();
            phone = intent.getStringExtra("phone");
        } catch (Exception e) {
            Log.e("getIntent Error", e + "");
        }

        if(phone.length()==10)
            Mobile.setText(phone);

    }

    public void GoLogin(View v)
    {
        Intent i = new Intent(getApplicationContext(),LoginActivity.class);
        if(Mobile.length()==10)
            i.putExtra("phone",Mobile.getText().toString());
        startActivity(i);
    }

    public void ContinueBtn(View v)
    {
        if(Name.length()>2 && Mobile.length()==10 && Email.length()>2)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("temail", Email.getText().toString());
            editor.putString("tname", Name.getText().toString());
            editor.putString("tphone", Mobile.getText().toString());
            editor.apply();

            Intent i = new Intent(this,PasswordActivity.class);
            i.putExtra("phone",Mobile.getText().toString());
            i.putExtra("from","Register");
            startActivity(i);
        }
        else
            showMsg("Some required fields are empty!");
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