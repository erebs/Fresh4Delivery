package com.seclob.fresh4delivery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.essam.simpleplacepicker.MapActivity;
import com.essam.simpleplacepicker.utils.SimplePlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.seclob.fresh4delivery.adaptor.MainCategoryAdaptor;
import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.model.MainCategoryModel;
import com.seclob.fresh4delivery.model.ShopModel;

import org.aviran.cookiebar2.CookieBar;
import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.CarouselType;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    protected LocationManager mLocationManager;
    protected Context context;
    int LOCATION_REFRESH_TIME = 100000; // 15 seconds to update
    int LOCATION_REFRESH_DISTANCE = 2000; // 500 meters to update
    boolean gps_enabled = false;
    boolean network_enabled = false;
    TextView City,Address;
    SharedPreferences sharedPreferences;
    LinearLayout SearchBox;
    List<MainCategoryModel> mainCategoryModels = new ArrayList<>();
    private RecyclerView MainCatRecycleView;
    private MainCategoryAdaptor mainCategoryAdaptor;

    List<CarouselItem> carouselImage1 = new ArrayList<>();
    ImageCarousel carousel1;

    List<ShopModel> shopModels = new ArrayList<>();
    List<ShopModel> shopModels2 = new ArrayList<>();
    List<ShopModel> shopModels3 = new ArrayList<>();
    List<ShopModel> shopModels4 = new ArrayList<>();

    private RecyclerView ShopRecycleView;
    private RecyclerView ShopRecycleView2;
    private RecyclerView ShopRecycleView3;
    private RecyclerView ShopRecycleView4;

    private ShopAdapter shopAdapter;
    private ShopAdapter shopAdapter2;
    private ShopAdapter shopAdapter3;
    private ShopAdapter shopAdapter4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);

        City = findViewById(R.id.main_city_name);
        Address = findViewById(R.id.main_address);
        SearchBox = findViewById(R.id.SearchBox);
        MainCatRecycleView = findViewById(R.id.MainCatRecycleView);
        this.mainCategoryAdaptor = new MainCategoryAdaptor(this);
        MainCatRecycleView.setAdapter(mainCategoryAdaptor);
        LinearLayoutManager HorizontalLayout = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        MainCatRecycleView.setLayoutManager(HorizontalLayout);

        //ImageCarousel1
        carousel1  = findViewById(R.id.carousel1);
        carousel1.setCarouselType(CarouselType.SHOWCASE);
        carousel1.setShowNavigationButtons(false);
        carousel1.setScaleOnScroll(false);
        carousel1.setItemLayout(R.layout.custom_fixed_size_item_layout);
        carousel1.setImageViewId(R.id.image_view);

        //Shop - near res
        ShopRecycleView = findViewById(R.id.ShopRecycleView);
        this.shopAdapter = new ShopAdapter(this);
        ShopRecycleView.setAdapter(shopAdapter);
        GridLayoutManager mGridFP = new GridLayoutManager(getApplicationContext(), 1);
        ShopRecycleView.setLayoutManager(mGridFP);
        ShopRecycleView.setNestedScrollingEnabled(false);

        //Shop - near nres
        ShopRecycleView2 = findViewById(R.id.ShopRecycleView2);
        this.shopAdapter2 = new ShopAdapter(this);
        ShopRecycleView2.setAdapter(shopAdapter2);
        GridLayoutManager mGridFP2 = new GridLayoutManager(getApplicationContext(), 1);
        ShopRecycleView2.setLayoutManager(mGridFP2);
        ShopRecycleView2.setNestedScrollingEnabled(false);

        //Shop - near nres
        ShopRecycleView3 = findViewById(R.id.ShopRecycleView3);
        this.shopAdapter3 = new ShopAdapter(this);
        ShopRecycleView3.setAdapter(shopAdapter3);
        GridLayoutManager mGridFP3 = new GridLayoutManager(getApplicationContext(), 1);
        ShopRecycleView3.setLayoutManager(mGridFP3);
        ShopRecycleView3.setNestedScrollingEnabled(false);

        //Shop - near nres
        ShopRecycleView4 = findViewById(R.id.ShopRecycleView4);
        this.shopAdapter4 = new ShopAdapter(this);
        ShopRecycleView4.setAdapter(shopAdapter4);
        GridLayoutManager mGridFP4 = new GridLayoutManager(getApplicationContext(), 1);
        ShopRecycleView4.setLayoutManager(mGridFP4);
        ShopRecycleView4.setNestedScrollingEnabled(false);

        try {
            LoadCacheCat();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        statusCheck();
            City.setText(sharedPreferences.getString("city",""));
            Address.setText(sharedPreferences.getString("address",""));
            HomeApi(sharedPreferences.getString("pincode",""));
//        getCartNull();
        super.onResume();
    }

    public void statusCheck() {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }else {
            if(checkLocationPermission())
            {
                if(sharedPreferences.getString("pincode","").length()==6)
                {
                    City.setText(sharedPreferences.getString("city",""));
                    Address.setText(sharedPreferences.getString("address",""));
                    HomeApi(sharedPreferences.getString("pincode",""));
                }else {
                    LocPic();
                }

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                        LOCATION_REFRESH_DISTANCE, mLocationListener);

                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME,
                        LOCATION_REFRESH_DISTANCE, mLocationListener);
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void LoadCacheCat() throws JSONException {

        //CategoryCache
        String categoriesObjs = sharedPreferences.getString("categories","");
        JSONArray categoriesObj = new JSONArray(categoriesObjs);
        mainCategoryModels.clear();
        int disCnt = categoriesObj.length()>=8?8:categoriesObj.length();
        for (int i = 0; i < disCnt; i++)
        {
            String categoriesArray = categoriesObj.getString(i);
            JSONObject categoriesObject = new JSONObject(categoriesArray);
            MainCategoryModel mainCategoryModel = new MainCategoryModel();
            mainCategoryModel.setId(categoriesObject.getString("id"));
            mainCategoryModel.setName(categoriesObject.getString("name"));
            mainCategoryModel.setImage(getString(R.string.app_url)+categoriesObject.getString("image"));
            mainCategoryModel.setType("restaurant");
            mainCategoryModels.add(mainCategoryModel);
        }
        mainCategoryAdaptor.renewItems(mainCategoryModels);

        //Sliders1Cache
        JSONArray slidersArray1 = new JSONArray(sharedPreferences.getString("fbanners",""));
        carouselImage1.clear();
        for (int i = 0; i < slidersArray1.length(); i++)
        {
            String SliderObjectString1 = slidersArray1.getString(i);
            JSONObject SliderObject1 = new JSONObject(SliderObjectString1);
            String sliderImageUrl1 = getString(R.string.app_url) + SliderObject1.getString("image");
            carouselImage1.add(new CarouselItem(sliderImageUrl1));
        }
        carousel1.addData(carouselImage1);
        carousel1.setAutoPlay(true);

    }

    public void LocBtn(View view)
    {
        Intent i = new Intent(this, DadsActivity.class);
        startActivity(i);
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

    public void getCartNull()
    {
        JRequest jRequest;

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("user_id", sharedPreferences.getString("id", ""));

            jRequest = new JRequest(RequestJson, "getcart", true, this, new JRequest.TaskCompleteListener(){
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



                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("cartObj", Res.getString("cart"));
                            editor.putString("ccTotal", Res.getString("total_amount"));
                            editor.apply();

                            String ccTotal = Res.getString("total_amount");
                            BottomItemView(Res.getString("cart"),ccTotal);
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

    public void BottomItemView(String Array,String Total) throws JSONException {

        TextView ItemCount,CartTotal;
        ItemCount = findViewById(R.id.res_itemCount3);
        CartTotal = findViewById(R.id.res_Total3);
        JSONArray UnitArray = new JSONArray(Array);
        String itemCounts = UnitArray.length()+"";
        if(UnitArray.length()>0)
        {
            findViewById(R.id.botttomCartBox3).setVisibility(View.VISIBLE);
            ItemCount.setText(itemCounts+(UnitArray.length()>1?" items":" item"));
            CartTotal.setText("₹"+Total);
        }
        else
        {
            findViewById(R.id.botttomCartBox3).setVisibility(View.GONE);
        }

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();


                Log.e("Pincode",postalCode);


                if(sharedPreferences.getString("pincode","").length()==6)
                {

                }else
                {
//                    City.setText(city);
//                    Address.setText(address);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("pincode", postalCode);
                    editor.putString("address", address);
                    editor.putString("city", city);
                    editor.apply();
                    HomeApi(postalCode);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Test")
                        .setMessage("Tessst")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_REFRESH_TIME,
                                LOCATION_REFRESH_DISTANCE, mLocationListener);

                    }

                } else {


                }
                return;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        try {

            Log.e("Lan",data.getExtras().getString(SimplePlacePicker.SELECTED_ADDRESS));
            double lon = Double.parseDouble(data.getExtras().getString("Lon"));
            double lan  = Double.parseDouble(data.getExtras().getString("Lan"));

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

            Toast.makeText(this, postalCode, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void goRes(View view)
    {
        Intent intent = new Intent(this, ShopsActivity.class);
        intent.putExtra("type", "Restaurants");
        intent.putExtra("cat_id", "Restaurants");
        startActivity(intent);
    }


    public void GoProfile(View view)
    {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void GoSearch(View view)
    {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void goSup(View view)
    {
        Intent intent = new Intent(this, ShopsActivity.class);
        intent.putExtra("type", "Supermarkets");
        intent.putExtra("cat_id", "Restaurants");
        startActivity(intent);
    }

    public void HomeApi(String Pincode)
    {
        SearchBox.setClickable(false);
        JRequest jRequest;
        findViewById(R.id.main_sup_loader).setVisibility(View.VISIBLE);
        findViewById(R.id.main_res_loader).setVisibility(View.VISIBLE);
        ShopRecycleView.setVisibility(View.GONE);
        ShopRecycleView2.setVisibility(View.GONE);
        ShopRecycleView3.setVisibility(View.GONE);
        ShopRecycleView4.setVisibility(View.GONE);
        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("user_id", sharedPreferences.getString("id", ""));
            RequestJson.put("limit", "10");
            RequestJson.put("pincode", sharedPreferences.getString("pincode",""));
//            RequestJson.put("pincode", Pincode);
            //RequestJson.put("access_token", sharedPreferences.getString("token", ""));

            jRequest = new JRequest(RequestJson, "2.0/home", true, this, new JRequest.TaskCompleteListener(){
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
                            SearchBox.setClickable(true);
                            findViewById(R.id.main_sup_loader).setVisibility(View.GONE);
                            findViewById(R.id.main_res_loader).setVisibility(View.GONE);
                            ShopRecycleView.setVisibility(View.VISIBLE);
                            ShopRecycleView2.setVisibility(View.VISIBLE);
                            ShopRecycleView3.setVisibility(View.VISIBLE);
                            ShopRecycleView4.setVisibility(View.VISIBLE);

                            //Categories
                            String categoriesObjs = Res.getString("categories");
                            String slidersArrays1 = Res.getString("fbanners");
                            String restaurantsObjs = Res.getString("restaurants");
                            String nrestaurantsObjs = Res.getString("nrestaurants");


                            JSONArray categoriesObj = new JSONArray(categoriesObjs);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("cartObj", Res.getString("cart"));
                            editor.putString("ccTotal", Res.getString("total_amount"));
                            editor.putString("categories", categoriesObjs);
                            editor.putString("restaurantsObjs", restaurantsObjs);
                            editor.putString("nrestaurantsObjs", nrestaurantsObjs);
                            editor.putString("fbanners", slidersArrays1);
                            editor.apply();

                            BottomItemView(Res.getString("cart"),Res.getString("total_amount"));

                            mainCategoryModels.clear();
                            int disCnt = categoriesObj.length()>=8?8:categoriesObj.length();
                            for (int i = 0; i < disCnt; i++)
                            {
                                String categoriesArray = categoriesObj.getString(i);
                                JSONObject categoriesObject = new JSONObject(categoriesArray);
                                MainCategoryModel mainCategoryModel = new MainCategoryModel();
                                mainCategoryModel.setId(categoriesObject.getString("id"));
                                mainCategoryModel.setName(categoriesObject.getString("name"));
                                mainCategoryModel.setImage(getString(R.string.app_url)+categoriesObject.getString("image"));
                                mainCategoryModel.setType("restaurant");
                                mainCategoryModels.add(mainCategoryModel);
                            }
                            mainCategoryAdaptor.renewItems(mainCategoryModels);

                            //Sliders1

                            JSONArray slidersArray1 = new JSONArray(slidersArrays1);

                            carouselImage1.clear();
                            for (int i = 0; i < slidersArray1.length(); i++)
                            {
                                String SliderObjectString1 = slidersArray1.getString(i);
                                JSONObject SliderObject1 = new JSONObject(SliderObjectString1);
                                String sliderImageUrl1 = getString(R.string.app_url) + SliderObject1.getString("image");
                                carouselImage1.add(new CarouselItem(sliderImageUrl1));
                            }
                            carousel1.addData(carouselImage1);
                            carousel1.setAutoPlay(true);


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

                            String nrestaurantsObjs3 = Res.getString("supermarkets");
                            JSONArray nrestaurantsObj3 = new JSONArray(nrestaurantsObjs3);
                            shopModels3.clear();
                            for (int i = 0; i < nrestaurantsObj3.length(); i++)
                            {
                                String nrestaurantsArray3 = nrestaurantsObj3.getString(i);
                                JSONObject nrestaurantsObject3 = new JSONObject(nrestaurantsArray3);
                                ShopModel shopModel = new ShopModel();
                                shopModel.setID(nrestaurantsObject3.getString("id"));
                                shopModel.setName(nrestaurantsObject3.getString("name"));
                                shopModel.setImage(nrestaurantsObject3.getString("logo"));
                                shopModel.setDes(nrestaurantsObject3.getString("desc"));
                                shopModel.setDtime(nrestaurantsObject3.getString("delivery_time"));
                                shopModel.setRating(nrestaurantsObject3.getString("rating"));
                                shopModel.setOpenTime(nrestaurantsObject3.getString("open_time"));
                                shopModel.setStatus("Active");
                                shopModel.setType("supermarket");
                                shopModels3.add(shopModel);
                            }
                            shopAdapter3.renewItems(shopModels3);

                            String nrestaurantsObjs4 = Res.getString("nsupermarkets");
                            JSONArray nrestaurantsObj4 = new JSONArray(nrestaurantsObjs4);
                            shopModels4.clear();
                            for (int i = 0; i < nrestaurantsObj4.length(); i++)
                            {
                                String nrestaurantsArray4 = nrestaurantsObj4.getString(i);
                                JSONObject nrestaurantsObject4 = new JSONObject(nrestaurantsArray4);
                                ShopModel shopModel = new ShopModel();
                                shopModel.setID(nrestaurantsObject4.getString("id"));
                                shopModel.setName(nrestaurantsObject4.getString("name"));
                                shopModel.setImage(nrestaurantsObject4.getString("logo"));
                                shopModel.setDes(nrestaurantsObject4.getString("desc"));
                                shopModel.setDtime(nrestaurantsObject4.getString("delivery_time"));
                                shopModel.setRating(nrestaurantsObject4.getString("rating"));
                                shopModel.setOpenTime(nrestaurantsObject4.getString("open_time"));
                                shopModel.setStatus("Not");
                                shopModel.setType("supermarket");
                                shopModels4.add(shopModel);
                            }
                            shopAdapter4.renewItems(shopModels4);

                            int num = restaurantsObj.length()+nrestaurantsObj.length()+nrestaurantsObj3.length()+nrestaurantsObj4.length();
                            if (num>0)
                                findViewById(R.id.notd).setVisibility(View.GONE);
                            else
                                findViewById(R.id.notd).setVisibility(View.VISIBLE);

//
//                            //Sliders2
//                            String slidersArrays2 = Res.getString("sbanners");
//                            JSONArray slidersArray2 = new JSONArray(slidersArrays2);
//
//                            carouselImage2.clear();
//                            for (int i = 0; i < slidersArray2.length(); i++)
//                            {
//                                String SliderObjectString2 = slidersArray2.getString(i);
//                                JSONObject SliderObject2 = new JSONObject(SliderObjectString2);
//                                String sliderImageUrl2 = getString(R.string.site_url) + SliderObject2.getString("image");
//                                carouselImage2.add(new CarouselItem(sliderImageUrl2));
//                            }
//                            carousel2.addData(carouselImage2);
//                            carousel2.setAutoPlay(true);


                        }

                        else
                        {
                            //showMsg(msg);
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
                .setMessage(msg).setCookiePosition(CookieBar.BOTTOM)
                .setBackgroundColor(R.color.lite_green)
                .setDuration(5000).show();
    }

    public void GoCart(View view){
        Intent i = new Intent(this,CartActivity.class);
        startActivity(i);
    }

    //BottomNav
    public void goHome(View view){
//        Intent i = new Intent(this,MainActivity.class);
//        startActivity(i);
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
        Intent i = new Intent(this,HisActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
        finish();
    }

}