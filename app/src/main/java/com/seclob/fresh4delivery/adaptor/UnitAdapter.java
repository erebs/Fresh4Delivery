package com.seclob.fresh4delivery.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.seclob.fresh4delivery.R;
import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.model.CategoryModel;
import com.seclob.fresh4delivery.model.UnitModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    Context ctx;
    private List<UnitModel> mUnitModel = new ArrayList<>();
    UnitModel unitModel;
    String catallID;
    int row_index=0;
    private getUpdate update;
    SharedPreferences sharedPreferences;

    public UnitAdapter(Context ctx){
        this.ctx = ctx;
        sharedPreferences = ctx.getSharedPreferences("NISC",Context.MODE_PRIVATE);
        try {
            this.update = ((getUpdate) ctx);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    public void renewItems(List<UnitModel> list) {
        this.mUnitModel = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_layout, parent,false);
        return new MyViewHolder(inflate);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            unitModel = mUnitModel.get(position);
        holder.Name.setText(mUnitModel.get(position).getName());
        holder.Price.setText("₹"+mUnitModel.get(position).getPrice());
        holder.Offer.setText("₹"+mUnitModel.get(position).getOffer());
        holder.Offer.setPaintFlags(holder.Offer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if (mUnitModel.get(position).getIsVeg().equalsIgnoreCase("Veg"))
            Picasso.get().load(R.drawable.veg).into(holder.IsVeg);
        else
            Picasso.get().load(R.drawable.non_veg).into(holder.IsVeg);

        if(mUnitModel.get(position).getIsCarted().equalsIgnoreCase("1")){

                holder.QtyView.setVisibility(View.VISIBLE);
                holder.Add.setVisibility(View.GONE);
                holder.Qty.setText(mUnitModel.get(position).getQty());
        }
        else
        {
            holder.QtyView.setVisibility(View.GONE);
            holder.Add.setVisibility(View.VISIBLE);

        }

        holder.Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    holder.AddLoader.setVisibility(View.VISIBLE);
                    JRequest jRequest;
                    try {
                        JSONObject RequestJson = new JSONObject();
                        RequestJson.put("pincode", sharedPreferences.getString("pincode", ""));
                        RequestJson.put("type", sharedPreferences.getString("type", ""));
                        RequestJson.put("user_id", sharedPreferences.getString("id", ""));
                        RequestJson.put("product_id", mUnitModel.get(position).getPID());
                        if (mUnitModel.get(position).getRID().length()>0)
                            RequestJson.put("shop_id", mUnitModel.get(position).getRID());
                        else
                            RequestJson.put("shop_id", sharedPreferences.getString("shop_id", ""));
                        RequestJson.put("quantity", "1");
                        RequestJson.put("unit_id", mUnitModel.get(position).getID());

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
                                        update.Update("");
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
                        RequestJson.put("product_id", mUnitModel.get(position).getPID());
                        RequestJson.put("unit_id",  mUnitModel.get(position).getID());

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
                                        update.Update("");
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
                    RequestJson.put("product_id", mUnitModel.get(position).getPID());
                    RequestJson.put("unit_id",  mUnitModel.get(position).getID());

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
                                    update.Update("");

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



    }

    public interface getUpdate{
        void Update(String catId) throws JSONException;
    }


    @Override
    public int getItemCount() {
        return mUnitModel.size();
    }

    public void clear() {
        int size = mUnitModel.size();
        mUnitModel.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Name,Price,Offer,Qty,Add;
        LinearLayout QtyView;
        ImageView IsVeg,Plus,Minus;
        ProgressBar AddLoader;


        public MyViewHolder(View itemView) {
            super(itemView);

            Name = (TextView) itemView.findViewById(R.id.res_unit_name);
            Price = (TextView) itemView.findViewById(R.id.res_unit_price);
            Offer = (TextView) itemView.findViewById(R.id.res_unit_offer);
            Qty = (TextView) itemView.findViewById(R.id.res_unit_qty);
            Add = (TextView) itemView.findViewById(R.id.res_unit_addbtn);
            QtyView = itemView.findViewById(R.id.res_unit_qtyview);
            IsVeg =  itemView.findViewById(R.id.res_unit_isveg);
            Plus =  itemView.findViewById(R.id.res_unit_add);
            Minus =  itemView.findViewById(R.id.res_unit_minus);
            AddLoader = itemView.findViewById(R.id.res_pro_unit_loader);

        }

    }

    public void showMsg(String msg)
    {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

}