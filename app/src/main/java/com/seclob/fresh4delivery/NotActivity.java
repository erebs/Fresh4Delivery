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

import com.seclob.fresh4delivery.adaptor.MainCategoryAdaptor;
import com.seclob.fresh4delivery.adaptor.NotAdaptor;
import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.model.MainCategoryModel;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    List<MainCategoryModel> mainCategoryModels = new ArrayList<>();
    private RecyclerView MainCatRecycleView;
    private NotAdaptor mainCategoryAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);

        MainCatRecycleView = findViewById(R.id.NotRecycleView);
        this.mainCategoryAdaptor = new NotAdaptor(this);
        MainCatRecycleView.setAdapter(mainCategoryAdaptor);
        GridLayoutManager mGridFP = new GridLayoutManager(getApplicationContext(), 1);
        MainCatRecycleView.setLayoutManager(mGridFP);
        getNot();
        try {
            getCacheNot();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getCacheNot() throws JSONException {
        String userObj = sharedPreferences.getString("notifications","");

        JSONArray categoriesObj = new JSONArray(userObj);
        mainCategoryModels.clear();
        for (int i = 0; i < categoriesObj.length(); i++)
        {
            String categoriesArray = categoriesObj.getString(i);
            JSONObject categoriesObject = new JSONObject(categoriesArray);
            MainCategoryModel mainCategoryModel = new MainCategoryModel();
            mainCategoryModel.setName(categoriesObject.getString("title"));
            mainCategoryModel.setType(categoriesObject.getString("sub_title"));
            mainCategoryModels.add(mainCategoryModel);
        }
        mainCategoryAdaptor.renewItems(mainCategoryModels);
    }

    public void getNot()
    {
//        LinearLayout Loader = findViewById(R.id.progress_horizontal);
//        Loader.setVisibility(View.VISIBLE);
        JRequest jRequest;

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("user_id", sharedPreferences.getString("id", ""));

            jRequest = new JRequest(RequestJson, "customer/notifications", true, this, new JRequest.TaskCompleteListener(){
                @Override
                public void onTaskComplete(String result) throws JSONException
                {
                  //  Loader.setVisibility(View.GONE);

                    try
                    {
                        JSONObject Res = new JSONObject(result);
                        String sts = Res.getString("sts");
                        String msg = Res.getString("msg");

                        if (sts.equalsIgnoreCase("01"))
                        {

                            String userObj = Res.getString("notifications");

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("notifications", userObj);
                            editor.apply();

                            JSONArray categoriesObj = new JSONArray(userObj);
                            mainCategoryModels.clear();
                            for (int i = 0; i < categoriesObj.length(); i++)
                            {
                                String categoriesArray = categoriesObj.getString(i);
                                JSONObject categoriesObject = new JSONObject(categoriesArray);
                                MainCategoryModel mainCategoryModel = new MainCategoryModel();
                                mainCategoryModel.setName(categoriesObject.getString("title"));
                                mainCategoryModel.setType(categoriesObject.getString("sub_title"));
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
        Intent i = new Intent(this,CatActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
        finish();
    }
    public void goNot(View view){
//        Intent i = new Intent(this,NotActivity.class);
//        startActivity(i);
    }
    public void goHis(View view){
        Intent i = new Intent(this,HisActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
        finish();
    }

}