package com.seclob.fresh4delivery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.essam.simpleplacepicker.MapActivity;
import com.essam.simpleplacepicker.utils.SimplePlacePicker;
import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.libraries.JRequest2;
import com.seclob.fresh4delivery.model.AddressModel;
import com.seclob.fresh4delivery.model.UnitModel;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddressActivity extends AppCompatActivity {

    String choose="",adsid="";
    EditText Add, LM, Pin, City, Dist, State, Cnt ,Name;
    TextView geoCity, geoAddress;
    String La="",Lo="";
    TextView Htext,Wtext,Otext;
    ImageView Hicon,Wicon,Oicon;
    LinearLayout Hbox,Wbox,Obox;
    String type="";
    ScrollView scrollView3;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);


        Name = findViewById(R.id.name_ads);
        Add = findViewById(R.id.address_ads);
        LM = findViewById(R.id.landmark_ads);
        Pin = findViewById(R.id.pincode_ads);
        City = findViewById(R.id.city_ads);
        Dist = findViewById(R.id.dist_ads);
        State = findViewById(R.id.state_ads);
        Cnt = findViewById(R.id.phone_ads);
        geoCity = findViewById(R.id.adtype_ads);
        geoAddress = findViewById(R.id.geo_ads);
        scrollView3 = findViewById(R.id.scrollView3);
        Hbox = findViewById(R.id.home_box);
        Hicon = findViewById(R.id.home_icon);
        Htext = findViewById(R.id.home_text);
        Wbox = findViewById(R.id.work_box);
        Wicon = findViewById(R.id.work_icon);
        Wtext = findViewById(R.id.work_text);
        Obox = findViewById(R.id.others_box);
        Oicon = findViewById(R.id.others_icon);
        Otext = findViewById(R.id.others_text);
        Cnt.setText(sharedPreferences.getString("phone", ""));

        try {
            Intent intent = getIntent();
            choose = intent.getStringExtra("choose");
            adsid = intent.getStringExtra("adsid");
        } catch (Exception e) {
            Log.e("getIntent Error", e + "");
        }
        if(choose.equalsIgnoreCase("yes"))
        {
            LocPic();
        }
    }

    public void LocPic()
    {
        Intent intent = new Intent(this, MapActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString(SimplePlacePicker.API_KEY,"AIzaSyBQYLf1CVm_F9YigeqmA8w63gjKboXMeh0");
        bundle.putString(SimplePlacePicker.COUNTRY,"India");
        bundle.putString(SimplePlacePicker.LANGUAGE,"en");
        intent.putExtras(bundle);
        startActivityForResult(intent, SimplePlacePicker.SELECT_LOCATION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        try {

            Log.e("Lan",data.getExtras().getString(SimplePlacePicker.SELECTED_ADDRESS));
            double lon = Double.parseDouble(data.getExtras().getString("Lon"));
            double lan  = Double.parseDouble(data.getExtras().getString("Lan"));

            La = lan+"";
            Lo = lon+"";

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            addresses = geocoder.getFromLocation(lan, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            Pin.setText(postalCode);
            City.setText(city);
            State.setText(state);
            geoAddress.setText(address);
            geoCity.setText(city);

            //Toast.makeText(this, postalCode, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
        getDist(Pin.getText().toString());
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void getDist(String pincode)
    {

        //showMsg("Start");
        JRequest2 jRequest2;

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("user_id", "");

            jRequest2 = new JRequest2(RequestJson, pincode, true, this, new JRequest2.TaskCompleteListener(){
                @Override
                public void onTaskComplete(String result) throws JSONException
                {

                    try
                    {
                        JSONArray Result = new JSONArray(result);
                        String Res = Result.getString(0);
                        JSONObject res = new JSONObject(Res);
                        String sts = res.getString("Status");

                        if (sts.equalsIgnoreCase("Success")) {

                            String PostOffices = res.getString("PostOffice");
                            JSONArray PostOffice = new JSONArray(PostOffices);
                            String postOffice = PostOffice.getString(0);
                            JSONObject UnitObject = new JSONObject(postOffice);
                            Dist.setText(UnitObject.getString("District"));
                        }
                    }
                    catch (Exception e)
                    { Log.e("catcherror", e + "d"); }
                }
            });
            jRequest2.execute();
        }
        catch (JSONException e)
        { e.printStackTrace(); }
    }

    public void showMsg(String msg)
    {
        CookieBar.build(this)
                .setMessage(msg)
                .setBackgroundColor(android.R.color.holo_red_dark)
                .setCookiePosition(CookieBar.TOP)
                .show();
    }
    public void GoBack(View view)
    {
        super.onBackPressed();
    }

    public void HomeBtn(View view)
    {TypeBtn("Home");}

    public void WorkBtn(View view)
    {TypeBtn("Work");}

    public void OthersBtn(View view)
    {TypeBtn("Others");}

    public void TypeBtn(String Type)
    {
        type = Type;
        Hbox.setBackground(getDrawable(R.drawable.input_dis));
        Wbox.setBackground(getDrawable(R.drawable.input_dis));
        Obox.setBackground(getDrawable(R.drawable.input_dis));
        Hicon.setColorFilter(ContextCompat.getColor(this, R.color.grey_text), android.graphics.PorterDuff.Mode.SRC_IN);
        Wicon.setColorFilter(ContextCompat.getColor(this, R.color.grey_text), android.graphics.PorterDuff.Mode.SRC_IN);
        Oicon.setColorFilter(ContextCompat.getColor(this, R.color.grey_text), android.graphics.PorterDuff.Mode.SRC_IN);
        Htext.setTextColor(getResources().getColor(R.color.grey_text));
        Wtext.setTextColor(getResources().getColor(R.color.grey_text));
        Otext.setTextColor(getResources().getColor(R.color.grey_text));

        if(Type.equalsIgnoreCase("Home"))
        {
            Hbox.setBackground(getDrawable(R.drawable.input_sel));
            Hicon.setColorFilter(ContextCompat.getColor(this, R.color.lite_green), android.graphics.PorterDuff.Mode.SRC_IN);
            Htext.setTextColor(getResources().getColor(R.color.lite_green));
        }
        else if(Type.equalsIgnoreCase("Work"))
        {
            Wbox.setBackground(getDrawable(R.drawable.input_sel));
            Wicon.setColorFilter(ContextCompat.getColor(this, R.color.lite_green), android.graphics.PorterDuff.Mode.SRC_IN);
            Wtext.setTextColor(getResources().getColor(R.color.lite_green));
        }
        else
        {
            Obox.setBackground(getDrawable(R.drawable.input_sel));
            Oicon.setColorFilter(ContextCompat.getColor(this, R.color.lite_green), android.graphics.PorterDuff.Mode.SRC_IN);
            Otext.setTextColor(getResources().getColor(R.color.lite_green));
        }

    }

    public void AddBtn(View view)
    {
        findViewById(R.id.address_type).setVisibility(View.GONE);
        if(Name.length()>2 && Add.length()>2 && type.length()>1)
        {
            AddAddress();
        }
        else
        {
            if(Name.length()<2)
            { showMsg("Please enter your name!");}
            else if(Add.length()<2)
            {showMsg("Please enter your address!");}
            else if(type.length()<1)
            {
                findViewById(R.id.address_type).setVisibility(View.VISIBLE);
                scrollView3.fullScroll(View.FOCUS_DOWN);
            }else
            showMsg("Some required fields are empty!");
        }
    }

    public void AddAddress()
    {
        LinearLayout Loader = findViewById(R.id.progress_horizontal);
        Loader.setVisibility(View.VISIBLE);

        JRequest jRequest;

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("user_id", sharedPreferences.getString("id", ""));
            RequestJson.put("name", Name.getText().toString());
            RequestJson.put("mobile", Cnt.getText().toString());
            RequestJson.put("pincode", Pin.getText().toString());
            RequestJson.put("landmark", LM.getText().toString());
            RequestJson.put("city", City.getText().toString());
            RequestJson.put("address", Add.getText().toString());
            RequestJson.put("geo_address", geoAddress.getText().toString());
            RequestJson.put("address", Add.getText().toString());
            RequestJson.put("district", "1");
            RequestJson.put("state", "1");
            RequestJson.put("type", type);
            RequestJson.put("latitude", La);
            RequestJson.put("longitude", Lo);
            RequestJson.put("phone", sharedPreferences.getString("phone", ""));

            jRequest = new JRequest(RequestJson, "2.0/address/create", true, this, new JRequest.TaskCompleteListener(){
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

                            finish();

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





}