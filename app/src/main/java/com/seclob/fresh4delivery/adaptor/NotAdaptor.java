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
import com.seclob.fresh4delivery.model.MainCategoryModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotAdaptor extends RecyclerView.Adapter<NotAdaptor.MyViewHolder>
{
    Context ctx;
    private List<MainCategoryModel> mainCategoryModels = new ArrayList<>();
    MainCategoryModel mainCategoryModel;

    public NotAdaptor(Context ctx)
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
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.not_layout, parent,false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position)
    {
            mainCategoryModel = mainCategoryModels.get(position);
        holder.Head.setText(mainCategoryModel.getName());
        holder.Cont.setText(mainCategoryModel.getType());

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
        TextView Head,Cont;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            Head = itemView.findViewById(R.id.not_head);
            Cont = itemView.findViewById(R.id.not_cont);
        }

    }
}