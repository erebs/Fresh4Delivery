package com.seclob.fresh4delivery.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.seclob.fresh4delivery.CartAdapter;
import com.seclob.fresh4delivery.R;
import com.seclob.fresh4delivery.RestaurantsActivity;
import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.model.HisModel;
import com.seclob.fresh4delivery.model.ItemsModel;
import com.seclob.fresh4delivery.model.UnitModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HisAdapter extends RecyclerView.Adapter<HisAdapter.MyViewHolder> {

    Context ctx;
    private List<HisModel> mHisModel = new ArrayList<>();
    HisModel HisModel;
    List<ItemsModel> itemsModels = new ArrayList<>();
    ItemsAdapter itemsAdapter;


    public HisAdapter(Context ctx){
        this.ctx = ctx;

        try {
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    public void renewItems(List<HisModel> list) {
        this.mHisModel = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_view, parent,false);
        return new MyViewHolder(inflate);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        HisModel = mHisModel.get(position);
        holder.Name.setText(mHisModel.get(position).getSname());
        holder.Place.setText(mHisModel.get(position).getAds());
        holder.Dtime.setText(mHisModel.get(position).getDtime());
        holder.Time.setText(mHisModel.get(position).getTime());
        holder.Amt.setText(mHisModel.get(position).getAmt());
        holder.Tax.setText(mHisModel.get(position).getTax());
        holder.Dcharge.setText(mHisModel.get(position).getDcharge());
        holder.Time.setText(mHisModel.get(position).getTime());
        holder.Vagain.setText(mHisModel.get(position).getPayType());

        this.itemsAdapter = new ItemsAdapter(ctx);
        holder.Items.setAdapter(itemsAdapter);
        holder.Items.setNestedScrollingEnabled(false);
        GridLayoutManager mGrid2 = new GridLayoutManager(ctx, 1);
        holder.Items.setLayoutManager(mGrid2);

        JSONArray UnitArray = null;
        try {
            UnitArray = new JSONArray(mHisModel.get(position).getOrdersArray());
            itemsModels.clear();
            for (int i = 0; i < UnitArray.length(); i++)
            {
                String Unit = UnitArray.getString(i);
                JSONObject UnitObject = new JSONObject(Unit);
                ItemsModel itemsModel = new ItemsModel();
                itemsModel.setName(UnitObject.getString("productname")+" ("+UnitObject.getString("unit_name")+")");
                itemsModel.setQty(UnitObject.getString("quantity"));
                itemsModels.add(itemsModel);
            }
            itemsAdapter.renewItems(itemsModels);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.Cview.setVisibility(View.GONE);
        String Sts = mHisModel.get(position).getSts();
        if (Sts.equalsIgnoreCase("New"))
            holder.Cview.setVisibility(View.VISIBLE);
//        if(Sts.equalsIgnoreCase(""))
        holder.Sts.setText(Sts);

        Picasso.get().load(ctx.getString(R.string.app_url)+mHisModel.get(position).getSImage()).placeholder(R.drawable.res_pro_placeholder).into(holder.Image);
        holder.Cbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.Cbtn.setText("Plesae wait...");

                JRequest jRequest;
                try {
                    JSONObject RequestJson = new JSONObject();
                    RequestJson.put("atype", "Sub");
                    RequestJson.put("id",mHisModel.get(position).getID());
                    RequestJson.put("status", "Cancelled");

                    jRequest = new JRequest(RequestJson, "customer/order/status", true, ctx, new JRequest.TaskCompleteListener(){
                        @Override
                        public void onTaskComplete(String result) throws JSONException
                        {
                            holder.Cbtn.setText("Cancel Order");

                            try
                            {
                                JSONObject Res = new JSONObject(result);
                                String sts = Res.getString("sts");
                                String msg = Res.getString("msg");

                                if (sts.equalsIgnoreCase("01"))
                                {
                                    holder.Cview.setVisibility(View.GONE);
                                    holder.Sts.setText("Cancelled");
                                }

                                else
                                {
                                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
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



    @Override
    public int getItemCount() {
        return mHisModel.size();
    }

    public void clear() {
        int size = mHisModel.size();
        mHisModel.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Name, Place, Dtime, Sts, Vagain, Time, Amt, Cbtn,Tax,Dcharge;
        LinearLayout Cview;
        RecyclerView Items;
        ImageView Image;

        public MyViewHolder(View itemView) {
            super(itemView);

            Name = (TextView) itemView.findViewById(R.id.items_shop);
            Place = (TextView) itemView.findViewById(R.id.items_ads);
            Dtime = (TextView) itemView.findViewById(R.id.items_dtime);
            Sts = (TextView) itemView.findViewById(R.id.items_sts);
            Vagain = (TextView) itemView.findViewById(R.id.items_vagain);
            Time = (TextView) itemView.findViewById(R.id.items_time);
            Amt = (TextView) itemView.findViewById(R.id.items_amt);
            Cbtn = (TextView) itemView.findViewById(R.id.items_cBtn);
            Items = itemView.findViewById(R.id.items_array);
            Cview = itemView.findViewById(R.id.items_cview);
            Tax = itemView.findViewById(R.id.items_tax);
            Dcharge = itemView.findViewById(R.id.items_dcharge);
            Image = itemView.findViewById(R.id.his_image);

        }

    }
}