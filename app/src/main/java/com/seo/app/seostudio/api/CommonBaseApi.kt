package com.seo.app.seostudio.api

import androidx.annotation.Keep

@Keep
object CommonBaseApi {
    private val apiBaseUrl = "https://db2.keywordsur.fr/"
    public fun extractKeywordData(): RetrofitServiceForBaseApi {
        return RetrofitclientBaseApi.getClient(apiBaseUrl)
            .create(RetrofitServiceForBaseApi::class.java)
    }
}
