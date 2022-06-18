package com.seclob.fresh4delivery.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.seclob.fresh4delivery.model.CategoryModel;
import com.seclob.fresh4delivery.R;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    Context ctx;
    private List<CategoryModel> mCategoryModel = new ArrayList<>();
    CategoryModel CategoryModel;
    String catallID;
    int row_index=0;
    private getCategory getcat;


    public CategoryAdapter(Context ctx){
        this.ctx = ctx;
        try {
            this.getcat = ((getCategory) ctx);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    public void renewItems(List<CategoryModel> list) {
        this.mCategoryModel = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent,false);
        return new MyViewHolder(inflate);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            CategoryModel = mCategoryModel.get(position);
        holder.Name.setText(mCategoryModel.get(position).getName());
            holder.BG.setOnClickListener(new View.OnClickListener() {
                @Override
            public void onClick(View v) {
                row_index=position;
                notifyDataSetChanged();

                    try {
                        getcat.Cat(mCategoryModel.get(position).getID(),position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
        });

        if(position==CategoryModel.getPos()){
            holder.BG.setBackgroundResource(R.drawable.cat_bg_selected);
        }
        else
        {
            holder.BG.setBackgroundResource(R.drawable.cat_bg);
        }

    }

    public interface getCategory{
        void Cat(String catId,int pos) throws JSONException;
    }


    @Override
    public int getItemCount() {
        return mCategoryModel.size();
    }

    public void clear() {
        int size = mCategoryModel.size();
        mCategoryModel.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Name;
        LinearLayout BG;

        public MyViewHolder(View itemView) {
            super(itemView);

            Name = (TextView) itemView.findViewById(R.id.cat_name);
            BG = itemView.findViewById(R.id.cat_bg);
        }

    }
}