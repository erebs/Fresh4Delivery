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

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.seclob.fresh4delivery.CatlistActivity;
import com.seclob.fresh4delivery.R;
import com.seclob.fresh4delivery.RestaurantsActivity;
import com.seclob.fresh4delivery.model.HisModel;
import com.seclob.fresh4delivery.model.ItemsModel;
import com.seclob.fresh4delivery.model.MainCategoryModel;
import com.seclob.fresh4delivery.model.SearchModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    Context ctx;
    private List<SearchModel> mSearchModel = new ArrayList<>();
    SearchModel SearchModel;
    ItemsAdapter itemsAdapter;


    public SearchAdapter(Context ctx){
        this.ctx = ctx;

        try {
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    public void renewItems(List<SearchModel> list) {
        this.mSearchModel = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_view, parent,false);
        return new MyViewHolder(inflate);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        SearchModel = mSearchModel.get(position);
        holder.Name.setText(mSearchModel.get(position).getName());
        holder.Type.setText(mSearchModel.get(position).getType());
        Picasso.get().load(ctx.getString(R.string.app_url)+mSearchModel.get(position).getImage()).placeholder(R.drawable.res_pro_placeholder).into(holder.Image);

        holder.Cview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              if (mSearchModel.get(position).getType().equalsIgnoreCase("Restaurant") || mSearchModel.get(position).getType().equalsIgnoreCase("Supermarket"))
              {
                  if(mSearchModel.get(position).getStatus().equalsIgnoreCase("Active")) {

                      Intent i = new Intent(ctx, RestaurantsActivity.class);
                      i.putExtra("shop_id",mSearchModel.get(position).getID());
                      i.putExtra("type",mSearchModel.get(position).getType());
                      ctx.startActivity(i);

                  }
              }else
              {
                  Intent i = new Intent(ctx, CatlistActivity.class);
                  i.putExtra("cid",mSearchModel.get(position).getID());
                  i.putExtra("cname",mSearchModel.get(position).getName());
                  ctx.startActivity(i);
              }

            }
        });

    }



    @Override
    public int getItemCount() {
        return mSearchModel.size();
    }

    public void clear() {
        int size = mSearchModel.size();
        mSearchModel.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Name, Type;
        LinearLayout Cview;
        ImageView Image;

        public MyViewHolder(View itemView) {
            super(itemView);

            Name = (TextView) itemView.findViewById(R.id.search_name);
            Type = itemView.findViewById(R.id.search_type);
            Cview = itemView.findViewById(R.id.search_view);
            Image = itemView.findViewById(R.id.search_image);

        }

    }
}