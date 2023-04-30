package com.example.rescue;

import android.bluetooth.BluetoothSocket;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class BluetoothSocketHelper implements Serializable {
    public transient BluetoothSocket bluetoothSocket;

    public BluetoothSocketHelper(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }
}
