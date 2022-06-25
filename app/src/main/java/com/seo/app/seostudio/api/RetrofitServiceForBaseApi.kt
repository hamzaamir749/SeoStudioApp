package com.seo.app.seostudio.api

import androidx.annotation.Keep
import com.seo.app.seostudio.model.KeywordItem
import retrofit2.http.GET
import retrofit2.http.Url

@Keep
interface RetrofitServiceForBaseApi {
    @GET
    suspend fun extractKeywordsData(@Url url: String): HashMap<String, KeywordItem>?
}