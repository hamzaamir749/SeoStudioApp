package com.seo.app.seostudio.api

import androidx.annotation.Keep
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
@Keep
object RetrofitclientBaseApi {
    private var retrofit: Retrofit?=null
    public fun getClient(baseUrl:String): Retrofit
    {
        if(retrofit ==null)
        {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}