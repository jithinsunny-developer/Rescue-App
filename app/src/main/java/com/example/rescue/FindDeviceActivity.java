package com.example.rescue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class FindDeviceActivity extends AppCompatActivity {

    private Toolbar findDeviceToolbar;
    private Button pairedDeviceBtn;
    private ListView pairedDeviceListView;
    private TextView btStatus;


    private Set<BluetoothDevice> bluetoothDevices;
    private ArrayList<BluetoothDevice> bluetoothDevicesList;
    private ArrayList<UUID> devicesUUID;
    ArrayList<String> devicesName;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> arrayAdapter;



    static final int STATE_CONNECTING = 1;
    static final int STATE_CONNECTED = 2;
    static final int STATE_CONNECTION_FAILED = 3;

    private static final String APP_NAME = "RescueApp";

    BluetoothSocket connected_bluetooth_socket = null;

    private void initializeViews(){
        pairedDeviceBtn = (Button)findViewById(R.id.show_paired_devices_btn);
        pairedDeviceListView = (ListView)findViewById(R.id.paired_devices_list_view);
        btStatus = (TextView)findViewById(R.id.find_device_bt_status_show);
        findDeviceToolbar = (Toolbar)findViewById(R.id.find_device_toolbar);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_device);

        initializeViews();

        setSupportActionBar(findDeviceToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevicesList = new ArrayList<>();
        devicesUUID = new ArrayList<>();

        if(!bluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 2786);
        }

        if(bluetoothAdapter.isEnabled()){
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(discoverableIntent, 2786);
        }

        pairedDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBluetoothDevice();///////////////////////////////
            }
        });

        pairedDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(FindDeviceActivity.this, bluetoothDevicesList.get(position).getName(), Toast.LENGTH_SHORT).show();


                RescueBTClient client = new RescueBTClient(bluetoothDevicesList.get(position), devicesUUID.get(position));
                client.start();
                btStatus.setText("Connecting");


            }
        });

    }


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case STATE_CONNECTED:
                    btStatus.setText("Connected");
                    break;
                case STATE_CONNECTING:
                    btStatus.setText("Connecting");
                    break;
                case STATE_CONNECTION_FAILED:
                    btStatus.setText("Connection Failed");
                    break;
            }
            return true;
        }
    });

    public void searchBluetoothDevice() {
        if(bluetoothAdapter.isEnabled()){
            bluetoothDevices = bluetoothAdapter.getBondedDevices();
            devicesName = new ArrayList<>();
            for(BluetoothDevice device : bluetoothDevices){
                devicesName.add(device.getName());
                bluetoothDevicesList.add(device);
                //
                ParcelUuid[] parcelUuids=  device.getUuids();
//               for(ParcelUuid uuid: parcelUuids){
//                   Log.i(device.getName(), uuid.getUuid().toString());
//               }
                devicesUUID.add(parcelUuids[0].getUuid());////////////////////

            }
            arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, devicesName);
            pairedDeviceListView.setAdapter(arrayAdapter);


        }
    }

    public class RescueBTClient extends Thread{
        private BluetoothDevice device;
        private BluetoothSocket socket;
        public RescueBTClient(BluetoothDevice device, UUID MY_UUID){
            this.device = device;


            try{
//                this.socket = this.device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                this.socket = this.device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        @Override
        public void run() {

            try{
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                final Bundle bundle = new Bundle();
                bundle.putBinder("inputSocket", new ObjectWrapperForBinder(socket));
                //START SERVICE
                Intent serviceIntent = new Intent(getApplicationContext(), ReceiveSignalService.class);
                serviceIntent.putExtras(bundle);

//                stopService(serviceIntent);

                ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
                finish();

            }catch (IOException e){
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
                Log.i("SOCKET", socket.getRemoteDevice().getName());
            }
        }
    }

}
/**
 *
 * implementation 'com.google.firebase:firebase-database:16.0.4'
 *     implementation 'com.google.firebase:firebase-auth:16.0.4'
 *
 * */
