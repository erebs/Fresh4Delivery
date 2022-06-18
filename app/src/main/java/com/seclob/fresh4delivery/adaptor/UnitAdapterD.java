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
import com.seclob.fresh4delivery.model.UnitModel;
import com.seclob.fresh4delivery.model.UnitModelD;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UnitAdapterD extends RecyclerView.Adapter<UnitAdapterD.MyViewHolder> {

    private LayoutInflater inflater;
    Context ctx;
    private List<UnitModelD> mUnitModelD = new ArrayList<>();
    UnitModelD unitModelD;
    String catallID;
    int row_index=0;
    SharedPreferences sharedPreferences;

    public UnitAdapterD(Context ctx){
        this.ctx = ctx;
        try {
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    public void renewItems(List<UnitModelD> list) {
        this.mUnitModelD = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_layout_shimmer, parent,false);
        return new MyViewHolder(inflate);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            unitModelD = mUnitModelD.get(position);

            holder.ID.setText(mUnitModelD.get(position).getID());
    }


    @Override
    public int getItemCount() {
        return mUnitModelD.size();
    }

    public void clear() {
        int size = mUnitModelD.size();
        mUnitModelD.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder{



        TextView ID;

        public MyViewHolder(View itemView) {
            super(itemView);



            ID =  itemView.findViewById(R.id.unit_dummy_id);

        }

    }

    public void showMsg(String msg)
    {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

}