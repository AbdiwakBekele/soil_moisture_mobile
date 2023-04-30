package com.example.abdiwakb.circuitcontroller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Object> deviceList;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textName;

        public ViewHolder(View view){
            super(view);
            textName = view.findViewById(R.id.txtView_device);
        }
    }

    public ListAdapter(Context context, List<Object> deviceList){
        this.context = context;
        this.deviceList = deviceList;
    }

    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        DeviceInfoModel deviceInfoModel = (DeviceInfoModel) deviceList.get(position);
        String deviceName = deviceInfoModel.getDeviceName();
        final String deviceAddress = deviceInfoModel.getDeviceHardwareAddress();

        //Assign Device Name to the Recycler View
        ViewHolder itemholder = (ViewHolder)holder;
        itemholder.textName.setText(deviceName);

        //Return to Main Screen when Clicked
        itemholder.textName.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("deviceAddress", deviceAddress);
                context.startActivity(intent);
            }
        });
    }

    public int getItemCount(){
        int datacount = deviceList.size();
        return datacount;
    }

}
