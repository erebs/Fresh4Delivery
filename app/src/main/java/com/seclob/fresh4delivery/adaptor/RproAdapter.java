package com.seclob.fresh4delivery.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.seclob.fresh4delivery.LoginActivity;
import com.seclob.fresh4delivery.R;
import com.seclob.fresh4delivery.RegisterActivity;
import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.model.CategoryModel;
import com.seclob.fresh4delivery.model.RproModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RproAdapter extends RecyclerView.Adapter<RproAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    Context ctx;
    private List<RproModel> mRproModel = new ArrayList<>();
    RproModel RproModel;
    int row_index=0;
    private getUnit getunit;
    private getUpdate getUpdate;
    SharedPreferences sharedPreferences;

    public RproAdapter(Context ctx){
        this.ctx = ctx;
        sharedPreferences = ctx.getSharedPreferences("NISC",Context.MODE_PRIVATE);

        try {
            this.getunit = ((getUnit) ctx);
            this.getUpdate = ((getUpdate) ctx);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    public void renewItems(List<RproModel> list) {
        this.mRproModel = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.res_products, parent,false);
        return new MyViewHolder(inflate);
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            RproModel = mRproModel.get(position);
        holder.Name.setText(mRproModel.get(position).getName());
        holder.Price.setText("₹"+mRproModel.get(position).getPrice());
        holder.Offer.setText("₹"+mRproModel.get(position).getOffer());
        holder.Offer.setPaintFlags(holder.Offer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.Res.setVisibility(View.GONE);
        if(mRproModel.get(position).getRes().length()>0)
            holder.Res.setVisibility(View.VISIBLE);
        holder.Res.setText("From "+mRproModel.get(position).getRes());

        holder.Available.setVisibility(View.GONE);
        if(mRproModel.get(position).getDesc().length()>0)
            holder.Available.setVisibility(View.VISIBLE);
        holder.Available.setText(mRproModel.get(position).getDesc());

        if (mRproModel.get(position).getIsVeg().equalsIgnoreCase("Veg"))
            Picasso.get().load(R.drawable.veg).into(holder.IsVeg);
        else
            Picasso.get().load(R.drawable.non_veg).into(holder.IsVeg);

        Picasso.get().load(ctx.getString(R.string.app_url)+mRproModel.get(position).getImage()).placeholder(R.drawable.res_pro_placeholder).into(holder.Image);

        holder.ModifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mRproModel.get(position).getHasUnit().equalsIgnoreCase("1"))
                {
                    try {
                        getunit.Unit(mRproModel.get(position).getUnit(),mRproModel.get(position).getName(),mRproModel.get(position).getIsVeg(),mRproModel.get(position).getResID());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

            holder.Add.setOnClickListener(new View.OnClickListener() {
                @Override
            public void onClick(View v) {

                if(mRproModel.get(position).getHasUnit().equalsIgnoreCase("1"))
                {
                    try {
                        getunit.Unit(mRproModel.get(position).getUnit(),mRproModel.get(position).getName(),mRproModel.get(position).getIsVeg(),mRproModel.get(position).getResID());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else
                {
                    holder.AddLoader.setVisibility(View.VISIBLE);
                        JRequest jRequest;
                        try {
                            JSONObject RequestJson = new JSONObject();
                            RequestJson.put("pincode", sharedPreferences.getString("pincode", ""));
                            RequestJson.put("type", sharedPreferences.getString("type", ""));
                            RequestJson.put("user_id", sharedPreferences.getString("id", ""));
                            RequestJson.put("product_id", mRproModel.get(position).getID());
                            if (mRproModel.get(position).getResID().length()>0)
                                RequestJson.put("shop_id", mRproModel.get(position).getResID());
                            else
                            RequestJson.put("shop_id", sharedPreferences.getString("shop_id", ""));
                            RequestJson.put("quantity", "1");
                            RequestJson.put("unit_id", "0");

                            jRequest = new JRequest(RequestJson, "customer/addtocart", true, ctx, new JRequest.TaskCompleteListener(){
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
                                            getUpdate.UpdateCart();
                                            String quantity = Res.getString("qty");
                                            holder.Add.setVisibility(View.GONE);
                                            holder.QtyView.setVisibility(View.VISIBLE);
                                            holder.Qty.setText(quantity);



                                        }

                                        else
                                        {
                                            showMsg(msg);
                                        }
                                        holder.AddLoader.setVisibility(View.GONE);


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


        });


        holder.Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(holder.Qty.getText().toString()) >= 10) {

                    showMsg("Maximum order quantity exceeded!");

                } else {
                    holder.AddLoader.setVisibility(View.VISIBLE);
                    JRequest jRequest;
                    try {
                        JSONObject RequestJson = new JSONObject();
                        RequestJson.put("atype", "Add");
                        RequestJson.put("user_id", sharedPreferences.getString("id", ""));
                        RequestJson.put("product_id", mRproModel.get(position).getID());
                        RequestJson.put("unit_id", "0");

                        jRequest = new JRequest(RequestJson, "cart/changequantity2", true, ctx, new JRequest.TaskCompleteListener() {
                            @Override
                            public void onTaskComplete(String result) throws JSONException {
                                holder.AddLoader.setVisibility(View.GONE);

                                try {
                                    JSONObject Res = new JSONObject(result);
                                    String sts = Res.getString("sts");
                                    String msg = Res.getString("msg");

                                    if (sts.equalsIgnoreCase("01")) {
                                        String quantity = Res.getString("qty");
                                        getUpdate.UpdateCart();
                                        holder.Add.setVisibility(View.GONE);
                                        holder.QtyView.setVisibility(View.VISIBLE);
                                        holder.Qty.setText(quantity);



                                    } else {
                                        showMsg(msg);
                                    }


                                } catch (Exception e) {
                                    Log.e("catcherror", e + "d");
                                }
                            }
                        });
                        jRequest.execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        });

        holder.Minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.AddLoader.setVisibility(View.VISIBLE);
                JRequest jRequest;
                try {
                    JSONObject RequestJson = new JSONObject();
                    RequestJson.put("atype", "Sub");
                    RequestJson.put("user_id", sharedPreferences.getString("id", ""));
                    RequestJson.put("product_id", mRproModel.get(position).getID());
                    RequestJson.put("unit_id", "0");

                    jRequest = new JRequest(RequestJson, "cart/changequantity2", true, ctx, new JRequest.TaskCompleteListener(){
                        @Override
                        public void onTaskComplete(String result) throws JSONException
                        {
                            holder.AddLoader.setVisibility(View.GONE);

                            try
                            {
                                JSONObject Res = new JSONObject(result);
                                String sts = Res.getString("sts");
                                String msg = Res.getString("msg");

                                if (sts.equalsIgnoreCase("01"))
                                {
                                    getUpdate.UpdateCart();
                                    String quantity = Res.getString("qty");
                                    if (quantity.equalsIgnoreCase("0"))
                                    {

                                        holder.Add.setVisibility(View.VISIBLE);
                                        holder.QtyView.setVisibility(View.GONE);
                                    }else
                                    {
                                        holder.Add.setVisibility(View.GONE);
                                        holder.QtyView.setVisibility(View.VISIBLE);
                                        holder.Qty.setText(quantity);
                                    }



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



        });

        if(mRproModel.get(position).getIsCarted().equalsIgnoreCase("1")){

            if (mRproModel.get(position).getHasUnit().equalsIgnoreCase("1"))
            {
                holder.ModifyBtn.setVisibility(View.VISIBLE);
                holder.Add.setVisibility(View.GONE);
                holder.QtyView.setVisibility(View.GONE);
            }else
            { holder.QtyView.setVisibility(View.VISIBLE);
                holder.Add.setVisibility(View.GONE);
                holder.ModifyBtn.setVisibility(View.GONE);
                holder.Qty.setText(mRproModel.get(position).getCartedQty());}


        }
        else
        {
            holder.QtyView.setVisibility(View.GONE);
            holder.Add.setVisibility(View.VISIBLE);
            holder.ModifyBtn.setVisibility(View.GONE);

        }

    }

    public interface getUnit{
        void Unit(String Array,String Pname,String IsVeg, String RID) throws JSONException;
    }

    public interface getUpdate{
        void UpdateCart() throws JSONException;
    }


    @Override
    public int getItemCount() {
        return mRproModel.size();
    }

    public void clear() {
        int size = mRproModel.size();
        mRproModel.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Name, Price, Offer, Available, Add, Qty, ModifyBtn,Res;
        ImageView Image, IsVeg, Plus, Minus;
        LinearLayout QtyView;
        ProgressBar AddLoader;

        public MyViewHolder(View itemView) {
            super(itemView);

            Name = (TextView) itemView.findViewById(R.id.res_pro_name);
            Image = itemView.findViewById(R.id.res_pro_image);
            IsVeg = itemView.findViewById(R.id.res_pro_isveg);
            Plus = itemView.findViewById(R.id.res_pro_add);
            Minus = itemView.findViewById(R.id.res_pro_minus);
            Price = itemView.findViewById(R.id.res_pro_price);
            Available = itemView.findViewById(R.id.res_pro_isavailable);
            Add = itemView.findViewById(R.id.res_pro_addbtn);
            Qty = itemView.findViewById(R.id.res_pro_qty);
            Offer = itemView.findViewById(R.id.res_pro_offer);
            QtyView = itemView.findViewById(R.id.res_pro_qtyview);
            AddLoader = itemView.findViewById(R.id.res_pro_add_loader);
            ModifyBtn =itemView.findViewById(R.id.res_pro_modify);
            Res =itemView.findViewById(R.id.res_res_name);
        }

    }

    public void showMsg(String msg)
    {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

}