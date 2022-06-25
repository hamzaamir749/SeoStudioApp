package com.seo.app.seostudio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.applovin.sdk.AppLovinSdk
import com.seo.app.seostudio.BuildConfig
import com.seo.app.seostudio.billing.BillingUtil
import com.seo.app.seostudio.databinding.FragmentMenuBinding
import com.seo.app.seostudio.ui.activities.MainActivity
import com.seo.app.seostudio.utils.*

// need website,need youtube link
class MenuFragment : Fragment() {

    private lateinit var binding: FragmentMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        clickListeners()
    }

    private fun clickListeners() {
        binding.ShareappCardview.setOnClickListener {
            (requireActivity() as MainActivity).loadAd(true) {
                requireContext().shareApp()
            }
        }
        binding.websiteCardivew.setOnClickListener {
            (requireActivity() as MainActivity).loadAd(true) {
                requireContext().openBrowser(GlobalVariables.WEBSITE_LINK)
            }

        }
        binding.youtubeCardivew.setOnClickListener {
            (requireActivity() as MainActivity).loadAd(true) {
                requireContext().openBrowser(GlobalVariables.YOUTUBE_LINK)
            }

        }
        binding.blogpostCardivew.setOnClickListener {
            (requireActivity() as MainActivity).loadAd(true) {
                requireContext().openBrowser(GlobalVariables.BLOG_LINK)
            }

        }
        binding.rateusCardview.setOnClickListener {
            (requireActivity() as MainActivity).loadAd(true) {
                requireContext().rateUs()
            }

        }
        binding.feedbackCardview.setOnClickListener {
            (requireActivity() as MainActivity).loadAd(true) {
                requireContext().feedbackUs(GlobalVariables.EMAIL)
            }

        }

        binding.premiumCardview.setOnClickListener {
            activity?.let {
                BillingUtil(requireContext()).purchase(
                    it,
                    if (BuildConfig.DEBUG) BillingUtil.LIFE_TIME_PRODUCT_DEBUG else BillingUtil.LIFE_TIME_PRODUCT
                )
            }
        }
    }

    private fun initViews() {

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
}