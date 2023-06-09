package com.example.abdiwakb.circuitcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SelectDeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device);

        RecyclerView RCV_deviceList = findViewById(R.id.deviceList);

        //Initialize Bluetooth Adapter
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        List<Object> deviceList = new ArrayList<>();

        for(BluetoothDevice device : pairedDevices){
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress();
            DeviceInfoModel deviceInfoModel = new DeviceInfoModel(deviceName, deviceHardwareAddress);
            deviceList.add(deviceInfoModel);
        }

        //Setting Up RecyclerView
        RCV_deviceList.setLayoutManager( new LinearLayoutManager(this));

        //Connecting to Data Source and Content Layout
        ListAdapter listAdapter = new ListAdapter(this, deviceList);
        RCV_deviceList.setAdapter(listAdapter);
    }
}
