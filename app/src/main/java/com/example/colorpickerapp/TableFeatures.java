package com.example.colorpickerapp;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;



public class TableFeatures extends AppCompatActivity {

    private OutputStream outputStream;
    private Button bDisco;
    private Button bManual;
    private Button bGame;
    private Button bColorGradiant;
    private boolean discoBoolean;
    boolean btDevice;
    private String value;
    String test = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_features);

        Bundle extras = getIntent().getExtras(); //TODO
        if (extras != null) {
            value = extras.getString("DeviceName"); //TODO
            //String value2 = extras.getString("discoKey");

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(), "BT adapter null", Toast.LENGTH_SHORT);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            for (BluetoothDevice device : pairedDevices) {
                try {
                    if (value.equals(device.getName())) { //das war der selecteditem der übergeben wurde

                        //gefunden :)
                        btDevice = true; //Hier

                        //*****TODO hier senderei vorbereiten
                        //https://stackoverflow.com/questions/22899475/android-sample-bluetooth-code-to-send-a-simple-string-via-bluetooth
                        ParcelUuid[] uuids = device.getUuids();
                        BluetoothSocket socket = null;
                        try {
                            socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                            socket.connect();
                            outputStream = socket.getOutputStream();
                            //inStream = socket.getInputStream();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //******

                        Toast.makeText(getApplicationContext(), "connect to: " + device.getName(), Toast.LENGTH_SHORT).show();
                        break;
                    }
                } catch (NullPointerException a) {
                    a.printStackTrace();
                }
            }

            bDisco = findViewById(R.id.Disco);
            bManual = findViewById(R.id.Manual);
            bGame = findViewById(R.id.Game);
            bColorGradiant = findViewById(R.id.Farbverlauf);

            bDisco.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "connect to: " + outputStream, Toast.LENGTH_SHORT).show();
                    test = "x";

                    try {
                        write("Hallo" + test);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

    }

    public void write (String s) throws IOException {
        if (outputStream != null) {
            outputStream.write(s.getBytes());
        }
    }

}
