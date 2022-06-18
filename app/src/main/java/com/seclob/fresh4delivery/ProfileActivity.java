package com.seclob.fresh4delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextView Dname,Email,Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);

        Dname = findViewById(R.id.dname_pro);
        Dname.setText(sharedPreferences.getString("name", ""));
        Email = findViewById(R.id.email_pro);
        Email.setText(sharedPreferences.getString("email", ""));
        Phone = findViewById(R.id.phone_pro);
        Phone.setText(sharedPreferences.getString("phone", ""));

    }

    public void TermsBtn(View view) {
        Intent i = new Intent(getApplicationContext(), WebActivity.class);
        i.putExtra("url",getString(R.string.site_url)+"termsandconditions");
        i.putExtra("title","Terms & Conditions");
        startActivity(i);
    }

    public void AboutUS(View view) {
        Intent i = new Intent(getApplicationContext(), WebActivity.class);
        i.putExtra("url",getString(R.string.site_url)+"aboutus");
        i.putExtra("title","About us");
        startActivity(i);
    }

    public void PrivacyBtn(View view) {
        Intent i = new Intent(getApplicationContext(), WebActivity.class);
        i.putExtra("url",getString(R.string.site_url)+"privacy");
        i.putExtra("title","Privacy Policy");
        startActivity(i);
    }

    public void SupportBtn(View view) {
        Intent i = new Intent(getApplicationContext(), WebActivity.class);
        i.putExtra("url",getString(R.string.site_url)+"support");
        i.putExtra("title","Support");
        startActivity(i);
    }

    public void CntBtn(View view) {
        Intent i = new Intent(getApplicationContext(), WebActivity.class);
        i.putExtra("url",getString(R.string.site_url)+"contactus");
        i.putExtra("title","Contact Us");
        startActivity(i);
    }

    public void Dads(View view) {
        Intent i = new Intent(getApplicationContext(), DadsActivity.class);
        startActivity(i);
    }

    public void GoBackBtn(View view)
    {
        super.onBackPressed();
    }


    public void Logout(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", "");
        editor.putString("phone", "");
        editor.putString("name", "");
        editor.putString("email", "");
        editor.putString("pincode", "");
        editor.putString("afterOtp", "");
        editor.apply();
        Intent i = new Intent(getApplicationContext(), SplashActivity.class);
        i.putExtra("mobile","");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void RefundBtn(View view) {
        Intent i = new Intent(getApplicationContext(), WebActivity.class);
        i.putExtra("url",getString(R.string.site_url)+"refund");
        i.putExtra("title","Refund Policy");
        startActivity(i);
    }

}