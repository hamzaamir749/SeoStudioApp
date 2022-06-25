package com.seo.app.seostudio.sharedpref

import android.content.Context

class savevalue(context:Context)
{
    val PREFERENCE_NAME="seostudio_sharedpref"

    companion object {

        // is premium
        var ispremium="ispremium"


    }
    val p=context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)
    fun setFloat(key:String,value:Float)
    {
        val a=p.edit()
        a.putFloat(key,value)
        a.apply()
    }
    fun getFloat(key:String):Float
    {
        return p.getFloat(key,1f)
    }
    fun setInt(key:String,value:Int)
    {
        val a=p.edit()
        a.putInt(key,value)
        a.apply()
    }
    fun getInt(key:String):Int
    {
        return p.getInt(key,-1)
    }
    fun setboolean(key:String,value:Boolean)
    {
        val a=p.edit()
        a.putBoolean(key,value)
        a.apply()
    }
    fun getboolean(key:String):Boolean
    {
        return p.getBoolean(key,false)
    }
    fun setString(key:String,value:String)
    {
        val a=p.edit()
        a.putString(key,value)
        a.apply()
    }
    fun getstring(key:String): String?
    {
        return p.getString(key,"")
    }
    fun setLong(key:String,value:Long)
    {
        val a=p.edit()
        a.putLong(key,value)
        a.apply()
    }
    fun getLong(key:String): Long?
    {
        return p.getLong(key,0L)
    }
}