package com.example.abdiwakb.circuitcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private String deviceAddress = null;
    public static Handler handler;
    public static BluetoothSocket mmSocket;
    public static CreateConnectThread createConnectThread;
    public static ConnectedThread connectedThread;

    private final static int CONNECTION_STATUS = 1;
    private final static int MESSAGE_READ = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing UI For Bluetooth Connection
        final TextView txtView_bluetoothStatus = (TextView)findViewById(R.id.txtView_bluetoothStatus);

        Button btn_connect = (Button)findViewById(R.id.btn_connnect);
        Button btn_disconnect = (Button)findViewById(R.id.btn_disconnect);

        //Initializing UI For LED Control
        final TextView txtView_ledStatus = (TextView)findViewById(R.id.txtView_ledStatus);
        Button btn_on = (Button)findViewById(R.id.btn_on);
        Button btn_off = (Button)findViewById(R.id.btn_off);
        Button btn_blink = (Button)findViewById(R.id.btn_blink);

        //For Connect Button
        btn_connect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SelectDeviceActivity.class);
                startActivity(intent);
            }
        });

        //For Disconnect Button
        btn_disconnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                createConnectThread.cancel();
                txtView_bluetoothStatus.setText("Bluetooth is Disconnected");
            }
        });

        //Get Device Address Information
        deviceAddress = getIntent().getStringExtra("deviceAddress");

        //if Device Address is Found
        if(deviceAddress != null){
            txtView_bluetoothStatus.setText("Connecting..." + deviceAddress);
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter, deviceAddress);
            createConnectThread.start();
        }

        //Handler Object
        handler = new Handler(Looper.getMainLooper()){

            @Override
            public void handleMessage(Message msg){
                switch(msg.what){
                    case CONNECTION_STATUS:
                        switch(msg.arg1){
                            case 1:
                                txtView_bluetoothStatus.setText("Bluetooth Connected");
                                break;
                            case -1:
                                txtView_bluetoothStatus.setText("Connection Failed");
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String statusText = msg.obj.toString().replace("/n", "");
                        txtView_ledStatus.setText(statusText);
                        Toast.makeText(MainActivity.this, "DataReceived", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        //Turn ON
        btn_on.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String androidCmd = "w";
                connectedThread.write(androidCmd);
            }
        });

        //Turn OFF
        btn_off.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String androidCmd = "s";
                connectedThread.write(androidCmd);
            }
        });

        //Blink
        btn_blink.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String androidCmd = "b";
                connectedThread.write(androidCmd);
            }
        });
    }


    // Thread for Connection
    public static class CreateConnectThread extends Thread{

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address){

            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            //UUID uuid = bluetoothDevice.getUuids()[0].getUuid();
            UUID uuid = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");
            //final String uuid = "0000112f-0000-1000-8000-00805f9b34fb";


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
                    String arduinoMsg = null;
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
}

