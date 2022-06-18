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
import android.widget.TextView;

import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.model.ShopModel;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShopsActivity extends AppCompatActivity {

    TextView Sname;
    String type="",cat_id="";
    SharedPreferences sharedPreferences;
    List<ShopModel> shopModels = new ArrayList<>();
    private RecyclerView ShopRecycleView;
    private ShopAdapter shopAdapter;
    List<ShopModel> shopModels2 = new ArrayList<>();
    private RecyclerView ShopRecycleView2;
    private ShopAdapter shopAdapter2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);
        getSupportActionBar().hide();
        Sname = findViewById(R.id.shops_act);
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);


        //Shop - near res
        ShopRecycleView = findViewById(R.id.ShopsViewRecycleView);
        this.shopAdapter = new ShopAdapter(this);
        ShopRecycleView.setAdapter(shopAdapter);
        GridLayoutManager mGridFP = new GridLayoutManager(getApplicationContext(), 1);
        ShopRecycleView.setLayoutManager(mGridFP);
        ShopRecycleView.setNestedScrollingEnabled(false);

        //Shop - near nres
        ShopRecycleView2 = findViewById(R.id.nShopRecycleView2);
        this.shopAdapter2 = new ShopAdapter(this);
        ShopRecycleView2.setAdapter(shopAdapter2);
        GridLayoutManager mGridFP2 = new GridLayoutManager(getApplicationContext(), 1);
        ShopRecycleView2.setLayoutManager(mGridFP2);
        ShopRecycleView2.setNestedScrollingEnabled(false);


        try {
            Intent intent = getIntent();
            type = intent.getStringExtra("type");
            cat_id = intent.getStringExtra("cat_id");
        } catch (Exception e) {
            Log.e("getIntent Error", e + "");
        }
        Sname.setText(type);
    }

    @Override
    protected void onResume() {
        GetShops();
        super.onResume();
    }

    public void GetShops()
    {
        LinearLayout Loader = findViewById(R.id.shops_loader);
        Loader.setVisibility(View.VISIBLE);
        JRequest jRequest;

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("user_id",sharedPreferences.getString("id",""));
            RequestJson.put("pincode",sharedPreferences.getString("pincode",""));
            RequestJson.put("limit","200");

            jRequest = new JRequest(RequestJson, "2.0/"+type.toLowerCase(Locale.ROOT)+"", true, this, new JRequest.TaskCompleteListener(){
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

                            String restaurantsObjs = Res.getString(type.toLowerCase(Locale.ROOT));
                            JSONArray restaurantsObj = new JSONArray(restaurantsObjs);
                            shopModels.clear();
                            for (int i = 0; i < restaurantsObj.length(); i++)
                            {
                                String restaurantsArray = restaurantsObj.getString(i);
                                JSONObject restaurantsObject = new JSONObject(restaurantsArray);
                                ShopModel shopModel = new ShopModel();
                                shopModel.setID(restaurantsObject.getString("id"));
                                shopModel.setName(restaurantsObject.getString("name"));
                                shopModel.setImage(restaurantsObject.getString("logo"));
                                shopModel.setDes(restaurantsObject.getString("desc"));
                                shopModel.setDtime(restaurantsObject.getString("delivery_time"));
                                shopModel.setRating(restaurantsObject.getString("rating"));
                                //shopModel.setOpenTime(restaurantsObject.getString("open_time"));
                                shopModel.setOpenTime("");
                                shopModel.setStatus("Active");
                                String typeKey = (type.equalsIgnoreCase("Restaurants")?"restaurant":"supermarket");
                                shopModel.setType(typeKey);
                                shopModels.add(shopModel);
                            }
                            shopAdapter.renewItems(shopModels);
                            String nrestaurantsObjs = Res.getString("n"+type.toLowerCase(Locale.ROOT));
                            JSONArray nrestaurantsObj = new JSONArray(nrestaurantsObjs);
                            shopModels2.clear();
                            for (int i = 0; i < nrestaurantsObj.length(); i++)
                            {
                                String nrestaurantsArray = nrestaurantsObj.getString(i);
                                JSONObject nrestaurantsObject = new JSONObject(nrestaurantsArray);
                                ShopModel shopModel = new ShopModel();
                                shopModel.setID(nrestaurantsObject.getString("id"));
                                shopModel.setName(nrestaurantsObject.getString("name"));
                                shopModel.setImage(nrestaurantsObject.getString("logo"));
                                shopModel.setDes(nrestaurantsObject.getString("desc"));
                                shopModel.setDtime(nrestaurantsObject.getString("delivery_time"));
                                shopModel.setRating(nrestaurantsObject.getString("rating"));
                                shopModel.setOpenTime(nrestaurantsObject.getString("open_time"));
                                shopModel.setStatus("Not");
                                String typeKey = (type.equalsIgnoreCase("Restaurants")?"restaurant":"supermarket");
                                shopModel.setType(typeKey);
                                shopModels2.add(shopModel);
                            }
                            shopAdapter2.renewItems(shopModels2);

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