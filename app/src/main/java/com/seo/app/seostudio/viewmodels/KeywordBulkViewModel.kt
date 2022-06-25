package com.seo.app.seostudio.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akstudios.KSTWV.repository.KeywordBulkRepository
import com.seo.app.seostudio.utils.FlowState
import com.seo.app.seostudio.utils.parseError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KeywordBulkViewModel @Inject constructor(private var repo: KeywordBulkRepository) :
    ViewModel() {
    private val _keywordBulk: MutableStateFlow<FlowState> = MutableStateFlow(FlowState.Empty)
    val keywordBulk: StateFlow<FlowState> = _keywordBulk

    fun getKeywordBulk(url: String) {
        viewModelScope.launch {
            repo.getKeywordBulk(url).onStart {
                _keywordBulk.value = FlowState.Loading
            }.catch { e ->
                _keywordBulk.value = FlowState.Failure(e)
            }.collect { response ->
                if (response.isSuccessful) {
                    _keywordBulk.value = FlowState.SuccessApiResponse(response.body()!!)
                } else {
                    _keywordBulk.value = FlowState.Error(parseError(response))
                }
            }
        }


    }
}