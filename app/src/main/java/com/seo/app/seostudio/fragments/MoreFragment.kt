package com.seo.app.seostudio.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seo.app.seostudio.BuildConfig
import com.seo.app.seostudio.R
import com.seo.app.seostudio.ads.NativeHelper
import com.seo.app.seostudio.billing.billingUtil
import com.seo.app.seostudio.databinding.FragmentMoreBinding
import com.seo.app.seostudio.utils.utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MoreFragment : BaseFragment() {

    @Inject
    lateinit var binding: FragmentMoreBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!billingUtil.isPremium) {

        } else {
            binding.premiumCardview.visibility = View.GONE
        }
        loadNAtiveAdsetting()
        binding.rateusCardview.setOnClickListener {

            rateusdialog()
        }

        binding.ShareappCardview.setOnClickListener {

            try {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "\n" +
                            "Let me recommend you this application for keyword plans\n" +
                            "\n https://play.google.com/store/apps/details?id=" + requireContext().applicationContext.packageName
                )
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            } catch (exc: java.lang.Exception) {
                exc.printStackTrace()
            }
        }
        binding.websiteCardivew.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://seostudios.xyz")
                    )
                )
            } catch (e: Exception) {
            }
        }
        binding.feedbackCardview.setOnClickListener {

            feedback()
        }
        binding.youtubeCardivew.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/channel/UCeMtaKHDdTI8Mo6r_cUBLLQ")
                    )
                )
            } catch (e: Exception) {
            }

        }
        binding.blogpostCardivew.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://blog.seoanalyser.me")
                    )
                )
            } catch (e: Exception) {
            }

        }
        binding.premiumCardview.setOnClickListener {
            activity?.let {
                billingUtil(requireContext()).purchase(
                    it,
                    if (BuildConfig.DEBUG) billingUtil.LIFE_TIME_PRODUCT_DEBUG else billingUtil.LIFE_TIME_PRODUCT
                )
            }

        }
    }

    private fun rate() {
        val uri =
            Uri.parse("market://details?id=" + requireContext().applicationContext.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + requireContext().applicationContext.packageName)
                )
            )
        }
    }

    private fun rateusdialog() {
        rate()
    }

    fun feedback() {
        val uriText = "mailto:contact@akstudios.me" +
                "?subject=" + Uri.encode("keyword planner app")
        val uri = Uri.parse(uriText)
        val sendIntent = Intent(Intent.ACTION_SENDTO)
        sendIntent.data = uri
        try {
            startActivity(Intent.createChooser(sendIntent, "Send email"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadNAtiveAdsetting() {
        if (!billingUtil.isPremium) {
            if (utils.isconnected(requireContext())) {
                activity?.let {
                    val nativeVideoSites = NativeHelper(it)
                    nativeVideoSites.loadAdsWithConfiguration(
                        binding.nativeContainerSetting,
                        binding.admobNativeContainerSetting,

                        resources.getString(R.string.nativead), 1
                    )
                }
            } else {
                binding.nativeContainerSetting.visibility = View.GONE
            }
        } else {
            binding.nativeContainerSetting.visibility = View.GONE
        }
    }

}