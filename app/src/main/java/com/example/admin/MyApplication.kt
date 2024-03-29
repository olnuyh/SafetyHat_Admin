package com.example.admin

import android.app.Application
import android.content.Context
import android.util.SparseBooleanArray
import org.json.JSONArray
import java.net.Socket

class MyApplication : Application() {
    companion object{
        var socket: Socket?= null
        lateinit var prefs : SharedPreferencesManager
        lateinit var workers : JSONArray
        val workerList = arrayListOf<WorkerCheckStatus>()
        val areaList = arrayListOf<String>()
    }

    override fun onCreate() {
        prefs = SharedPreferencesManager(applicationContext)
        super.onCreate()
    }
}

class SharedPreferencesManager(context : Context){
    private val prefs = context.getSharedPreferences("admin", Context.MODE_PRIVATE)

    fun getString(key:String, value:String) : String{
        return prefs.getString(key, value).toString()
    }

    fun setString(key:String, value:String) {
        prefs.edit().putString(key, value).apply()
    }

    fun clear(){
        prefs.edit().clear().commit()
    }
}