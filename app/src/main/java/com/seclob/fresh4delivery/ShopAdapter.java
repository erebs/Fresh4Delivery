package com.seclob.fresh4delivery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.seclob.fresh4delivery.model.ShopModel;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    Context ctx;
    private List<ShopModel> mShopModel = new ArrayList<>();
    ShopModel shopModel;
    ColorMatrix matrix = new ColorMatrix();
    ColorMatrixColorFilter filter;

    public ShopAdapter(Context ctx){
        this.ctx = ctx;
        matrix.setSaturation(0);
        filter = new ColorMatrixColorFilter(matrix);

        try {
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    public void renewItems(List<ShopModel> list) {
        this.mShopModel = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.res_single, parent,false);
        return new MyViewHolder(inflate);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            shopModel = mShopModel.get(position);
        holder.Name.setText(mShopModel.get(position).getName());
        holder.Rating.setText(mShopModel.get(position).getRating());
        holder.Dtime.setText("Delivers in "+mShopModel.get(position).getDtime());
        Picasso.get().load(ctx.getString(R.string.app_url)+mShopModel.get(position).getImage()).placeholder(R.drawable.ph_bnnr).into(holder.Image);
        if(mShopModel.get(position).getStatus().equalsIgnoreCase("Not")) {
            holder.Image.setColorFilter(filter);
            holder.NotAct.setVisibility(View.VISIBLE);
            holder.ActTxt.setText("Opens at "+mShopModel.get(position).getOpenTime()+" AM");
            holder.ActBox.setVisibility(View.VISIBLE);
            String des = mShopModel.get(position).getDes();
            des = (des.length()>32?des.substring(0,32):des);
            holder.Des.setText(des+"...");
            holder.Des.setLines(1);
        }else {
            String des = mShopModel.get(position).getDes();
            des = (des.length()>64?des.substring(0,64):des);
            holder.Des.setText(des+"...");
        }

        holder.Click.setOnClickListener(new View.OnClickListener() {
                @Override
            public void onClick(View v) {

                    if(mShopModel.get(position).getStatus().equalsIgnoreCase("Active")) {

                        Intent i = new Intent(ctx,RestaurantsActivity.class);
                        i.putExtra("shop_id",mShopModel.get(position).getID());
                        i.putExtra("type",mShopModel.get(position).getType());
                        ctx.startActivity(i);

                    }

                }
        });



    }


    @Override
    public int getItemCount() {
        return mShopModel.size();
    }

    public void clear() {
        int size = mShopModel.size();
        mShopModel.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Name,Rating,Dtime,Des,ActTxt;
        ImageView Image;
        LinearLayout Click,NotAct,ActBox;

        public MyViewHolder(View itemView) {
            super(itemView);

            Name = (TextView) itemView.findViewById(R.id.shop_name);
            Rating = (TextView) itemView.findViewById(R.id.shop_rating);
            Dtime = (TextView) itemView.findViewById(R.id.shop_dtime);
            Des = (TextView) itemView.findViewById(R.id.shop_des);
            Image = itemView.findViewById(R.id.shop_image);
            Click = itemView.findViewById(R.id.shop_click);
            NotAct = itemView.findViewById(R.id.shop_notact);
            ActBox = itemView.findViewById(R.id.shop_actbox);
            ActTxt = itemView.findViewById(R.id.shop_acttext);
        }

    }
}