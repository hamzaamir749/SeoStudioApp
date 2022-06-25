package com.seo.app.seostudio.model

import androidx.annotation.Keep

@Keep
data class SimilarKeyword(
    var keyword: String,
    var overlapping_pages: String,
    var search_volume: String,
    var cpc: String
)
