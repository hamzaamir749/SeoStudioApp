package com.seo.app.seostudio.network

import com.seo.app.seostudio.models.KeywordItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url


interface ApiService {
    @Headers("Accept: application/json")
    @GET
    suspend fun getKeywordBulk(
        @Url url: String
    ): Response<HashMap<String, KeywordItem>>

}
