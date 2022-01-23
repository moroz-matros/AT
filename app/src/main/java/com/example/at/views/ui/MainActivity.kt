package com.example.at.views.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.at.R
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity(){

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

    }

    fun replaceFragment(f: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, f)
            .addToBackStack(null)
            .commit()
    }
}

