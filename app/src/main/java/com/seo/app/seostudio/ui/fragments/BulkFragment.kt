package com.seo.app.seostudio.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.applovin.sdk.AppLovinSdk
import com.seo.app.seostudio.adapters.BulkKeywordAdapter
import com.seo.app.seostudio.billing.BillingUtil
import com.seo.app.seostudio.databinding.FragmentBulkBinding
import com.seo.app.seostudio.models.KeywordItem
import com.seo.app.seostudio.ui.activities.MainActivity
import com.seo.app.seostudio.utils.*
import com.seo.app.seostudio.viewmodels.KeywordBulkViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BulkFragment : Fragment() {

    lateinit var binding: FragmentBulkBinding

    @Inject
    lateinit var loadingDialog: LoadingDialog

    @Inject
    lateinit var adapterBulkKeyword: BulkKeywordAdapter


    private val KeywordBulkViewModel: KeywordBulkViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBulkBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        clickListeners()
    }

    private fun clickListeners() {

        binding.KeywordSearchView.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                (requireActivity() as MainActivity).loadAd(false) {
                    if (binding.KeywordSearchView.text.toString().trim().isNotEmpty()) {
                        var keyword = ""
                        val list = binding.KeywordSearchView.text?.split(",")
                        list?.forEach {
                            keyword = "${keyword.trim()}%22${it.trim()}%22,"
                        }
                        KeywordBulkViewModel.getKeywordBulk(
                            "https://db2.keywordsur.fr/keyword_surfer_keywords?country=${binding.countrySpinner.selectedCountryNameCode}&keywords=[${
                                keyword.substringBeforeLast(
                                    ","
                                )
                            }]"
                        )
                    } else {
                        binding.KeywordSearchView.error = "Please Enter"
                    }
                }
            }
            true
        }
        binding.SearchButton.setOnClickListener {
            (requireActivity() as MainActivity).loadAd(false) {
                if (binding.KeywordSearchView.text.toString().trim().isNotEmpty()) {
                    var keyword = ""
                    val list = binding.KeywordSearchView.text?.split(",")
                    list?.forEach {
                        keyword = "${keyword.trim()}%22${it.trim()}%22,"
                    }
                    KeywordBulkViewModel.getKeywordBulk(
                        "https://db2.keywordsur.fr/keyword_surfer_keywords?country=${binding.countrySpinner.selectedCountryNameCode}&keywords=[${
                            keyword.substringBeforeLast(
                                ","
                            )
                        }]"
                    )
                } else {
                    binding.KeywordSearchView.error = "Please Enter"
                }
            }
        }

        adapterBulkKeyword.setOnClick(object : RecyclerviewCallbacks<String> {
            override fun onItemClick() {
                (requireActivity() as MainActivity).loadAd(true)
            }
        })
    }

    private fun initViews() {
        binding.BulkRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterBulkKeyword
        }
        handleApiResponse()
        if (BillingUtil.isPremium) {
            binding.bannerAdLayout.visibility = View.GONE
        } else {
            loadApplovinSdk()
        }
    }

    private fun loadApplovinSdk() {
        AppLovinSdk.getInstance(requireContext()).mediationProvider = "max"
        AppLovinSdk.getInstance(requireContext()).initializeSdk {
            requireContext().createBannerAd(binding.bannerAdLayout)
        }
    }


    private fun handleApiResponse() {
        lifecycleScope.launchWhenCreated {
            KeywordBulkViewModel.keywordBulk.collect {
                when (it) {
                    is FlowState.Loading -> loadingDialog.showDialog(requireActivity())
                    is FlowState.Failure -> {
                        loadingDialog.dismissDialog()
                        requireContext().showToast("Something went wrong, please try again!")
                    }
                    is FlowState.SuccessApiResponse -> {
                        loadingDialog.dismissDialog()
                        var list = mutableListOf<KeywordItem>()
                        if (it.data != null) {
                            for (key in it.data.keys) {
                                Log.i("result", "" + key + ",value is :" + it.data.get(key))
                                var data = it.data.get(key)
                                data?.keyword = key
                                if (data != null) {
                                    list.add(data)
                                }
                            }
                            Log.i("result", list.toString())

                            adapterBulkKeyword.setDataList(list)
                        }
                    }
                    is FlowState.Error -> {
                        loadingDialog.dismissDialog()
                        requireContext().showToast(it.error)

                    }
                    else -> {
                    }
                }
            }
        }
    }
}