package com.seo.app.seostudio.utils

import com.seo.app.seostudio.models.KeywordItem


sealed class FlowState {
    object Empty : FlowState()
    class Failure(var error: Throwable?) : FlowState()
    object Loading : FlowState()
    class Error(val error: String) : FlowState()

    class SuccessApiResponse(var data:HashMap<String, KeywordItem>) : FlowState()

}