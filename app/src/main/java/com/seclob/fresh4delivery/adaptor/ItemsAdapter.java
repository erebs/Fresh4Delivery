package com.seclob.fresh4delivery.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.seclob.fresh4delivery.R;
import com.seclob.fresh4delivery.model.CategoryModel;
import com.seclob.fresh4delivery.model.ItemsModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder> {

    Context ctx;
    private List<ItemsModel> mItemsModel = new ArrayList<>();
    ItemsModel ItemsModel;


    public ItemsAdapter(Context ctx){
        this.ctx = ctx;
        try {
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    public void renewItems(List<ItemsModel> list) {
        this.mItemsModel = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_layout, parent,false);
        return new MyViewHolder(inflate);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        ItemsModel = mItemsModel.get(position);
        holder.Name.setText(mItemsModel.get(position).getName());
        holder.Qty.setText(mItemsModel.get(position).getQty()+" x");
//        if (mItemsModel.get(position).getIsVeg().equalsIgnoreCase("Veg"))
//            Picasso.get().load(R.drawable.veg).into(holder.Image);
//        else
//            Picasso.get().load(R.drawable.non_veg).into(holder.Image);
    }



    @Override
    public int getItemCount() {
        return mItemsModel.size();
    }

    public void clear() {
        int size = mItemsModel.size();
        mItemsModel.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Name, Qty;
        ImageView Image;

        public MyViewHolder(View itemView) {
            super(itemView);

            Name = (TextView) itemView.findViewById(R.id.items_name);
            Qty = (TextView) itemView.findViewById(R.id.items_qty);
            Image = itemView.findViewById(R.id.items_veg);
        }

    }
}