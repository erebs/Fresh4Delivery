package com.seclob.fresh4delivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.seclob.fresh4delivery.adaptor.MainCategoryAdaptor;
import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.model.MainCategoryModel;
import com.seclob.fresh4delivery.model.ShopModel;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CatActivity extends AppCompatActivity {

    List<MainCategoryModel> mainCategoryModels = new ArrayList<>();
    private RecyclerView MainCatRecycleView;
    private MainCategoryAdaptor mainCategoryAdaptor;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);

        MainCatRecycleView = findViewById(R.id.CatRecycleView);
        this.mainCategoryAdaptor = new MainCategoryAdaptor(this);
        MainCatRecycleView.setAdapter(mainCategoryAdaptor);
        GridLayoutManager mGridFP = new GridLayoutManager(getApplicationContext(), 4);
        MainCatRecycleView.setLayoutManager(mGridFP);

        try {
            LoadCat();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void LoadCat() throws JSONException {
        String categoriesObjs = sharedPreferences.getString("categories","");
        JSONArray categoriesObj = new JSONArray(categoriesObjs);
        mainCategoryModels.clear();
        for (int i = 0; i < categoriesObj.length(); i++)
        {
            String categoriesArray = categoriesObj.getString(i);
            JSONObject categoriesObject = new JSONObject(categoriesArray);
            MainCategoryModel mainCategoryModel = new MainCategoryModel();
            mainCategoryModel.setId(categoriesObject.getString("id"));
            mainCategoryModel.setName(categoriesObject.getString("name"));
            mainCategoryModel.setImage(getString(R.string.app_url)+categoriesObject.getString("image"));
            mainCategoryModel.setType("null");
            mainCategoryModels.add(mainCategoryModel);
        }
        mainCategoryAdaptor.renewItems(mainCategoryModels);
    }

    @Override
    protected void onResume() {
        GetShops();
        super.onResume();
    }

    public void GetShops()
    {
        LinearLayout Loader = findViewById(R.id.loader_view);
        Loader.setVisibility(View.VISIBLE);
        JRequest jRequest;

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("user_id",sharedPreferences.getString("id",""));
            RequestJson.put("pincode",sharedPreferences.getString("pincode",""));
            RequestJson.put("limit","250");

            jRequest = new JRequest(RequestJson, "2.0/categories", true, this, new JRequest.TaskCompleteListener(){
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
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("cartObj", Res.getString("cart"));
                            editor.putString("ccTotal", Res.getString("total_amount"));
                            editor.apply();

                            String restaurantsObjs = Res.getString("categories");
                            JSONArray categoriesObj = new JSONArray(restaurantsObjs);
                            mainCategoryModels.clear();
                            for (int i = 0; i < categoriesObj.length(); i++)
                            {
                                String categoriesArray = categoriesObj.getString(i);
                                JSONObject categoriesObject = new JSONObject(categoriesArray);
                                MainCategoryModel mainCategoryModel = new MainCategoryModel();
                                mainCategoryModel.setId(categoriesObject.getString("id"));
                                mainCategoryModel.setName(categoriesObject.getString("name"));
                                mainCategoryModel.setImage(getString(R.string.app_url)+categoriesObject.getString("image"));
                                mainCategoryModel.setType("null");
                                mainCategoryModels.add(mainCategoryModel);
                            }
                            mainCategoryAdaptor.renewItems(mainCategoryModels);

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

    //BottomNav
    public void goHome(View view){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
        finish();
    }
    public void goCat(View view){
//        Intent i = new Intent(this,CatActivity.class);
//        startActivity(i);
    }
    public void goNot(View view){
        Intent i = new Intent(this,NotActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
        finish();
    }
    public void goHis(View view){
        Intent i = new Intent(this,HisActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
        finish();
    }

}