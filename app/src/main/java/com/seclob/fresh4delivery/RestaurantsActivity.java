package com.seclob.fresh4delivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;
import com.seclob.fresh4delivery.adaptor.CategoryAdapter;
import com.seclob.fresh4delivery.adaptor.RproAdapter;
import com.seclob.fresh4delivery.adaptor.UnitAdapter;
import com.seclob.fresh4delivery.adaptor.UnitAdapterD;
import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.model.CategoryModel;
import com.seclob.fresh4delivery.model.RproModel;
import com.seclob.fresh4delivery.model.UnitModel;
import com.seclob.fresh4delivery.model.UnitModelD;
import com.squareup.picasso.Picasso;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.blurry.Blurry;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RestaurantsActivity extends AppCompatActivity implements CategoryAdapter.getCategory, RproAdapter.getUnit, UnitAdapter.getUpdate,RproAdapter.getUpdate{

    ImageView Logo, Banner, PopupView;
    TextView ResName, ResDtime, ItemCount, CartTotal;
    MaterialRatingBar ResRating;
    String ProductsArray,Categories;
    int cpos=0,clen=0;
    List<CategoryModel> categoryModels = new ArrayList<>();
    CategoryAdapter categoryAdapter;
    RecyclerView CategoryRecycleview;
    RelativeLayout swipLayout;
    List<RproModel> rproModels = new ArrayList<>();
    RproAdapter rproAdapter;
    RecyclerView RproRecycleview;
    String type="Restaurant",shop_id="";
    SharedPreferences sharedPreferences;
    String cartObj;
    FrameLayout unitBox;

    List<UnitModel> unitModels = new ArrayList<>();
    UnitAdapter unitAdapter;
    RecyclerView UnitRecycleview;

    List<UnitModelD> unitModelsD = new ArrayList<>();
    UnitAdapterD unitAdapterD;
    RecyclerView UnitRecycleviewD;
    Animation right,left;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        getSupportActionBar().hide();
        swipLayout = findViewById(R.id.swipLayout);
        Logo = findViewById(R.id.res_logo_image);
        Banner = findViewById(R.id.res_banner_image);
        ResName = findViewById(R.id.res_name);
        ResDtime = findViewById(R.id.res_dtime);
        ResRating = findViewById(R.id.res_rating);
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);
        unitBox = findViewById(R.id.unitBox);
        ItemCount = findViewById(R.id.res_itemCount);
        CartTotal = findViewById(R.id.res_Total);

        try {
            Intent intent = getIntent();
            type = intent.getStringExtra("type");
            shop_id = intent.getStringExtra("shop_id");
        } catch (Exception e) {
            Log.e("getIntent Error", e + "");
        }

        CategoryRecycleview = findViewById(R.id.CategoryRecycleview);
        this.categoryAdapter = new CategoryAdapter(this);
        CategoryRecycleview.setAdapter(categoryAdapter);
        LinearLayoutManager HorizontalLayout = new LinearLayoutManager(RestaurantsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        CategoryRecycleview.setLayoutManager(HorizontalLayout);
        ((SimpleItemAnimator) CategoryRecycleview.getItemAnimator()).setSupportsChangeAnimations(true);

        RproRecycleview = findViewById(R.id.RproRecycleview);
        this.rproAdapter = new RproAdapter(this);
        RproRecycleview.setAdapter(rproAdapter);
        RproRecycleview.setNestedScrollingEnabled(true);
        GridLayoutManager mGrid = new GridLayoutManager(getApplicationContext(), 1);
        RproRecycleview.setLayoutManager(mGrid);

        RproRecycleview.setOnTouchListener(new OnSwipeTouchListener(RestaurantsActivity.this) {
            @Override
            public void onSwipeLeft() throws JSONException {
                super.onSwipeLeft();

                if(cpos<clen-1)
                {cpos++;
                    getCart(cpos);
                //ChangeCat("",cpos);
                //Toast.makeText(RestaurantsActivity.this, "Swipe Left gesture detected", Toast.LENGTH_SHORT).show();
                    right = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.ani_right);
                    RproRecycleview.setAnimation(right);
                }
            }
            @Override
            public void onSwipeRight() throws JSONException {
                super.onSwipeRight();
                if (cpos > 0) {
                    left = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.ani_left);
                    RproRecycleview.setAnimation(left);
                    cpos--;
                    getCart(cpos);
                    //ChangeCat("", cpos);
                    //Toast.makeText(RestaurantsActivity.this, "Swipe Right gesture detected", Toast.LENGTH_SHORT).show();]

                }
            }
        });

        UnitRecycleview = findViewById(R.id.UnitRecycleview);
        this.unitAdapter = new UnitAdapter(this);
        UnitRecycleview.setAdapter(unitAdapter);
        UnitRecycleview.setNestedScrollingEnabled(true);
        GridLayoutManager mGrid2 = new GridLayoutManager(getApplicationContext(), 1);
        UnitRecycleview.setLayoutManager(mGrid2);

        UnitRecycleviewD = findViewById(R.id.UnitRecycleviewD);
        this.unitAdapterD = new UnitAdapterD(this);
        UnitRecycleviewD.setAdapter(unitAdapterD);
        UnitRecycleviewD.setNestedScrollingEnabled(true);
        GridLayoutManager mGrid3 = new GridLayoutManager(getApplicationContext(), 1);
        UnitRecycleviewD.setLayoutManager(mGrid3);


    }

    @Override
    protected void onResume() {
        try {
            getCart();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    public void GoBack(View view)
    {
        super.onBackPressed();
    }

    public void GetResDetails(String ID)
    {
        FrameLayout Loader = findViewById(R.id.res_loader);
        Loader.setVisibility(View.VISIBLE);
        JRequest jRequest;

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("user_id", sharedPreferences.getString("id", ""));
            RequestJson.put("pincode", sharedPreferences.getString("pincode",""));

            jRequest = new JRequest(RequestJson, "2.0/"+type.toLowerCase(Locale.ROOT)+"/"+ID, true, this, new JRequest.TaskCompleteListener(){
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


                            String shopObj = Res.getString("shop");
                            JSONObject ShopObj = new JSONObject(shopObj);
                            Picasso.get().load(getString(R.string.app_url)+ShopObj.getString("banner")).into(Banner);
                            Picasso.get().load(getString(R.string.app_url)+ShopObj.getString("logo")).into(Logo);
                            ResName.setText(ShopObj.getString("name"));
                            ResDtime.setText("Delivers in "+ShopObj.getString("delivery_time"));
                            ResRating.setRating(Float.parseFloat(ShopObj.getString("rating")));

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("shop_id", ShopObj.getString("id"));
                            editor.putString("type", type);
                            editor.apply();

                            Categories = Res.getString("category");
                            JSONArray CategoriesArray = new JSONArray(Categories);
                            clen = CategoriesArray.length();
                            if(clen>1)
                            {
                                findViewById(R.id.cat_view).setVisibility(View.VISIBLE);
                            }

                            ProductsArray = Res.getString("products");
                            ChangeCat2("", 0);

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

    @Override
    public void Cat(String catId, int pos) throws JSONException {

        getCart(pos);
    }

    @Override
    public void Unit(String Array,String Pname, String IsVeg, String RID) throws JSONException {

        JSONArray UnitArray = new JSONArray(Array);
        unitModelsD.clear();

        for (int i = 0; i < UnitArray.length(); i++)
        {
            String Unit = UnitArray.getString(i);
            JSONObject UnitObject = new JSONObject(Unit);
            UnitModelD unitModelD = new UnitModelD();
            unitModelD.setID(UnitObject.getString("id"));
            unitModelsD.add(unitModelD);
        }
        unitAdapterD.renewItems(unitModelsD);

        LayoutAnimationController anicon =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation);
        unitBox.setLayoutAnimation(anicon);
        unitBox.setVisibility(View.VISIBLE);

        TextView PnameView = findViewById(R.id.pro_name);
        PnameView.setText(Pname);
        getCart4Unit(Array,Pname,IsVeg);



    }

    public void DoneUnitBtn(View view)
    {
        findViewById(R.id.unitBox).setVisibility(View.GONE);

    }

    public void ClosePop(View view)
    {
        findViewById(R.id.unitBox).setVisibility(View.GONE);

    }

    public void ChangeCat(String CatID, int Pos) throws JSONException {

        cpos = Pos;
        JSONArray CategoriesArray = new JSONArray(Categories);
//        categoryModels.clear();
//        for (int i = 0; i < CategoriesArray.length(); i++)
//        {
//            String Category = CategoriesArray.getString(i);
//            JSONObject CategoryObject = new JSONObject(Category);
//            CategoryModel categoryModel = new CategoryModel();
//            categoryModel.setID(CategoryObject.getString("id"));
//            categoryModel.setName(CategoryObject.getString("name"));
//            categoryModel.setPos(Pos);
//            categoryModels.add(categoryModel);
//        }
//        categoryAdapter.renewItems(categoryModels);

        String Category2 = CategoriesArray.getString(Pos);
        JSONObject CategoryObject2 = new JSONObject(Category2);
        String newCatId = CategoryObject2.getString("id");
        RproRecycleview.scrollToPosition(0);
//        runLayoutAnimation(RproRecycleview);
        JSONArray ProductArray = new JSONArray(ProductsArray);
        rproModels.clear();
        for (int i = 0; i < ProductArray.length(); i++)
        {
            String Product = ProductArray.getString(i);
            JSONObject ProductObject = new JSONObject(Product);
            if(ProductObject.getString("cat_id").equalsIgnoreCase(newCatId)) {
                RproModel rproModel = new RproModel();
                rproModel.setIsCarted("0");
                JSONArray cartArray = new JSONArray(cartObj);
                for (int c = 0; c < cartArray.length(); c++)
                {
                    String CartItem = cartArray.getString(c);
                    JSONObject CartObject = new JSONObject(CartItem);
                    if(CartObject.getString("product_id").equalsIgnoreCase(ProductObject.getString("id")))
                    {
                        rproModel.setCartedQty(CartObject.getString("quantity"));
                        rproModel.setIsCarted("1");
                    }
                }
                rproModel.setID(ProductObject.getString("id"));
                rproModel.setName(ProductObject.getString("name"));
                rproModel.setIsVeg(ProductObject.getString("type"));
                rproModel.setPrice(ProductObject.getString("price"));
                rproModel.setOffer(ProductObject.getString("offerprice"));
                rproModel.setDesc(ProductObject.getString("desc"));
                rproModel.setRes("");
                rproModel.setResID("");
                rproModel.setIsAvailable(ProductObject.getString("status"));
                rproModel.setImage(ProductObject.getString("image"));
                rproModel.setHasUnit(ProductObject.getString("has_units"));
                rproModel.setUnit(ProductObject.getString("units"));
                rproModels.add(rproModel);
            }
        }
        rproAdapter.renewItems(rproModels);
        findViewById(R.id.res_shimer_lader).setVisibility(View.GONE);



    }

    public void ChangeCat2(String CatID, int Pos) throws JSONException {

        cpos = Pos;
        JSONArray CategoriesArray = new JSONArray(Categories);
        categoryModels.clear();
        for (int i = 0; i < CategoriesArray.length(); i++)
        {
            String Category = CategoriesArray.getString(i);
            JSONObject CategoryObject = new JSONObject(Category);
            CategoryModel categoryModel = new CategoryModel();
            categoryModel.setID(CategoryObject.getString("id"));
            categoryModel.setName(CategoryObject.getString("name"));
            categoryModel.setPos(Pos);
            categoryModels.add(categoryModel);
        }
        categoryAdapter.renewItems(categoryModels);

        String Category2 = CategoriesArray.getString(Pos);
        JSONObject CategoryObject2 = new JSONObject(Category2);
        String newCatId = CategoryObject2.getString("id");
        RproRecycleview.scrollToPosition(0);
//        runLayoutAnimation(RproRecycleview);
        JSONArray ProductArray = new JSONArray(ProductsArray);
        rproModels.clear();
        for (int i = 0; i < ProductArray.length(); i++)
        {
            String Product = ProductArray.getString(i);
            JSONObject ProductObject = new JSONObject(Product);
            if(ProductObject.getString("cat_id").equalsIgnoreCase(newCatId)) {
                RproModel rproModel = new RproModel();
                rproModel.setIsCarted("0");
                rproModel.setHasUnit(ProductObject.getString("has_units"));
                JSONArray cartArray = new JSONArray(cartObj);
                for (int c = 0; c < cartArray.length(); c++)
                {
                    String CartItem = cartArray.getString(c);
                    JSONObject CartObject = new JSONObject(CartItem);
                    if(CartObject.getString("product_id").equalsIgnoreCase(ProductObject.getString("id")))
                    {
                        rproModel.setCartedQty(CartObject.getString("quantity"));
                        rproModel.setIsCarted("1");
                    }
                }
                rproModel.setID(ProductObject.getString("id"));
                rproModel.setName(ProductObject.getString("name"));
                rproModel.setIsVeg(ProductObject.getString("type"));
                rproModel.setPrice(ProductObject.getString("price"));
                rproModel.setDesc(ProductObject.getString("desc"));
                rproModel.setOffer(ProductObject.getString("offerprice"));
                rproModel.setIsAvailable(ProductObject.getString("status"));
                rproModel.setRes("");
                rproModel.setResID("");
                rproModel.setImage(ProductObject.getString("image"));
                rproModel.setUnit(ProductObject.getString("units"));
                rproModels.add(rproModel);
            }
        }
        rproAdapter.renewItems(rproModels);

        LinearLayoutManager layoutManager = ((LinearLayoutManager)CategoryRecycleview.getLayoutManager());
        int totalVisibleItems = layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition();
        int centeredItemPosition = totalVisibleItems / 2;
        CategoryRecycleview.smoothScrollToPosition(Pos);
        CategoryRecycleview.setScrollY(centeredItemPosition );

    }


    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


    public void getCart(int Pos) throws JSONException {



        findViewById(R.id.res_shimer_lader).setVisibility(View.VISIBLE);
        JSONArray CategoriesArray = new JSONArray(Categories);
        rproModels.clear();
        rproAdapter.renewItems(rproModels);
        categoryModels.clear();
        for (int i = 0; i < CategoriesArray.length(); i++)
        {
            String Category = CategoriesArray.getString(i);
            JSONObject CategoryObject = new JSONObject(Category);
            CategoryModel categoryModel = new CategoryModel();
            categoryModel.setID(CategoryObject.getString("id"));
            categoryModel.setName(CategoryObject.getString("name"));
            categoryModel.setPos(Pos);
            categoryModels.add(categoryModel);
        }
        categoryAdapter.renewItems(categoryModels);
        LinearLayoutManager layoutManager = ((LinearLayoutManager)CategoryRecycleview.getLayoutManager());
        int totalVisibleItems = layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition();
        int centeredItemPosition = totalVisibleItems / 2;
        CategoryRecycleview.smoothScrollToPosition(Pos);
        CategoryRecycleview.setScrollY(centeredItemPosition );

        cartObj = sharedPreferences.getString("cartObj","");
        String ccTotal = sharedPreferences.getString("ccTotal","");
        BottomItemView(cartObj,ccTotal);
        ChangeCat("",Pos);


    }

    public void BottomItemView(String Array,String Total) throws JSONException {

        JSONArray UnitArray = new JSONArray(Array);
        String itemCounts = UnitArray.length()+"";
        if(UnitArray.length()>0)
        {
            findViewById(R.id.botttomCartBox).setVisibility(View.VISIBLE);
            ItemCount.setText(itemCounts+(UnitArray.length()>1?" items":" item"));
            CartTotal.setText("₹"+Total);
        }
        else
        {
            findViewById(R.id.botttomCartBox).setVisibility(View.GONE);
        }

    }

    public void getCart() throws JSONException {

                            cartObj = sharedPreferences.getString("cartObj","");
                            String ccTotal = sharedPreferences.getString("ccTotal","");
                            GetResDetails(shop_id);
                            BottomItemView(cartObj,ccTotal);
//                            ChangeCat2("",cpos);

    }

    @Override
    public void Update(String catId) throws JSONException {
//        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        getCartNull();
    }

    public void getCart4Unit(String Array,String RID, String IsVeg) throws JSONException {

        findViewById(R.id.res_unit_shimmer_loader).setVisibility(View.VISIBLE);
        unitModels.clear();
        unitAdapter.renewItems(unitModels);
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

                            findViewById(R.id.res_unit_shimmer_loader).setVisibility(View.GONE);


                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("cartObj", Res.getString("cart"));
                            editor.putString("ccTotal", Res.getString("total_amount"));
                            editor.apply();

                            cartObj = Res.getString("cart");
                            JSONArray UnitArray = new JSONArray(Array);

                            String ccTotal = Res.getString("total_amount");
                            BottomItemView(cartObj,ccTotal);

                            Log.e("UnitArray",Array);
                            unitModels.clear();
                            for (int i = 0; i < UnitArray.length(); i++)
                            {
                                String Unit = UnitArray.getString(i);
                                JSONObject UnitObject = new JSONObject(Unit);
                                UnitModel unitModel = new UnitModel();
                                unitModel.setIsCarted("0");

                                JSONArray cartArray = new JSONArray(cartObj);
                                for (int c = 0; c < cartArray.length(); c++)
                                {
                                    String CartItem = cartArray.getString(c);
                                    JSONObject CartObject = new JSONObject(CartItem);
                                    if(CartObject.getString("unit_id").equalsIgnoreCase(UnitObject.getString("id")))
                                    {
                                        unitModel.setQty(CartObject.getString("quantity"));
                                        unitModel.setIsCarted("1");
                                    }
                                }

                                unitModel.setID(UnitObject.getString("id"));
                                unitModel.setPID(UnitObject.getString("product_id"));
                                unitModel.setRID("");
                                unitModel.setName(UnitObject.getString("name"));
                                unitModel.setPrice(UnitObject.getString("price"));
                                unitModel.setOffer(UnitObject.getString("offerprice"));
                                unitModel.setIsVeg(IsVeg);
                                unitModels.add(unitModel);
                            }
                            unitAdapter.renewItems(unitModels);


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

                            cartObj = Res.getString("cart");
                            String ccTotal = Res.getString("total_amount");
                            BottomItemView(cartObj,ccTotal);
                            ChangeCat("",cpos);

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


    @Override
    public void UpdateCart() throws JSONException {
        getCartNull();
    }

    public void GoCart(View v)
    {
        Intent i = new Intent(this,CartActivity.class);
        startActivity(i);
    }

}