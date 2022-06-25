package com.seo.app.seostudio.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

import com.seo.app.seostudio.utils.RecyclerviewCallbacks
import com.seo.app.seostudio.utils.roundOffDecimal
import com.seo.app.seostudio.R
import com.seo.app.seostudio.databinding.BulkSimilarKeyworBinding
import com.seo.app.seostudio.models.KeywordItem
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class BulkKeywordAdapter(val context: Context) :
    RecyclerView.Adapter<BulkKeywordAdapter.myviewholder>() {

    var list: MutableList<KeywordItem> = ArrayList()
    var callback: RecyclerviewCallbacks<String>? = null

    inner class myviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setdata(data: KeywordItem, p: Int) {

            var keyWordNameTextview = itemView.findViewById<TextView>(R.id.BulkDataKeywordname)
            var search = itemView.findViewById<TextView>(R.id.BulksearchVolume)
            var bingsearch = itemView.findViewById<TextView>(R.id.BulkBingsearchVolume)
            var competition = itemView.findViewById<TextView>(R.id.Bulkcompetion)
            var cpc = itemView.findViewById<TextView>(R.id.BulkcpcValue)
            var simiarLayout = itemView.findViewById<LinearLayout>(R.id.BulkSimilarkeywordLayout)

            keyWordNameTextview.text = "Keyword data related to '${data.keyword}'"
            search.text = NumberFormat.getNumberInstance(Locale.US)
                .format(data.search_volume.toDouble());
            bingsearch.text = if (data.bing_search_volume != "0") {
                NumberFormat.getNumberInstance(Locale.US)
                    .format(data.bing_search_volume.toDouble());
            } else {
                "0"
            }
            competition.text = roundOffDecimal(data.competition.toDouble())
            cpc.text =
                if (data.cpc.isEmpty()) {
                    "0$"
                } else {
                    DecimalFormat("#.##").format(data.cpc.toDouble()) + "$"
                }


            //set similarwords
            data.similar_keywords.forEach {
                var keyword = it.keyword
                var binding = BulkSimilarKeyworBinding.inflate(LayoutInflater.from(context))
                binding.SimilarKeywordName.text = it.keyword
                binding.SimilarKeywordvalue.text = NumberFormat.getNumberInstance(Locale.US)
                    .format(it.search_volume.toDouble());

                binding.copyButton.setOnClickListener {
                    callback?.onItemClick()
                    val clipboard =
                        context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("keyword", keyword)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "'${data.keyword}' Keyword copied", Toast.LENGTH_SHORT)
                        .show()
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

    fun setOnClick(click: RecyclerviewCallbacks<String>) {
        callback = click
    }
}

