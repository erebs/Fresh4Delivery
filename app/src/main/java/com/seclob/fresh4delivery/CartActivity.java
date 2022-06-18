package com.seclob.fresh4delivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.ExternalWalletListener;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.seclob.fresh4delivery.adaptor.UnitAdapter;
import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.model.UnitModel;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CartActivity extends AppCompatActivity implements CartAdapter.getUpdate, PaymentResultWithDataListener, ExternalWalletListener {

    TextView CartGTotal, NoteText, CCTotal,CCDcharge,CCTax,CCGTotal;
    SharedPreferences sharedPreferences;
    String cartnote = "<p style='text-align:justify'><span style='color:red; font-weight:bold;'>Note: </span> <span style='color:grey'>If you choose to cancel, you can do it wihtin 60 seconds after placing order. Post which you will be charged 100% cancellation fee.</span></p>";
    String otp="";
    String PayTl ="";
    List<UnitModel> unitModels = new ArrayList<>();
    CartAdapter unitAdapter;
    RecyclerView UnitRecycleview;
    String payType="";
    TextView Htext,Wtext, Dhead,Dtext;
    ImageView Hicon,Wicon;
    LinearLayout Hbox,Wbox;
    EditText Dnote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().hide();

        Hbox = findViewById(R.id.phome_box);
        Hicon = findViewById(R.id.phome_icon);
        Htext = findViewById(R.id.phome_text);
        Wbox = findViewById(R.id.pwork_box);
        Wicon = findViewById(R.id.pwork_icon);
        Wtext = findViewById(R.id.pwork_text);
        Dhead = findViewById(R.id.dhead);
        Dtext = findViewById(R.id.dtext);
        Dnote = findViewById(R.id.dnote);
        Random rand = new Random();
        otp = String.format("%09d", rand.nextInt(10000000));

        CartGTotal = findViewById(R.id.CartGTotal);
        NoteText = findViewById(R.id.NoteText);
        CCTotal = findViewById(R.id.CCTotal);
        CCDcharge = findViewById(R.id.CCDcharge);
        CCTax = findViewById(R.id.CCTax);
        CCGTotal = findViewById(R.id.CCGTotal);
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);

        Checkout.preload(getApplicationContext());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NoteText.setText(Html.fromHtml(cartnote, Html.FROM_HTML_MODE_COMPACT));
        } else {
            NoteText.setText(Html.fromHtml(cartnote));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NoteText.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        UnitRecycleview = findViewById(R.id.CartRecycleview);
        this.unitAdapter = new CartAdapter(this);
        UnitRecycleview.setAdapter(unitAdapter);
        UnitRecycleview.setNestedScrollingEnabled(false);
        GridLayoutManager mGrid2 = new GridLayoutManager(getApplicationContext(), 1);
        UnitRecycleview.setLayoutManager(mGrid2);


        PayType("CoD");
    }

    @Override
    protected void onResume() {
        getCartNull("loader");
        Dhead.setText("Deliver to "+sharedPreferences.getString("city",""));
        Dtext.setText(sharedPreferences.getString("address",""));
        super.onResume();
    }

    public void LocBtn(View view)
    {
        Intent i = new Intent(this, DadsActivity.class);
        startActivity(i);
    }

    public void getCartNull(String loader)
    {
        if (loader.equalsIgnoreCase("loader"))
        {
            findViewById(R.id.cart_act_loader).setVisibility(View.VISIBLE);
            findViewById(R.id.cart_act_main_loader).setVisibility(View.VISIBLE);
        }
        else {
            showMsg("Refreshing Cart...");
        }
        JRequest jRequest;

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("user_id", sharedPreferences.getString("id", ""));

            jRequest = new JRequest(RequestJson, "2.0/getcart", true, this, new JRequest.TaskCompleteListener(){
                @Override
                public void onTaskComplete(String result) throws JSONException
                {
                    findViewById(R.id.cart_act_loader).setVisibility(View.GONE);

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

                            if (Integer.parseInt(Res.getString("cartcount"))<=0) {
                                findViewById(R.id.cart_act_emty).setVisibility(View.VISIBLE);
                            }else {
                                findViewById(R.id.cart_act_emty).setVisibility(View.GONE);
                                findViewById(R.id.cart_act_main_loader).setVisibility(View.GONE);


                            }

                            String cartObj = Res.getString("cart");
                            double ccTotal = Double.parseDouble(Res.getString("total_amount"));
                            double ccDcharge = Double.parseDouble(Res.getString("delivery_charge"));
                            double ccTax = Double.parseDouble(Res.getString("tax_value"));

                            double GTotal = ccTotal+ccDcharge+ccTax;
                            CartGTotal.setText("₹"+GTotal);
                            CCTotal.setText("₹"+ccTotal);
                            CCDcharge.setText("₹"+ccDcharge);
                            CCTax.setText("₹"+ccTax);
                            CCGTotal.setText("₹"+GTotal);
                            PayTl = GTotal+"";
                            JSONArray UnitArray = new JSONArray(cartObj);
                            unitModels.clear();
                            for (int i = 0; i < UnitArray.length(); i++)
                            {
                                String Unit = UnitArray.getString(i);
                                JSONObject UnitObject = new JSONObject(Unit);
                                UnitModel unitModel = new UnitModel();
                                unitModel.setIsCarted(UnitObject.getString("id"));
                                unitModel.setID(UnitObject.getString("unit_id"));
                                unitModel.setPID(UnitObject.getString("product_id"));
                                String unitName = (UnitObject.getString("unitname"));
                                unitName = (unitName.length()>1?" - "+unitName:"");
                                unitModel.setName(UnitObject.getString("productname")+unitName);
                                unitModel.setPrice(UnitObject.getString("price"));
                                unitModel.setQty(UnitObject.getString("quantity"));
                                unitModel.setOffer(UnitObject.getString("offerprice"));
                                unitModel.setIsVeg("1");
                                unitModels.add(unitModel);
                            }
                            unitAdapter.renewItems(unitModels);

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

    @Override
    public void Update(String catId) throws JSONException {
        getCartNull(catId);
    }

    public void GoBack(View view)
    {
        super.onBackPressed();
    }

    public void CreatActivuty(View view)
    {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();


            co.setKeyID("rzp_live_y71dw8NWTEev44");


            String [] Point;
            Point = PayTl.split("\\.");
            String aPoint = (Point[1].length()>1?Point[1]:Point[1]+"0");
            aPoint = (aPoint.length()>2?aPoint.substring(0,2):aPoint);

            try {
                JSONObject options = new JSONObject();
                options.put("name", "Fresh4Delivery");
                options.put("description", "#FRSH"+otp);
                options.put("send_sms_hash",true);
                options.put("allow_rotation", true);
                //You can omit the image option to fetch the image from dashboard
                options.put("currency", "INR");
                options.put("amount", Point[0]+""+aPoint);

                JSONObject preFill = new JSONObject();
                preFill.put("email", sharedPreferences.getString("email",""));
                preFill.put("contact", sharedPreferences.getString("phone",""));

                options.put("prefill", preFill);

                co.open(activity, options);
            } catch (Exception e) {
                Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
                e.printStackTrace();
            }



    }

    public void PlaceBtn(View view)
    {
        if(payType.equalsIgnoreCase("CoD"))
        {
            placeOrder("");
        }else
        {
            startPayment();
        }
    }


    @Override
    public void onExternalWalletSelected(String s, PaymentData paymentData) {
        try{

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try{
            placeOrder(paymentData.getData().toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        try{
        showMsg("Payment Failed!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void HomeBtn(View view)
    {PayType("CoD");}

    public void WorkBtn(View view)
    {PayType("Online");}

    public void PayType(String Type)
    {
        payType = Type;
        Hbox.setBackground(getDrawable(R.drawable.input_dis));
        Wbox.setBackground(getDrawable(R.drawable.input_dis));
        Hicon.setColorFilter(ContextCompat.getColor(this, R.color.grey_text), android.graphics.PorterDuff.Mode.SRC_IN);
        Wicon.setColorFilter(ContextCompat.getColor(this, R.color.grey_text), android.graphics.PorterDuff.Mode.SRC_IN);
        Htext.setTextColor(getResources().getColor(R.color.grey_text));
        Wtext.setTextColor(getResources().getColor(R.color.grey_text));

        if(Type.equalsIgnoreCase("CoD"))
        {
            Hbox.setBackground(getDrawable(R.drawable.input_sel));
            Hicon.setColorFilter(ContextCompat.getColor(this, R.color.lite_green), android.graphics.PorterDuff.Mode.SRC_IN);
            Htext.setTextColor(getResources().getColor(R.color.lite_green));
        }
        else if(Type.equalsIgnoreCase("Online"))
        {
            Wbox.setBackground(getDrawable(R.drawable.input_sel));
            Wicon.setColorFilter(ContextCompat.getColor(this, R.color.lite_green), android.graphics.PorterDuff.Mode.SRC_IN);
            Wtext.setTextColor(getResources().getColor(R.color.lite_green));
        }


    }

    public void RefundBtn(View view) {
        Intent i = new Intent(getApplicationContext(), WebActivity.class);
        i.putExtra("url",getString(R.string.site_url)+"refund");
        i.putExtra("title","Refund Policy");
        startActivity(i);
    }

    public void placeOrder(String paydetails)
    {
        LinearLayout Loader = findViewById(R.id.progress_horizontal);
        Loader.setVisibility(View.VISIBLE);
        JRequest jRequest;

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("user_id", sharedPreferences.getString("id", ""));
            RequestJson.put("paytype", payType);
            RequestJson.put("promocode", "");
            RequestJson.put("notes", Dnote.getText().toString());
            if (payType.equalsIgnoreCase("CoD"))
            RequestJson.put("pay_status", "Pending");
            else
            {
                RequestJson.put("pay_status", "Success");
                RequestJson.put("details", paydetails);
            }

            jRequest = new JRequest(RequestJson, "2.0/placeorder", true, this, new JRequest.TaskCompleteListener(){
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
                            editor.putString("cartObj", "");
                            editor.putString("ccTotal", "");
                            editor.apply();
                            Intent i = new Intent(getApplicationContext(), SuccessActivity.class);
                            startActivity(i);
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