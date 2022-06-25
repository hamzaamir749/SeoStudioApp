package com.seo.app.seostudio.model

import androidx.annotation.Keep

@Keep
data class KeywordItem(
    var search_volume : String,
    var bing_search_volume : String,
    var cpc : String,
    var competition : String,
    var categories : List<String>,
    var similar_keywords : List<SimilarKeyword>,
    var keyword:String
)
