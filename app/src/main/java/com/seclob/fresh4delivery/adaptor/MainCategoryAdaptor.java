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

import androidx.recyclerview.widget.RecyclerView;

import com.seclob.fresh4delivery.CatlistActivity;
import com.seclob.fresh4delivery.R;
import com.seclob.fresh4delivery.model.MainCategoryModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainCategoryAdaptor extends RecyclerView.Adapter<MainCategoryAdaptor.MyViewHolder>
{
    Context ctx;
    private List<MainCategoryModel> mainCategoryModels = new ArrayList<>();
    MainCategoryModel mainCategoryModel;

    public MainCategoryAdaptor(Context ctx)
    {
        this.ctx = ctx;
    }

    public void renewItems(List<MainCategoryModel> list)
    {
        this.mainCategoryModels = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_category_view, parent,false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position)
    {
            mainCategoryModel = mainCategoryModels.get(position);
            holder.Name.setText(mainCategoryModel.getName().toLowerCase(Locale.ROOT));
        Picasso.get().load(mainCategoryModel.getImage()).placeholder(R.drawable.res_pro_placeholder).into(holder.Image);
            holder.Click.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                    {
                        Intent i = new Intent(ctx, CatlistActivity.class);
                        i.putExtra("cid",mainCategoryModels.get(position).getId());
                        i.putExtra("cname",mainCategoryModels.get(position).getName());
                        ctx.startActivity(i);
                    }
            });
    }

    @Override
    public int getItemCount()
    {
        return mainCategoryModels.size();
    }

    public void clear()
    {
        int size = mainCategoryModels.size();
        mainCategoryModels.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView Name;
        ImageView Image;
        LinearLayout Click;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            Name = itemView.findViewById(R.id.main_cat_name);
            Image = itemView.findViewById(R.id.main_cat_image);
            Click = itemView.findViewById(R.id.main_cat_click);
        }

    }
}