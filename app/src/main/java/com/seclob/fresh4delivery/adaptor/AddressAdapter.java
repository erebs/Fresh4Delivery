package com.seclob.fresh4delivery.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.seclob.fresh4delivery.R;
import com.seclob.fresh4delivery.libraries.JRequest;
import com.seclob.fresh4delivery.model.AddressModel;
import com.seclob.fresh4delivery.model.CategoryModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {

    Context ctx;
    private List<AddressModel> mAddressModel = new ArrayList<>();
    AddressModel AddressModel;
    private getCategory getcat;
    SharedPreferences sharedPreferences;

    public AddressAdapter(Context ctx){
        this.ctx = ctx;
        sharedPreferences = ctx.getSharedPreferences("NISC",Context.MODE_PRIVATE);

        try {
            this.getcat = ((getCategory) ctx);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    public void renewItems(List<AddressModel> list) {
        this.mAddressModel = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_view, parent,false);
        return new MyViewHolder(inflate);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        AddressModel = mAddressModel.get(position);
        holder.City.setText(mAddressModel.get(position).getName());
        holder.Address.setText(mAddressModel.get(position).getAddress());
        holder.Des.setVisibility(View.GONE);
        if (mAddressModel.get(position).getIsD().equalsIgnoreCase("1"))
        {
            holder.Des.setVisibility(View.VISIBLE);
        }

        if (mAddressModel.get(position).getType().equalsIgnoreCase("Home"))
            holder.Image.setImageDrawable(ctx.getDrawable(R.drawable.ic_home_fill));
        else if (mAddressModel.get(position).getType().equalsIgnoreCase("Work"))
        {            holder.Image.setImageDrawable(ctx.getDrawable(R.drawable.ic_building_fill));}
        else
        {            holder.Image.setImageDrawable(ctx.getDrawable(R.drawable.ic_map_pin_fill));}

        holder.View.setOnClickListener(new View.OnClickListener() {
                @Override
            public void onClick(View v) {


                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("ads_id",mAddressModel.get(position).getID());
                    editor.putString("pincode",mAddressModel.get(position).getPincode());
                    editor.putString("address", mAddressModel.get(position).getAddress());
                    editor.putString("city", mAddressModel.get(position).getName());
                    editor.apply();




                    JRequest jRequest;
                    try {
                        getcat.Cat("Start",0);
                        JSONObject RequestJson = new JSONObject();
                        RequestJson.put("user_id", sharedPreferences.getString("id", ""));
                        RequestJson.put("addressid", sharedPreferences.getString("ads_id", ""));

                        jRequest = new JRequest(RequestJson, "2.0/address/default", true, ctx, new JRequest.TaskCompleteListener(){
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

                                        getcat.Cat("Finish",0);

                                    }

                                    else
                                    {
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

                JRequest jRequest;
                try {
                    getcat.Cat("Start",0);

                    JSONObject RequestJson = new JSONObject();
                    RequestJson.put("user_id", sharedPreferences.getString("id", ""));

                    jRequest = new JRequest(RequestJson, "2.0/address/remove/"+mAddressModel.get(position).getID(), true, ctx, new JRequest.TaskCompleteListener(){
                        @Override
                        public void onTaskComplete(String result) throws JSONException
                        {

                            try
                            {
                                getcat.Cat("Stop",0);

                                JSONObject Res = new JSONObject(result);
                                String sts = Res.getString("sts");
                                String msg = Res.getString("msg");

                                if (sts.equalsIgnoreCase("00"))
                                {

                                }

                                else
                                {

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

    public interface getCategory{
        void Cat(String catId,int pos) throws JSONException;
    }


    @Override
    public int getItemCount() {
        return mAddressModel.size();
    }

    public void clear() {
        int size = mAddressModel.size();
        mAddressModel.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView City,Address,Des;
        LinearLayout View;
        ImageView Image,Remove;

        public MyViewHolder(View itemView) {
            super(itemView);

            City = (TextView) itemView.findViewById(R.id.ads_cty);
            Address = (TextView) itemView.findViewById(R.id.ads_adss);
            Image = itemView.findViewById(R.id.ads_image);
            View = itemView.findViewById(R.id.ads_view);
            Des = itemView.findViewById(R.id.ads_de);
            Remove = itemView.findViewById(R.id.ads_remove);
        }

    }
}