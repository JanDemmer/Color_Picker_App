package com.example.colorpickerapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothDevicesActivity extends AppCompatActivity {


    private ListView lstvw;
    private ArrayAdapter aAdapter;

    Set<BluetoothDevice> pairedDevices;

    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);

        //hier wird gecheckt ob das Device ein Bluetoothdevice hat
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "BT adapter null", Toast.LENGTH_SHORT);
        }

        //hier wird nach den erkannten Bt-Geräten in der Nähe gesucht
        pairedDevices = mBluetoothAdapter.getBondedDevices();

        Toast.makeText(getApplicationContext(), "Hello BT", Toast.LENGTH_SHORT);

        //hier wird eine Liste der erkannten Geräte erstellt
        ArrayList list = new ArrayList();

        aAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);

        //es wird solange gesucht bis deviceName das gleiche wie der string s ist
        List<String> s = new ArrayList<String>();
        for (BluetoothDevice device : pairedDevices) {
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress(); // MAC address
            aAdapter.add(deviceName);
        }

        // findet die Listview aus der xml
        lstvw = (ListView) findViewById(R.id.BTList);
        lstvw.setAdapter(aAdapter);

        //setContentView(lstvw);

        //hier wird das Gerät ausgsucht
        lstvw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selectedItem = (String) parent.getItemAtPosition(position);


            //fürt wieder zurück in die Main
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("DeviceName", selectedItem);
            startActivity(intent);

        }
        });

    }



}