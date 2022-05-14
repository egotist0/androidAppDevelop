package com.example.note

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.note.wp.Wallpaper
import com.example.note.wp.WpModel
import com.example.note.wp.wpModel

open class BaseActivity : AppCompatActivity(), Wallpaper {
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //setNightMode()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        if(sharedPreferences!!.getBoolean("nightMode", false)){
            setNightMode()
        }
        else{
            setDayMode()
        }

        super.onCreate(savedInstanceState)
//        wpModel.getSp(this, WpModel.KEY).apply {
//            if (this != WpModel.INVILD_VALUE) getWindow().setBackgroundDrawableResource(this)
//        }
        wpModel.wpListener.add(this)
        Log.d("AAA", "onCreate: ${wpModel.wpListener.size}")

    }

    //设置主题
    fun setNightMode() {
        setTheme(R.style.NightTheme)

    }
    fun setDayMode() {
        setTheme(R.style.DayTheme)

    }

    override fun onWallpaperChanged(wallPaperResId: Int) {
        Log.d("aaaaa", "onWallpaperChanged: $this $wallPaperResId")
        getWindow().setBackgroundDrawableResource(wallPaperResId)
    }


}