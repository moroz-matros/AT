package com.example.at.views.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.at.R
import com.example.at.models.DeviceInfoModel
import com.example.at.views.ui.MainActivity
import com.example.at.views.ui.SelectDeviceFragment


class DeviceListAdapter(private val context: SelectDeviceFragment, private val deviceList: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var textName: TextView
        var textAddress: TextView
        var linearLayout: LinearLayout

        init {
            textName = v.findViewById(R.id.text_view_device_name)
            textAddress = v.findViewById(R.id.text_view_device_address)
            linearLayout = v.findViewById(R.id.linear_layout_device_info)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.device_info_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as ViewHolder
        val deviceInfoModel: DeviceInfoModel = deviceList[position] as DeviceInfoModel
        itemHolder.textName.setText(deviceInfoModel.getDeviceInfoName())
        itemHolder.textAddress.setText(deviceInfoModel.getDeviceInfoHardwareAddress())

        // When a device is selected
        itemHolder.linearLayout.setOnClickListener {
            (context.activity as MainActivity).connectToDevice(deviceInfoModel.deviceName!!,
            deviceInfoModel.deviceHardwareAddress!!)
        }
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }
}
