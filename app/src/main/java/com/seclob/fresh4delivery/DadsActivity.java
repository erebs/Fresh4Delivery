package com.seclob.fresh4delivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.seclob.fresh4delivery.adaptor.AddressAdapter;
import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.model.AddressModel;
import com.seclob.fresh4delivery.model.UnitModel;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DadsActivity extends AppCompatActivity implements AddressAdapter.getCategory{

    List<AddressModel> addressModels = new ArrayList<>();
    AddressAdapter addressAdapter;
    RecyclerView AdsRecycleview;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dads);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);

        AdsRecycleview = findViewById(R.id.AdsRecycleview);
        this.addressAdapter = new AddressAdapter(this);
        AdsRecycleview.setAdapter(addressAdapter);
        AdsRecycleview.setNestedScrollingEnabled(false);
        GridLayoutManager mGrid2 = new GridLayoutManager(getApplicationContext(), 1);
        AdsRecycleview.setLayoutManager(mGrid2);

    }

    public void NewAdsBtn(View view)
    {
        Intent i = new Intent(this, AddressActivity.class);
        i.putExtra("choose","yes");
        i.putExtra("adsid","");
        startActivity(i);
    }

    public void GoBack(View view)
    {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        getAddress();
        super.onResume();
    }

    public void getAddress()
    {
        LinearLayout Loader = findViewById(R.id.progress_horizontal);

        Loader.setVisibility(View.VISIBLE);

        JRequest jRequest;

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("user_id", sharedPreferences.getString("id", ""));

            jRequest = new JRequest(RequestJson, "2.0/address", true, this, new JRequest.TaskCompleteListener(){
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
                            String cartObj = Res.getString("address");
                            JSONArray UnitArray = new JSONArray(cartObj);
                                addressModels.clear();
                            for (int i = 0; i < UnitArray.length(); i++)
                            {
                                String Unit = UnitArray.getString(i);
                                JSONObject UnitObject = new JSONObject(Unit);
                                AddressModel unitModel = new AddressModel();
                                unitModel.setID(UnitObject.getString("id"));
                                unitModel.setAddress(UnitObject.getString("geo_address"));
                                unitModel.setName(UnitObject.getString("city"));
                                unitModel.setType(UnitObject.getString("type"));
                                unitModel.setIsD(UnitObject.getString("default"));
                                unitModel.setPincode(UnitObject.getString("pincode"));
                                addressModels.add(unitModel);
                            }
                            addressAdapter.renewItems(addressModels);

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
                .setCookiePosition(CookieBar.TOP)
                .show();
    }

    @Override
    public void Cat(String catId, int pos) throws JSONException {
        LinearLayout Loader = findViewById(R.id.progress_horizontal);

        if(catId.equalsIgnoreCase("Start"))
        {Loader.setVisibility(View.VISIBLE);}
        else if(catId.equalsIgnoreCase("Stop"))
        {Loader.setVisibility(View.GONE);
            getAddress();
        }
        else if(catId.equalsIgnoreCase("Finish"))
        {Loader.setVisibility(View.GONE);
            finish();
        }
        else {

        }


    }
}