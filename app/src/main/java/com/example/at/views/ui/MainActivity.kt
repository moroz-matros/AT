package com.example.at.views.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.at.R
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.at.models.Store
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset


class MainActivity : AppCompatActivity(){
    var mmSocket: BluetoothSocket? = null
    var connectedThread: ConnectedThread? = null
    var createConnectThread: CreateConnectThread? = null
    val c: Context = this

   val CONNECTING_STATUS = 1 // used in bluetooth handler to identify message status
   val MESSAGE_READ = 2 // used in bluetooth handler to identify message update

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels

        setContentView(R.layout.activity_main)


        val f = MenuFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, f)
            .commit()
/*

        val f = TrainingSettingsFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, f)
            .commit()



        val f = TrainingFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, f)
            .commit()

         */

    }

    fun replaceFragment(f: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, f)
            .addToBackStack(null)
            .commit()
    }

    /*
        Second most important piece of Code. GUI Handler
         */
    var handler = BluetoothHandler()
     inner class BluetoothHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                CONNECTING_STATUS -> when (msg.arg1) {
                    1 -> {

                    }
                    -1 -> {
                    }
                }
                MESSAGE_READ -> {
                    val arduinoMsg = msg.obj.toString() // Read message from Arduino
                    when (arduinoMsg.toLowerCase()) {
                        "led is turned on" -> {
                        }
                        "led is turned off" -> {
                        }
                    }
                }
            }
        }
    }


    fun connectToDevice(deviceName: String, deviceAddress: String) {
        Toast.makeText(this, "called", Toast.LENGTH_SHORT).show()

        if (deviceName == "name") {
            return
        }

        /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        createConnectThread = CreateConnectThread(bluetoothAdapter, deviceAddress)
        createConnectThread!!.start()

    }



    /* ============================ Thread to Create Bluetooth Connection =================================== */
    inner class CreateConnectThread(bluetoothAdapter: BluetoothAdapter, address: String?) :
        Thread() {
        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            bluetoothAdapter.cancelDiscovery()
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket!!.connect()
                Log.e("Status", "Device connected")
                handler.obtainMessage(CONNECTING_STATUS, 1, -1)
                    .sendToTarget()
            } catch (connectException: IOException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket!!.close()
                    Log.e("Status", "Cannot connect to device")
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1)
                        .sendToTarget()
                } catch (closeException: IOException) {
                    Log.e(ContentValues.TAG, "Could not close the client socket", closeException)
                }
                return
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = ConnectedThread(mmSocket!!)
            connectedThread!!.run()
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket!!.close()
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Could not close the client socket", e)
            }
        }

        init {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            val bluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
            var tmp: BluetoothSocket? = null
            val uuid = bluetoothDevice.uuids[0].uuid
            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid)
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Socket's create() method failed", e)
            }
            mmSocket = tmp
        }
    }

    /* =============================== Thread for Data Transfer =========================================== */
    inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?
        override fun run() {
            val buffer = ByteArray(1024) // buffer store for the stream
            var bytes = 0 // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = mmInStream!!.read().toByte()
                    var readMessage: String

                    if (buffer[bytes].toInt().toChar() == '\n') {
                        readMessage = String(buffer, 0, bytes)
                        Log.e("Arduino Message", readMessage)
                        //Toast.makeText(c, "got $readMessage", Toast.LENGTH_SHORT).show()
                        Store.ArduinoMsg = readMessage
                        handler.obtainMessage(MESSAGE_READ, readMessage)
                            .sendToTarget()
                        bytes = 0
                    } else {
                        bytes++
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    break
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        fun write(input: String) {
            Toast.makeText(c, input, Toast.LENGTH_SHORT).show()
            val charset = Charsets.US_ASCII
            val bytes = input.toByteArray(charset)//converts entered String into bytes
            try {
                mmOutStream!!.write(bytes)
            } catch (e: IOException) {
                Log.e("Send Error", "Unable to send message", e)
            }
        }

        /* Call this from the main activity to shutdown the connection */
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
            }
        }

        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = mmSocket.inputStream
                tmpOut = mmSocket.outputStream
            } catch (e: IOException) {
            }
            mmInStream = tmpIn
            mmOutStream = tmpOut
        }
    }

    /* ============================ Terminate Connection at BackPress ====================== */
    override fun onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (createConnectThread != null) {
            createConnectThread!!.cancel()
        }
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}

