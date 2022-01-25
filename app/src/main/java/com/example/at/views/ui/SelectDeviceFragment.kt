package com.example.at.views.ui

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.at.databinding.FragmentSelectDeviceBinding
import com.example.at.models.DeviceInfoModel
import com.example.at.views.adapters.DeviceListAdapter
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList

class SelectDeviceFragment : Fragment() {

    private var _binding: FragmentSelectDeviceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectDeviceBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // Bluetooth Setup
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // Get List of Paired Bluetooth Device
        val pairedDevices = bluetoothAdapter.bondedDevices
        val deviceList: MutableList<Any> = ArrayList()
        if (pairedDevices.size > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (device in pairedDevices) {
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
                val deviceInfoModel = DeviceInfoModel(deviceName, deviceHardwareAddress)
                deviceList.add(deviceInfoModel)
            }
            // Display paired device using recyclerView
            val recyclerView: RecyclerView = binding.recyclerViewDevice
            recyclerView.layoutManager = LinearLayoutManager(this.context)
            val deviceListAdapter = DeviceListAdapter(this, deviceList)
            recyclerView.adapter = deviceListAdapter
            recyclerView.itemAnimator = DefaultItemAnimator()
        } else {
            val view: View = binding.recyclerViewDevice
            val snackbar = Snackbar.make(
                view,
                "Activate Bluetooth or pair a Bluetooth device",
                Snackbar.LENGTH_INDEFINITE
            )
            snackbar.setAction("OK") { }
            snackbar.show()
        }
        val deviceInfoModel = DeviceInfoModel("name", "address")
        deviceList.add(deviceInfoModel)
        val recyclerView: RecyclerView = binding.recyclerViewDevice
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        val deviceListAdapter = DeviceListAdapter(this, deviceList)
        recyclerView.adapter = deviceListAdapter
        recyclerView.itemAnimator = DefaultItemAnimator()
    }
}