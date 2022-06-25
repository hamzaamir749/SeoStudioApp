package com.seo.app.seostudio.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.seo.app.seostudio.R
import com.seo.app.seostudio.ads.InterstitialHelper
import com.seo.app.seostudio.databinding.BulkSimilarKeyworBinding
import com.seo.app.seostudio.databinding.SimilarItemLayoutBinding
import com.seo.app.seostudio.fragments.SingleFragment
import com.seo.app.seostudio.model.KeywordItem
import com.seo.app.seostudio.model.SimilarKeyword
import com.seo.app.seostudio.utils.utils
import java.lang.Exception
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class BulkKeywordAdapter(val context: Context, val frag: Fragment) :
    RecyclerView.Adapter<BulkKeywordAdapter.myviewholder>() {

    var list: MutableList<KeywordItem> = ArrayList()

    inner class myviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setdata(data: KeywordItem, p: Int) {

            var keyWordNameTextview = itemView.findViewById<TextView>(R.id.BulkDataKeywordname)
            var search = itemView.findViewById<TextView>(R.id.BulksearchVolume)
            var bingsearch = itemView.findViewById<TextView>(R.id.BulkBingsearchVolume)
            var competition = itemView.findViewById<TextView>(R.id.Bulkcompetion)
            var cpc = itemView.findViewById<TextView>(R.id.BulkcpcValue)
            var simiarLayout = itemView.findViewById<LinearLayout>(R.id.BulkSimilarkeywordLayout)

            keyWordNameTextview.text = "Keyword data related to '${data.keyword}'"
            try {
                search.text = if (data.search_volume != "0" || data.search_volume != "") {
                    NumberFormat.getNumberInstance(Locale.US).format(data.search_volume.toDouble());
                } else {
                    "0"
                }
            } catch (E: Exception) {
                search.text = "0"
            } catch (E: java.lang.Exception) {
                search.text = "0"
            }
            try {
                bingsearch.text = if (data.bing_search_volume != "0" || data.bing_search_volume != "") {
                    NumberFormat.getNumberInstance(Locale.US).format(data.bing_search_volume.toDouble());
                } else {
                    "0"
                }
            } catch (E: Exception) {
                bingsearch.text = "0"
            } catch (E: java.lang.Exception) {
                bingsearch.text = "0"
            }
            try {
                competition.text = if (data.competition != "" || data.competition != "") {
                    utils.roundOffDecimal(data.competition.toDouble())
                } else {
                    "0"
                }
            } catch (E: Exception) {
                competition.text = "0"
            } catch (E: java.lang.Exception) {
                competition.text = "0"
            }
            try {
                cpc.text =
                    if (data.cpc.isEmpty() || data.cpc == "") {
                        "0$"
                    } else {
                        DecimalFormat("#.##").format(data.cpc.toDouble()) + "$"
                    }
            } catch (E: Exception) {
                cpc.text = "0"
            } catch (E: java.lang.Exception) {
                cpc.text = "0"
            }


            //set similarwords
            data.similar_keywords.forEach {
                var keyword = it.keyword
                var binding = BulkSimilarKeyworBinding.inflate(LayoutInflater.from(context))
                binding.SimilarKeywordName.text = it.keyword
                binding.SimilarKeywordvalue.text = NumberFormat.getNumberInstance(Locale.US)
                    .format(it.search_volume.toDouble());

                binding.copyButton.setOnClickListener {
                    frag.activity?.let {
                        InterstitialHelper.loadAndShowInterstitial(
                            it, true
                        ) {
                            val clipboard = context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("keyword", keyword)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "'${data.keyword}' Keyword copied", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                simiarLayout.addView(binding.root)
            }

        }
    }

    fun setDataList(list: MutableList<KeywordItem>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myviewholder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bulk_keyword_data, parent, false)
        return myviewholder(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: myviewholder, position: Int) {
        val h = list[position]
        holder.setdata(h, position)
        setFadeAnimation(holder.itemView)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun setFadeAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 100
        view.startAnimation(anim)
    }

}