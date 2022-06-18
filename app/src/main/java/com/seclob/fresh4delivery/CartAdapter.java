package com.seclob.fresh4delivery;

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

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.seclob.fresh4delivery.adaptor.UnitAdapter;
import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.model.UnitModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    Context ctx;
    private List<UnitModel> mUnitModel = new ArrayList<>();
    UnitModel unitModel;
    String catallID;
    int row_index=0;
    private getUpdate update;
    SharedPreferences sharedPreferences;



    public CartAdapter(Context ctx){
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

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent,false);
        return new MyViewHolder(inflate);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            unitModel = mUnitModel.get(position);
        holder.Name.setText(mUnitModel.get(position).getName());
        holder.Qty.setText(mUnitModel.get(position).getQty());
        holder.Price.setText("₹"+mUnitModel.get(position).getPrice());
        holder.Offer.setText("₹"+mUnitModel.get(position).getOffer());
        holder.Offer.setPaintFlags(holder.Offer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


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
                                        holder.Qty.setText(quantity);

                                        update.Update("refresh");


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

                                    String quantity = Res.getString("qty");
                                    if (quantity.equalsIgnoreCase("0"))
                                    {
                                        update.Update("loader");

                                    }else
                                    {
                                        holder.Qty.setText(quantity);
                                        update.Update("refresh");

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


        holder.Remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.RemoveLoader.setVisibility(View.VISIBLE);
                JRequest jRequest;
                try {
                    JSONObject RequestJson = new JSONObject();
                    RequestJson.put("cartid", mUnitModel.get(position).getIsCarted());

                    jRequest = new JRequest(RequestJson, "cart/remove", true, ctx, new JRequest.TaskCompleteListener(){
                        @Override
                        public void onTaskComplete(String result) throws JSONException
                        {
                            holder.RemoveLoader.setVisibility(View.GONE);

                            try
                            {
                                JSONObject Res = new JSONObject(result);
                                String sts = Res.getString("sts");
                                String msg = Res.getString("msg");

                                if (sts.equalsIgnoreCase("00"))
                                {
                                    update.Update("loader");
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

        TextView Name,Price,Offer,Qty;
        ImageView Plus,Minus,Remove;
        ProgressBar AddLoader, RemoveLoader;
        LinearLayout CartCard;

        public MyViewHolder(View itemView) {
            super(itemView);

            Name = (TextView) itemView.findViewById(R.id.cart_name);
            Price = (TextView) itemView.findViewById(R.id.cart_price);
            Offer = (TextView) itemView.findViewById(R.id.cart_offer);
            Qty = (TextView) itemView.findViewById(R.id.cart_qty);
            Plus =  itemView.findViewById(R.id.cart_add);
            Minus =  itemView.findViewById(R.id.cart_minus);
            Remove =  itemView.findViewById(R.id.cart_remove);
            AddLoader = itemView.findViewById(R.id.cart_loader);
            RemoveLoader = itemView.findViewById(R.id.cart_loader_remove);
            CartCard = itemView.findViewById(R.id.cart_card);

        }

    }

    public void showMsg(String msg)
    {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

}