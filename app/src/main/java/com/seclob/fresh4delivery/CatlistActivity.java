package com.seclob.fresh4delivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.seclob.fresh4delivery.adaptor.CategoryAdapter;
import com.seclob.fresh4delivery.adaptor.RproAdapter;
import com.seclob.fresh4delivery.adaptor.UnitAdapter;
import com.seclob.fresh4delivery.adaptor.UnitAdapterD;
import com.seclob.fresh4delivery.libraries.JRequest;
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

public class CatlistActivity extends AppCompatActivity implements RproAdapter.getUnit, UnitAdapter.getUpdate,RproAdapter.getUpdate{

    TextView Title,ItemCount,CartTotal;
    String Cname, Cid;

    List<RproModel> rproModels = new ArrayList<>();
    RproAdapter rproAdapter;
    RecyclerView RproRecycleview;
    SharedPreferences sharedPreferences;
    String cartObj;
    FrameLayout unitBox;
    String ProductsArray="";
    List<UnitModel> unitModels = new ArrayList<>();
    UnitAdapter unitAdapter;
    RecyclerView UnitRecycleview;

    List<UnitModelD> unitModelsD = new ArrayList<>();
    UnitAdapterD unitAdapterD;
    RecyclerView UnitRecycleviewD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catlist);
        getSupportActionBar().hide();
        Title = findViewById(R.id.cat_name_cat);
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);
        unitBox = findViewById(R.id.unitBox2);
        ItemCount = findViewById(R.id.res_itemCount2);
        CartTotal = findViewById(R.id.res_Total2);

        try {
            Intent intent = getIntent();
            Cname = intent.getStringExtra("cname");
            Cid = intent.getStringExtra("cid");
        } catch (Exception e) {
            Log.e("getIntent Error", e + "");
        }
        Title.setText(Cname.toLowerCase(Locale.ROOT));
        RproRecycleview = findViewById(R.id.catsView);
        this.rproAdapter = new RproAdapter(this);
        RproRecycleview.setAdapter(rproAdapter);
        RproRecycleview.setNestedScrollingEnabled(true);
        GridLayoutManager mGrid = new GridLayoutManager(getApplicationContext(), 1);
        RproRecycleview.setLayoutManager(mGrid);

        UnitRecycleview = findViewById(R.id.UnitRecycleview2);
        this.unitAdapter = new UnitAdapter(this);
        UnitRecycleview.setAdapter(unitAdapter);
        UnitRecycleview.setNestedScrollingEnabled(true);
        GridLayoutManager mGrid2 = new GridLayoutManager(getApplicationContext(), 1);
        UnitRecycleview.setLayoutManager(mGrid2);

        UnitRecycleviewD = findViewById(R.id.UnitRecycleviewD2);
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


    public void GetCatProducts(String ID)
    {
        FrameLayout Loader = findViewById(R.id.res_shimer_lader2);
        Loader.setVisibility(View.VISIBLE);
        JRequest jRequest;

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("user_id", sharedPreferences.getString("id", ""));
            RequestJson.put("pincode", sharedPreferences.getString("pincode",""));

            jRequest = new JRequest(RequestJson, "customer/restaurants/category/"+ID, true, this, new JRequest.TaskCompleteListener(){
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

                            ProductsArray = Res.getString("products");
                            JSONArray ProductArray = new JSONArray(ProductsArray);
                            rproModels.clear();
                            for (int i = 0; i < ProductArray.length(); i++)
                            {
                                String Product = ProductArray.getString(i);
                                JSONObject ProductObject = new JSONObject(Product);
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
                                rproModel.setRes(ProductObject.getString("shop_name"));
                                rproModel.setResID(ProductObject.getString("shop_id"));
                                    rproModel.setOffer(ProductObject.getString("offerprice"));
                                    rproModel.setIsAvailable(ProductObject.getString("status"));
                                    rproModel.setImage(ProductObject.getString("image"));
                                    rproModel.setUnit(ProductObject.getString("units"));
                                    rproModels.add(rproModel);

                            }
                            rproAdapter.renewItems(rproModels);

                        }

                        else
                        {
                            showMsg(msg);
                        }


                    }
                    catch (Exception e)
                    { Log.e("catcherror 1", e + "d"); }
                }
            });
            jRequest.execute();
        }
        catch (JSONException e)
        { e.printStackTrace(); }
    }

    public void getCart() throws JSONException {

                            cartObj = sharedPreferences.getString("cartObj","");
                            String ccTotal = sharedPreferences.getString("ccTotal","");
                            BottomItemView(cartObj,ccTotal);
                            GetCatProducts(Cid);
    }

    public void showMsg(String msg)
    {
        CookieBar.build(this)
                .setMessage(msg)
                .setBackgroundColor(R.color.lite_green)
                .setCookiePosition(CookieBar.BOTTOM)
                .show();
    }

    public void BottomItemView(String Array,String Total) throws JSONException {

        JSONArray UnitArray = new JSONArray(Array);
        String itemCounts = UnitArray.length()+"";
        if(UnitArray.length()>0)
        {
            findViewById(R.id.botttomCartBox2).setVisibility(View.VISIBLE);
            ItemCount.setText(itemCounts+(UnitArray.length()>1?" items":" item"));
            CartTotal.setText("₹"+Total);
        }
        else
        {
            findViewById(R.id.botttomCartBox2).setVisibility(View.GONE);
        }

    }

    public void GoCart(View v)
    {
        Intent i = new Intent(this,CartActivity.class);
        startActivity(i);
    }

    @Override
    public void Unit(String Array, String Pname, String IsVeg, String RID) throws JSONException {
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

        TextView PnameView = findViewById(R.id.pro_name2);
        PnameView.setText(Pname);
        getCart4Unit(Array,Pname,IsVeg,RID);
    }

    public void getCart4Unit(String Array,String Pname, String IsVeg,String RID) throws JSONException {

        findViewById(R.id.res_unit_shimmer_loader2).setVisibility(View.VISIBLE);
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

                            findViewById(R.id.res_unit_shimmer_loader2).setVisibility(View.GONE);

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
                                unitModel.setName(UnitObject.getString("name"));
                                unitModel.setPrice(UnitObject.getString("price"));
                                unitModel.setOffer(UnitObject.getString("offerprice"));
                                unitModel.setRID(RID);
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

    @Override
    public void UpdateCart() throws JSONException {
        getCartNull();
    }

    @Override
    public void Update(String catId) throws JSONException {
        getCartNull();
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


                            JSONArray ProductArray = new JSONArray(ProductsArray);
                            rproModels.clear();
                            for (int i = 0; i < ProductArray.length(); i++)
                            {
                                String Product = ProductArray.getString(i);
                                JSONObject ProductObject = new JSONObject(Product);
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
                                rproModel.setRes(ProductObject.getString("shop_name"));
                                rproModel.setResID(ProductObject.getString("shop_id"));
                                rproModel.setOffer(ProductObject.getString("offerprice"));
                                rproModel.setIsAvailable(ProductObject.getString("status"));
                                rproModel.setImage(ProductObject.getString("image"));
                                rproModel.setUnit(ProductObject.getString("units"));
                                rproModels.add(rproModel);

                            }
                            rproAdapter.renewItems(rproModels);

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


    public void DoneUnitBtn(View view)
    {
        findViewById(R.id.unitBox2).setVisibility(View.GONE);

    }

    public void ClosePop(View view)
    {
        findViewById(R.id.unitBox2).setVisibility(View.GONE);

    }

}