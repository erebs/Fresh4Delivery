package com.seclob.fresh4delivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seclob.fresh4delivery.adaptor.MainCategoryAdaptor;
import com.seclob.fresh4delivery.adaptor.SearchAdapter;
import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.model.HisModel;
import com.seclob.fresh4delivery.model.MainCategoryModel;
import com.seclob.fresh4delivery.model.SearchModel;
import com.seclob.fresh4delivery.model.ShopModel;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    EditText SearchInput;
    ImageView SearchIcon,CloseIcon;
    SharedPreferences sharedPreferences;
    List<MainCategoryModel> mainCategoryModels = new ArrayList<>();
    private RecyclerView MainCatRecycleView;
    private MainCategoryAdaptor mainCategoryAdaptor;
    TextView SearchText;
    List<ShopModel> shopModels = new ArrayList<>();
    List<ShopModel> shopModels2 = new ArrayList<>();
    private RecyclerView ShopRecycleView;
    private RecyclerView ShopRecycleView2;
    private ShopAdapter shopAdapter;
    private ShopAdapter shopAdapter2;

    private RecyclerView SearchView1;
    private RecyclerView SearchView2;
    private RecyclerView SearchView3;
    private RecyclerView SearchView4;

    private SearchAdapter searchAdapter1,searchAdapter2,searchAdapter3,searchAdapter4;
    List<SearchModel> searchModels1 = new ArrayList<>();
    List<SearchModel> searchModels2 = new ArrayList<>();
    List<SearchModel> searchModels3 = new ArrayList<>();
    List<SearchModel> searchModels4 = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);

        SearchInput = findViewById(R.id.SearchInput);
        SearchIcon = findViewById(R.id.search_icon);
        CloseIcon = findViewById(R.id.clear_icon);
        SearchText = findViewById(R.id.search_msg_text);
        MainCatRecycleView = findViewById(R.id.MainCatRecycleView2);
        this.mainCategoryAdaptor = new MainCategoryAdaptor(this);
        MainCatRecycleView.setAdapter(mainCategoryAdaptor);
        LinearLayoutManager HorizontalLayout = new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.HORIZONTAL, false);
        MainCatRecycleView.setLayoutManager(HorizontalLayout);

        SearchView1 = findViewById(R.id.SearchView1);
        this.searchAdapter1 = new SearchAdapter(this);
        SearchView1.setAdapter(searchAdapter1);
        GridLayoutManager msearch1 = new GridLayoutManager(getApplicationContext(), 1);
        SearchView1.setLayoutManager(msearch1);
        SearchView1.setNestedScrollingEnabled(false);

        SearchView2 = findViewById(R.id.SearchView2);
        this.searchAdapter2 = new SearchAdapter(this);
        SearchView2.setAdapter(searchAdapter2);
        GridLayoutManager msearch2 = new GridLayoutManager(getApplicationContext(), 1);
        SearchView2.setLayoutManager(msearch2);
        SearchView2.setNestedScrollingEnabled(false);

        SearchView3 = findViewById(R.id.SearchView3);
        this.searchAdapter3 = new SearchAdapter(this);
        SearchView3.setAdapter(searchAdapter3);
        GridLayoutManager msearch3 = new GridLayoutManager(getApplicationContext(), 1);
        SearchView3.setLayoutManager(msearch3);
        SearchView3.setNestedScrollingEnabled(false);

        SearchView4 = findViewById(R.id.SearchView4);
        this.searchAdapter4 = new SearchAdapter(this);
        SearchView4.setAdapter(searchAdapter4);
        GridLayoutManager msearch4 = new GridLayoutManager(getApplicationContext(), 1);
        SearchView4.setLayoutManager(msearch4);
        SearchView4.setNestedScrollingEnabled(false);

        //Shop - near res
        ShopRecycleView = findViewById(R.id.ShopRecycleViewS);
        this.shopAdapter = new ShopAdapter(this);
        ShopRecycleView.setAdapter(shopAdapter);
        GridLayoutManager mGridFP = new GridLayoutManager(getApplicationContext(), 1);
        ShopRecycleView.setLayoutManager(mGridFP);
        ShopRecycleView.setNestedScrollingEnabled(false);

        //Shop - near nres
        ShopRecycleView2 = findViewById(R.id.ShopRecycleViewS2);
        this.shopAdapter2 = new ShopAdapter(this);
        ShopRecycleView2.setAdapter(shopAdapter2);
        GridLayoutManager mGridFP2 = new GridLayoutManager(getApplicationContext(), 1);
        ShopRecycleView2.setLayoutManager(mGridFP2);
        ShopRecycleView2.setNestedScrollingEnabled(false);

        try {
            LoadCacheCat();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SearchInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length()>0)
                {

                    SearchView1.setVisibility(View.VISIBLE);
                    SearchView2.setVisibility(View.VISIBLE);
                    SearchView3.setVisibility(View.VISIBLE);
                    SearchView4.setVisibility(View.VISIBLE);
                    Search(s.toString());
                }else {

                    SearchView1.setVisibility(View.GONE);
                    SearchView2.setVisibility(View.GONE);
                    SearchView3.setVisibility(View.GONE);
                    SearchView4.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(count>0) {
                    SearchIcon.setImageDrawable(getDrawable(R.drawable.ic_arrow_left_s_line));
                    CloseIcon.setVisibility(View.VISIBLE);
                    SearchText.setVisibility(View.VISIBLE);
                }
                else {
                    SearchText.setVisibility(View.GONE);
                    SearchIcon.setImageDrawable(getDrawable(R.drawable.ic_search_2_line));
                    CloseIcon.setVisibility(View.GONE);
                    SearchText.setVisibility(View.GONE);
                }
            }
        });

    }

    public void LoadCacheCat() throws JSONException {

        //CategoryCache
        String categoriesObjs = sharedPreferences.getString("categories", "");
        String restaurantsObjs = sharedPreferences.getString("restaurantsObjs", "");
        String nrestaurantsObjs = sharedPreferences.getString("nrestaurantsObjs", "");

        JSONArray categoriesObj = new JSONArray(categoriesObjs);
        mainCategoryModels.clear();
        int disCnt = categoriesObj.length() >= 8 ? 8 : categoriesObj.length();
        for (int i = 0; i < disCnt; i++) {
            String categoriesArray = categoriesObj.getString(i);
            JSONObject categoriesObject = new JSONObject(categoriesArray);
            MainCategoryModel mainCategoryModel = new MainCategoryModel();
            mainCategoryModel.setId(categoriesObject.getString("id"));
            mainCategoryModel.setName(categoriesObject.getString("name"));
            mainCategoryModel.setImage(getString(R.string.app_url) + categoriesObject.getString("image"));
            mainCategoryModel.setType("restaurant");
            mainCategoryModels.add(mainCategoryModel);
        }
        mainCategoryAdaptor.renewItems(mainCategoryModels);

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
            shopModel.setOpenTime(restaurantsObject.getString("open_time"));
            shopModel.setStatus("Active");
            shopModel.setType("restaurant");
            shopModels.add(shopModel);
        }
        shopAdapter.renewItems(shopModels);

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
            shopModel.setType("restaurant");
            shopModels2.add(shopModel);
        }
        shopAdapter2.renewItems(shopModels2);

    }

    public void Search(String Keyword)
    {
        JRequest jRequest;
            SearchText.setText("searching...");

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("user_id", sharedPreferences.getString("id", ""));
            RequestJson.put("pincode", sharedPreferences.getString("pincode", ""));
            RequestJson.put("search", Keyword);

            jRequest = new JRequest(RequestJson, "customer/search", true, this, new JRequest.TaskCompleteListener(){
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

                            String cartObj = Res.getString("restCategories");
                            JSONArray UnitArray = new JSONArray(cartObj);

                            searchModels1.clear();
                            for (int i = 0; i < UnitArray.length(); i++)
                            {
                                String Unit = UnitArray.getString(i);
                                JSONObject UnitObject = new JSONObject(Unit);
                                SearchModel searchModel = new SearchModel();
                                searchModel.setID(UnitObject.getString("id"));
                                searchModel.setName(UnitObject.getString("name"));
                                searchModel.setImage(UnitObject.getString("image"));
                                searchModel.setType("Restaurant Category");
                                searchModel.setStatus("Active");
//                                searchModel.setImage(UnitObject.getString("status"));
//                                searchModel.setType(UnitObject.getString("status"));
                                searchModels1.add(searchModel);
                            }
                            searchAdapter1.renewItems(searchModels1);

//                            String cartObj2 = Res.getString("superCategories");
//                            JSONArray UnitArray2 = new JSONArray(cartObj2);
//                            searchModels2.clear();
//                            for (int i = 0; i < UnitArray2.length(); i++)
//                            {
//                                String Unit = UnitArray2.getString(i);
//                                JSONObject UnitObject = new JSONObject(Unit);
//                                SearchModel searchModel = new SearchModel();
//                                searchModel.setID(UnitObject.getString("id"));
//                                searchModel.setName(UnitObject.getString("name"));
//                                searchModel.setImage(UnitObject.getString("image"));
//                                searchModel.setType("Supermarket Category");
//                                searchModel.setStatus("Active");
////                                searchModel.setImage(UnitObject.getString("status"));
////                                searchModel.setType(UnitObject.getString("status"));
//                                searchModels2.add(searchModel);
//                            }
//                            searchAdapter2.renewItems(searchModels2);

                            String cartObj3 = Res.getString("restaurants");
                            JSONArray UnitArray3 = new JSONArray(cartObj3);
                            searchModels3.clear();
                            for (int i = 0; i < UnitArray3.length(); i++)
                            {
                                String Unit = UnitArray3.getString(i);
                                JSONObject UnitObject = new JSONObject(Unit);
                                SearchModel searchModel = new SearchModel();
                                searchModel.setID(UnitObject.getString("id"));
                                searchModel.setName(UnitObject.getString("name"));
                                searchModel.setImage(UnitObject.getString("logo"));
                                searchModel.setType("Restaurant");
                                searchModel.setStatus("Active");
//                                searchModel.setImage(UnitObject.getString("status"));
//                                searchModel.setType(UnitObject.getString("status"));
                                searchModels3.add(searchModel);
                            }
                            searchAdapter3.renewItems(searchModels3);

                            String cartObj4 = Res.getString("supermarkets");
                            JSONArray UnitArray4 = new JSONArray(cartObj4);
                            searchModels4.clear();
                            for (int i = 0; i < UnitArray4.length(); i++)
                            {
                                String Unit = UnitArray4.getString(i);
                                JSONObject UnitObject = new JSONObject(Unit);
                                SearchModel searchModel = new SearchModel();
                                searchModel.setID(UnitObject.getString("id"));
                                searchModel.setName(UnitObject.getString("name"));
                                searchModel.setImage(UnitObject.getString("logo"));
                                searchModel.setType("Supermarket");
                                searchModel.setStatus("Active");
//                                searchModel.setImage(UnitObject.getString("status"));
//                                searchModel.setType(UnitObject.getString("status"));
                                searchModels4.add(searchModel);
                            }
                            searchAdapter4.renewItems(searchModels4);

                            int rest = UnitArray.length()+UnitArray3.length()+UnitArray4.length();
                            if (rest>0)
                            {  SearchText.setText("search results");
                            }else
                            { SearchText.setText("no results found!");}

                        }

                        else
                        {
                            showMsg(msg);
                            SearchText.setText("no results found!");
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

    public void goBack(View v)
    {
        SearchInput.setText("");
        super.onBackPressed();
    }

    public void Clear(View v)
    {
        SearchInput.setText("");
    }

}