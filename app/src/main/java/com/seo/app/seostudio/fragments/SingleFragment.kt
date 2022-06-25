package com.seo.app.seostudio.fragments

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.widget.Adapter
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import com.seo.app.seostudio.R
import com.seo.app.seostudio.adapters.SimilarKeywordAdapter
import com.seo.app.seostudio.ads.InterstitialHelper
import com.seo.app.seostudio.ads.NativeHelper
import com.seo.app.seostudio.api.CommonBaseApi
import com.seo.app.seostudio.api.RetrofitServiceForBaseApi
import com.seo.app.seostudio.billing.billingUtil
import com.seo.app.seostudio.databinding.FragmentSingleBinding
import com.seo.app.seostudio.model.SimilarKeyword
import com.seo.app.seostudio.utils.utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.log

@AndroidEntryPoint
class SingleFragment : BaseFragment() {


    @Inject
    lateinit var binding: FragmentSingleBinding

    lateinit var baseApiServices: RetrofitServiceForBaseApi
    lateinit var adapter: SimilarKeywordAdapter

    var simirKeyWordList: MutableList<SimilarKeyword> = mutableListOf()

    private var mRewardedAd: MaxRewardedAd? = null


    var isAdShown = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseApiServices = CommonBaseApi.extractKeywordData()
        binding.relatedkeywordsDataRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        adapter = SimilarKeywordAdapter(requireContext(), this)
        binding.relatedkeywordsDataRecyclerView.adapter = adapter
        binding.SearchButton.setOnClickListener {
            if (binding.KeywordSearchView.text.toString().trim().isNotEmpty()) {
                if (utils.isconnected(requireContext())) {
                    binding.DataLayout.visibility = View.GONE
                    binding.progressbar.visibility = View.VISIBLE
                    fetchData()
                } else {
                    Toast.makeText(requireContext(), "No internet", Toast.LENGTH_SHORT).show()
                }

            } else {
                binding.KeywordSearchView.error = "Enter keyword here"
            }
        }
        binding.KeywordSearchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val data = s.toString()
                if (data.isNotEmpty()) {

                } else {
                    binding.DataLayout.visibility = View.GONE
                    binding.progressbar.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.KeywordSearchView.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER) {

                if (binding.KeywordSearchView.text.toString().trim().isNotEmpty()) {
                    binding.DataLayout.visibility = View.GONE
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
        binding.showMoreKeywordButton.setOnClickListener {
            if (utils.isconnected(requireContext())) {
                showRewardedAd()
            } else {
                Toast.makeText(requireContext(), "No internet", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun fetchData() {

        binding.progressbar.visibility = View.VISIBLE
        var url =
            "https://db2.keywordsur.fr/keyword_surfer_keywords?country=${binding.countrySpinner.selectedCountryNameCode}&keywords=[%22${
                binding.KeywordSearchView.text.toString().trim()
            }%22]"
        Log.i("result", "fetchData: ${url.toString()}")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = baseApiServices.extractKeywordsData(url)
                Log.i("result", "fetchData: ${result.toString()}")

                var data = result?.get(binding.KeywordSearchView.text.toString().trim().lowercase())
                withContext(Dispatchers.Main) {
                    if (data != null) {
                        binding.DataLayout.visibility = View.VISIBLE
                        binding.progressbar.visibility = View.GONE
                        if (isAdShown) {
                            binding.showMoreKeywordButton.visibility = View.GONE
                        } else {
                            if (data.similar_keywords.isEmpty() || data.similar_keywords.size < 4) {
                                binding.showMoreKeywordButton.visibility = View.GONE
                            } else {
                                binding.showMoreKeywordButton.visibility = View.VISIBLE
                            }

                        }

                        binding.DataKeywordname.text = "Keyword data for '${
                            binding.KeywordSearchView.text?.trim().toString()
                        }'"
                        binding.searchVolume.text = NumberFormat.getNumberInstance(Locale.US)
                            .format(data.search_volume.toDouble());
                        binding.BingsearchVolume.text = if (data.bing_search_volume != "0") {
                            NumberFormat.getNumberInstance(Locale.US)
                                .format(data.bing_search_volume.toDouble());
                        } else {
                            "0"
                        }
                        binding.competion.text = utils.roundOffDecimal(data.competition.toDouble())
                        binding.cpcValue.text =
                            if (data.cpc.isEmpty()) {
                                "0$"
                            } else {
                                DecimalFormat("#.##").format(data.cpc.toDouble()) + "$"
                            }

                        binding.relatedKeywordname.text = "Similar Keywords related to '${
                            binding.KeywordSearchView.text?.trim().toString()
                        }'"

                        if (data.similar_keywords.isNotEmpty()) {
                            binding.showMoreKeywordButton.setText("Show ${data.similar_keywords.size - 4} More similar keywords")

                            simirKeyWordList = data.similar_keywords as MutableList
                            var similarList = mutableListOf<SimilarKeyword>()
                            if (!billingUtil.isPremium) {
                                if (isAdShown) {
                                    similarList = data.similar_keywords as MutableList
                                } else {
                                    var i = 0
                                    data.similar_keywords.forEach {
                                        if (i < 4) {
                                            similarList.add(it)
                                            i++
                                        }

                                    }
                                }

                            } else {
                                similarList = simirKeyWordList
                                binding.showMoreKeywordButton.visibility = View.GONE
                            }
                            adapter.setDataList(similarList)
                        } else {
                            binding.showMoreKeywordButton.visibility = View.GONE
                        }
                    } else {
                        binding.DataLayout.visibility = View.GONE
                        binding.progressbar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "No Data found against this keyword",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    binding.DataLayout.visibility = View.GONE
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
                    binding.DataLayout.visibility = View.GONE
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

    fun openSimilar(keyword: String) {
        if (utils.isconnected(requireContext())) {
            binding.KeywordSearchView.setText(keyword)
            binding.DataLayout.visibility = View.GONE
            binding.progressbar.visibility = View.VISIBLE
            fetchData()
        } else {
            Toast.makeText(requireContext(), "No internet", Toast.LENGTH_SHORT).show()
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
                adapter.setDataList(simirKeyWordList)
                binding.showMoreKeywordButton.visibility = View.GONE
                isAdShown = true
            }

        })
        mRewardedAd?.loadAd()


    }


    fun showRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd!!.showAd()
        } else {
            activity?.let {
                InterstitialHelper.loadAndShowInterstitial(
                    it, true
                ) {
                    adapter.setDataList(simirKeyWordList)
                    binding.showMoreKeywordButton.visibility = View.GONE
                }
            }

        }
    }


    fun loadNAtiveAd() {
        if (!billingUtil.isPremium) {
            if (utils.isconnected(requireContext())) {
                activity?.let {
                    val nativeVideoSites = NativeHelper(it)
                    nativeVideoSites.loadAdsWithConfiguration(
                        binding.nativeContainerSingle,
                        binding.admobNativeContainerSingle,

                        resources.getString(R.string.nativead), 1
                    )
                }
            } else {
                binding.nativeContainerSingle.visibility = View.GONE
            }
        } else {
            binding.nativeContainerSingle.visibility = View.GONE
        }
    }

}