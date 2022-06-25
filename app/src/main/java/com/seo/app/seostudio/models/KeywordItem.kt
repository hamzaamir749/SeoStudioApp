package com.seo.app.seostudio.models

data class KeywordItem(
    var search_volume: String,
    var bing_search_volume: String,
    var cpc: String,
    var competition: String,
    var categories: MutableList<String>,
    var similar_keywords: MutableList<SimilarKeyword>,
    var keyword: String
)

data class SimilarKeyword(
    var keyword: String,
    var overlapping_pages: String,
    var search_volume: String,
    var cpc: String
)

