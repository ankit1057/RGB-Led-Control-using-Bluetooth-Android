package com.led.ph;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;


public class ledControl extends Activity {

    Button btnBlink, btnOff, btnDisconnect, btnRed, btnGreen, btnBlue;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_control);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        btnBlink = (Button)findViewById(R.id.btnBlink);
        btnRed = (Button)findViewById(R.id.btnRed);
        btnGreen = (Button)findViewById(R.id.btnGreen);
        btnBlue = (Button)findViewById(R.id.btnBlue);
        btnOff = (Button)findViewById(R.id.btnOff);
        btnDisconnect = (Button)findViewById(R.id.btnDisconnect);

        new ConnectBT().execute();
    }

    public void buttonClicked(View v) {
        switch (v.getId()) {
            case (R.id.btnRed):
                turnOnRed();
                break;
            case (R.id.btnGreen):
                turnOnGreen();
                break;
            case (R.id.btnBlue):
                turnOnBlue();
                break;
            case (R.id.btnBlink):
                blinkLed();
                break;
            case (R.id.btnOff):
                turnOffLed();
                break;
            case (R.id.btnDisconnect):
                Disconnect();
                break;
        }
    }

    private void Disconnect() {
        if (btSocket!=null) { //If the btSocket is busy
            try {
                btSocket.close(); //close connection
            } catch (IOException e) {
                msg("Error");
            }
        }
        finish(); //return to the first layout
    }

    private void turnOffLed() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("TF".toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void blinkLed() {
        if (btSocket!=null) {
            try {
                btSocket.getOutputStream().write("BLINK".toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnOnRed() {
        if (btSocket!=null)  {
            try {
                btSocket.getOutputStream().write("RED".toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnOnGreen() {
        if (btSocket!=null)  {
            try {
                btSocket.getOutputStream().write("GREEN".toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnOnBlue() {
        if (btSocket!=null)  {
            try {
                btSocket.getOutputStream().write("BLUE".toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                 myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                 BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                 btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                 BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                 btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
