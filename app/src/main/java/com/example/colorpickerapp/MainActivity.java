package com.example.colorpickerapp;

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

import top.defaults.colorpicker.ColorPickerPopup;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Button buttonCP;
    private Button buttonBT;
    private Button buttonTF;
    int blue;
    int red;
    int green;
    int test;
    private Switch bRswitch;
    boolean swibitch;
    String sendingRegiment = "background";
    String text = "Bitte schalten Sie ihr Bluetooth ein";
    String dev;
    boolean btDevice;

    private OutputStream outputStream;
    //private InputStream inStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //step to
        // hier wird aus der btactivity übergeben an was geschickt werden soll
        Bundle extras = getIntent().getExtras(); //TODO
        if (extras != null) {
            String value = extras.getString("DeviceName"); //TODO
            String value2 = extras.getString("discoKey");

            Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
            //The key argument here must match that used in the other activity

            // hier wird ein Selected device erzeugt
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(), "BT adapter null", Toast.LENGTH_SHORT);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            //hier wird nochmal gesucht bis der passende Name gefunden ist
            for (BluetoothDevice device : pairedDevices) {
                try {
                    if (value.equals(device.getName())) { //das war der selecteditem der übergeben wurde

                        dev = device.getName();
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
        }


        textView = findViewById(R.id.textView);
        buttonCP = findViewById(R.id.button);
        buttonBT = findViewById(R.id.button3);
        bRswitch = findViewById(R.id.switch1);
        buttonTF = findViewById(R.id.button2);


        bRswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(swibitch = bRswitch.isChecked()) {
                    sendingRegiment = "c";
                } else {
                    sendingRegiment = "a";
                }

                try {
                    write(sendingRegiment + "");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

            if(btDevice == false) { //Hier
                buttonCP.setVisibility(View.GONE);
                buttonTF.setVisibility(View.GONE);
            }

            buttonCP.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    new ColorPickerPopup.Builder(MainActivity.this)

                            .initialColor(Color.TRANSPARENT) //default color red
                            .enableBrightness(true)
                            .enableAlpha(true)
                            .okTitle("Choose")
                            .cancelTitle("Cancel")
                            .showIndicator(true)
                            .showValue(true)
                            .build()
                            .show(v, new ColorPickerPopup.ColorPickerObserver() {

                                @Override
                                public void onColorPicked(int color) {
                                    //this method call, when user selected color...
                                    textView.setTextColor(color);

                                    red = (color >> 16) & 0xff;
                                    green = (color >> 8) & 0xff;
                                    blue = color & 0xff;

                                    test = color;

                                }

                                @Override
                                public void onColor(int color, boolean fromUser) {
                                    //this method call, when user selecting color...
                                    textView.setTextColor(color);

                                    // TODO dieser code nach arduino
                                    red = (color >> 16) & 0xff;
                                    green = (color >> 8) & 0xff;
                                    blue = color & 0xff;

                                    //ACHTUNG, hier wird gesendet!!! Bluetooth


                                    try {
                                        //write(color + "\n");
                                        write("r" + red);
                                        write("g" + green);
                                        write("b" + blue);
                                        //write(sendingRegiment + "");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                }
            });

        buttonBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO go to BT-Page

                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                    // Device does not support Bluetooth
                    Toast btWarnung = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                    btWarnung.show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), BluetoothDevicesActivity.class);
                    startActivity(intent);
                }
            }
        });

        buttonTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getApplicationContext(), TableFeatures.class);
                //startActivity(intent);

                Intent intent2 = new Intent(getBaseContext(), TableFeatures.class);
                intent2.putExtra("DeviceName", dev);
                startActivity(intent2);

            }
        });

    }

    public void write (String s) throws IOException {
        if (outputStream != null) {
            outputStream.write(s.getBytes());
        }
    }

}