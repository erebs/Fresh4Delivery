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

import com.seclob.fresh4delivery.adaptor.HisAdapter;
import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.model.HisModel;
import com.seclob.fresh4delivery.model.UnitModel;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HisActivity extends AppCompatActivity {

    List<HisModel> hisModels = new ArrayList<>();
    HisAdapter hisAdapter;
    RecyclerView UnitRecycleview;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);

        UnitRecycleview = findViewById(R.id.HisRecycleView);
        this.hisAdapter = new HisAdapter(this);
        UnitRecycleview.setAdapter(hisAdapter);
        UnitRecycleview.setNestedScrollingEnabled(false);
        GridLayoutManager mGrid2 = new GridLayoutManager(getApplicationContext(), 1);
        UnitRecycleview.setLayoutManager(mGrid2);
        getHis();
    }

    public void getHis()
    {

        LinearLayout Loader = findViewById(R.id.loader_view);
        Loader.setVisibility(View.VISIBLE);
        JRequest jRequest;

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("user_id", sharedPreferences.getString("id", ""));

            jRequest = new JRequest(RequestJson, "2.0/orders", true, this, new JRequest.TaskCompleteListener(){
                @Override
                public void onTaskComplete(String result) throws JSONException
                {

                    try
                    {
                        JSONObject Res = new JSONObject(result);
                        String sts = Res.getString("sts");
                        String msg = Res.getString("msg");

                        if (sts.equalsIgnoreCase("01"))
                        {
                            Loader.setVisibility(View.GONE);

                            String cartObj = Res.getString("orders");
                            JSONArray UnitArray = new JSONArray(cartObj);
                            hisModels.clear();
                            for (int i = 0; i < UnitArray.length(); i++)
                            {
                                String Unit = UnitArray.getString(i);
                                JSONObject UnitObject = new JSONObject(Unit);
                                HisModel hisModel = new HisModel();
                                hisModel.setAmt("₹"+UnitObject.getString("amount"));
                                hisModel.setDcharge("₹"+UnitObject.getString("delivery_charge"));
                                hisModel.setTax("₹"+UnitObject.getString("tax"));
                                hisModel.setPayType(UnitObject.getString("pay_type"));
                                hisModel.setTime(UnitObject.getString("order_on"));
                                hisModel.setID(UnitObject.getString("id"));
                                hisModel.setSts(UnitObject.getString("status"));
                                hisModel.setOrdersArray(UnitObject.getString("order_items"));
                                String shop = UnitObject.getString("shop");
                                JSONObject shopObj = new JSONObject(shop);
                                hisModel.setSname(shopObj.getString("name"));
                                hisModel.setSImage(shopObj.getString("logo"));
                                hisModel.setDtime(shopObj.getString("delivery_time"));
                                hisModel.setAds(shopObj.getString("city")+", "+shopObj.getString("district_name"));

                                hisModels.add(hisModel);
                            }
                            hisAdapter.renewItems(hisModels);

                            //Toast.makeText(CartActivity.this, CCGTotal.getText().toString(), Toast.LENGTH_SHORT).show();
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

    //BottomNav
    public void goHome(View view){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
        finish();
    }
    public void goCat(View view){
        Intent i = new Intent(this,CatActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
        finish();
    }
    public void goNot(View view){
        Intent i = new Intent(this,NotActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
        finish();
    }
    public void goHis(View view){
//        Intent i = new Intent(this,HisActivity.class);
//        startActivity(i);
    }

}