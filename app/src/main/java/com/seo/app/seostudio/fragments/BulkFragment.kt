package com.seo.app.seostudio.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import com.seo.app.seostudio.R
import com.seo.app.seostudio.adapters.BulkKeywordAdapter
import com.seo.app.seostudio.ads.InterstitialHelper
import com.seo.app.seostudio.ads.NativeHelper
import com.seo.app.seostudio.api.CommonBaseApi
import com.seo.app.seostudio.api.RetrofitServiceForBaseApi
import com.seo.app.seostudio.billing.billingUtil
import com.seo.app.seostudio.databinding.FragmentBulkBinding
import com.seo.app.seostudio.model.KeywordItem
import com.seo.app.seostudio.utils.utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BulkFragment : BaseFragment() {

    @Inject
    lateinit var binding: FragmentBulkBinding

    lateinit var baseApiServices: RetrofitServiceForBaseApi
    lateinit var adapter: BulkKeywordAdapter

    private var mRewardedAd: MaxRewardedAd? = null
    var isAdShown = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseApiServices = CommonBaseApi.extractKeywordData()
        binding.BulkRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        adapter = BulkKeywordAdapter(requireContext(), this)
        binding.BulkRecyclerView.adapter = adapter
        binding.SearchButton.setOnClickListener {
            if (binding.KeywordSearchView.text.toString().trim().isNotEmpty()) {
                if (!billingUtil.isPremium) {
                    if (isAdShown) {
                        if (utils.isconnected(requireContext())) {
                            fetchData()
                        } else {
                            Toast.makeText(requireContext(), "No internet", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        if (utils.isconnected(requireContext())) {
                            showRewardedAd()
                        } else {
                            Toast.makeText(requireContext(), "No internet", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    if (utils.isconnected(requireContext())) {
                        fetchData()
                    } else {
                        Toast.makeText(requireContext(), "No internet", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                binding.KeywordSearchView.error = "Enter keyword"
            }

        }

        binding.KeywordSearchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val data = s.toString()
                if (data.isNotEmpty()) {

                } else {
                    binding.BulkRecyclerView.visibility = View.GONE
                    binding.progressbar.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.KeywordSearchView.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER) {

                if (binding.KeywordSearchView.text.toString().trim().isNotEmpty()) {
                    binding.BulkRecyclerView.visibility = View.GONE
                    binding.progressbar.visibility = View.VISIBLE
                    fetchData()
                } else {
                    binding.KeywordSearchView.error = "Enter keyword here"
                }
            }
            true
        }

        if (!billingUtil.isPremium) {
            loadRewardedAd()

        }
        loadNAtiveAd()

    }

    private fun fetchData() {

        var keyword = ""
        var list = binding.KeywordSearchView.text?.split(",")
        list?.forEach {
            keyword = "${keyword.trim()}%22${it.trim()}%22,"
        }
        binding.progressbar.visibility = View.VISIBLE
        var url =
            "https://db2.keywordsur.fr/keyword_surfer_keywords?country=${binding.countrySpinner.selectedCountryNameCode}&keywords=[${
                keyword.substringBeforeLast(
                    ","
                )
            }]"
        Log.i("result", "fetchData: ${url.toString()}")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = baseApiServices.extractKeywordsData(url)
                Log.i("result", "fetchData: ${result.toString()}")
                var list = mutableListOf<KeywordItem>()
                if (result != null) {
                    for (key in result.keys) {
                        Log.i("result", "" + key + ",value is :" + result.get(key))
                        var data = result.get(key)
                        data?.keyword = key
                        if (data != null) {
                            list.add(data)
                        }
                    }
                    Log.i("result", list.toString())
                }
                CoroutineScope(Dispatchers.Main).launch {
                    binding.progressbar.visibility = View.GONE
                    binding.BulkRecyclerView.visibility = View.VISIBLE
                    adapter.setDataList(list)
                }


            } catch (e: Exception) {
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    binding.BulkRecyclerView.visibility = View.GONE
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "No Data found against this keyword in this country",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    binding.BulkRecyclerView.visibility = View.GONE
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "No Data found against this keyword this country ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }


    fun loadRewardedAd() {
        mRewardedAd =
            MaxRewardedAd.getInstance(resources.getString(R.string.rewarded), requireActivity())
        mRewardedAd!!.setListener(object : MaxRewardedAdListener {
            override fun onAdLoaded(ad: MaxAd?) {

            }

            override fun onAdDisplayed(ad: MaxAd?) {

            }

            override fun onAdHidden(ad: MaxAd?) {
                if (!isAdShown) {
                    loadRewardedAd()
                }
            }

            override fun onAdClicked(ad: MaxAd?) {

            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                mRewardedAd = null
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                mRewardedAd = null
            }

            override fun onRewardedVideoStarted(ad: MaxAd?) {

            }

            override fun onRewardedVideoCompleted(ad: MaxAd?) {

            }


            override fun onUserRewarded(ad: MaxAd?, reward: MaxReward?) {
                isAdShown = true
                fetchData()
            }

        })
        mRewardedAd?.loadAd()

    }


    fun showRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd?.showAd()
        } else {
            activity?.let {
                InterstitialHelper.loadAndShowInterstitial(
                    it, true
                ) {
                    isAdShown = true
                    fetchData()
                }
            }

            Log.i("rewardedad", "The rewarded ad wasn't ready yet.")
        }
    }


    fun loadNAtiveAd() {
        if (!billingUtil.isPremium) {
            if (utils.isconnected(requireContext())) {
                activity?.let {
                    val nativeVideoSites = NativeHelper(it)
                    nativeVideoSites.loadAdsWithConfiguration(
                        binding.nativeContainerBulk,
                        binding.admobNativeContainerBulk,
                        resources.getString(R.string.nativead),
                        1
                    )
                }
            } else {
                binding.nativeContainerBulk.visibility = View.GONE
            }
        } else {
            binding.nativeContainerBulk.visibility = View.GONE
        }
    }
}