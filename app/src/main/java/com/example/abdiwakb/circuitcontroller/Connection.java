package com.example.abdiwakb.circuitcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class Connection extends Fragment implements View.OnClickListener {

    //Reference to UI Components
    private ListView listView_device;
    private TextView txtView_btStatus;
    private TextView txtView_amount;
    private TextView txtView_status;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;

    public static Handler handler;
    public static BluetoothSocket mmSocket;
    public static CreateConnectThread createConnectThread;
    public static ConnectedThread connectedThread;

    private final static int CONNECTION_STATUS = 1;
    private final static int MESSAGE_READ = 2;

    String percent = "nan";
    String status = "nan";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_connection, container, false);

        //Getting UI component Objects
        txtView_btStatus = (TextView) view.findViewById(R.id.txtView_btStatus);
        txtView_amount = (TextView) view.findViewById(R.id.textView_amount);
        txtView_status = (TextView) view.findViewById(R.id.textView_Status);
        listView_device = (ListView) view.findViewById(R.id.lstView_device);

        Button btn_connect = (Button) view.findViewById(R.id.btn_connect);
        Button btn_disconnect = (Button) view.findViewById(R.id.btn_disconnect);
        Button btn_store = (Button)view.findViewById(R.id.btn_store);

        //Assigning Click Listener
        btn_connect.setOnClickListener(this);
        btn_disconnect.setOnClickListener(this);
        btn_store.setOnClickListener(this);

        listView_device.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                createConnectThread = new CreateConnectThread(bluetoothAdapter,btArray[i].getAddress());
                createConnectThread.start();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Handler Object
        handler = new Handler(Looper.getMainLooper()){

            @Override
            public void handleMessage(Message msg){
                switch(msg.what){
                    case CONNECTION_STATUS:
                        switch(msg.arg1){
                            case 1:
                                txtView_btStatus.setText("Bluetooth Connected");
                                break;
                            case -1:
                                txtView_btStatus.setText("Connection Failed");
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String statusText = msg.obj.toString().replace("/n", "");

                        double moisture = Double.parseDouble(statusText);
                        double moistPercentage = 100 - (moisture/1023)*100;
                        percent = Double.toString(moistPercentage);

                        if(moisture <= 500){
                            status = "HIGH";
                        }
                        else if(moisture <= 750){
                            status = "MEDIUM";
                        }
                        else if(moisture <= 1024){
                            status = "LOW";
                        }
                        else{
                            status = "LOW";
                        }
                        txtView_amount.setText("Amount: "+percent.substring(0, 4) + "%");
                        txtView_status.setText("Status: " + status);


                        //Toast.makeText(MainActivity.this, "DataReceived", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_connect:

                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                btArray = new BluetoothDevice[bt.size()];
                int index = 0;

                if (bt.size() > 0) {
                    for (BluetoothDevice device : bt) {
                        btArray[index] = device;
                        strings[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, strings);
                    listView_device.setAdapter(arrayAdapter);
                }
                break;

            case R.id.btn_disconnect:
                createConnectThread.cancel();
                txtView_btStatus.setText("Bluetooth Disconnected");
                break;

            case R.id.btn_store:
                if(addHistory()){
                    Toast.makeText(getContext(), "New Data Stored Successfully...", Toast.LENGTH_SHORT).show();
                }else{

                    Toast.makeText(getContext(), "Error Recording Data!", Toast.LENGTH_SHORT).show();
                }
                break;

                }
        }

    // Thread for Connection
    public static class CreateConnectThread extends Thread{

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address){

            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();
            //UUID uuid = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
            //final String uuid = "0000112f-0000-1000-8000-00805f9b34fb";
            //UUID uuid = UUID.fromString("0000112f-0000-1000-8000-00805f9b34fb");

            try{
                //tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
                tmp = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);


            } catch (IOException e){
                Log.e("SocketError", e.toString());
            }
            mmSocket = tmp;
        }

        public void run(){
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try{
                mmSocket.connect();
                handler.obtainMessage(CONNECTION_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException){
                Log.e("Status", connectException.toString());
                try{
                    mmSocket.close();
                    handler.obtainMessage(CONNECTION_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException){
                    Log.e("Status", "Unable to Close the Socket");
                }
                return;
            }

           connectedThread = new ConnectedThread(mmSocket);
           connectedThread.run();
        }

        public void cancel(){
            try{
                mmSocket.close();
            }catch (IOException e){}
        }

    }

    // Thread Data Exchange
    public static class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e){}
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes = 0;

            while(true){
                try{
                    buffer[bytes] = (byte)mmInStream.read();
                    String arduinoMsg;
                    if(buffer[bytes] == '\n'){
                        arduinoMsg = new String(buffer, 0, bytes);
                        Log.e("Message", arduinoMsg);
                        handler.obtainMessage(MESSAGE_READ,arduinoMsg).sendToTarget();
                        bytes = 0;
                    }else{
                        bytes++;
                    }
                } catch (IOException e){

                    break;
                }

            }
        }

        public void write(String input){
            byte[] bytes = input.getBytes();
            Log.e("Status", String.valueOf(bytes));
            try{
                mmOutStream.write(bytes);
            } catch (IOException e){
                Log.e("Status", "Unable to Send Data");
            }
        }

    }


    private boolean addHistory(){

        if(status != null){
            SQLiteOpenHelper soilDatabaseHelper = new SoilDatabaseHelper(getContext());
            try{
                SQLiteDatabase db = soilDatabaseHelper.getReadableDatabase();

                //Inserting Default Data
                ContentValues soilHistory = new ContentValues();
                soilHistory.put("Date", "12/07/2021");
                soilHistory.put("MoistureAmount", percent.substring(0,2));
                soilHistory.put("Status", status);

                db.insert("HISTORY", null, soilHistory);

                return true;

            }catch (SQLiteException e){
                return false;
            }

        }
        else {
            return false;
        }


    }

}
