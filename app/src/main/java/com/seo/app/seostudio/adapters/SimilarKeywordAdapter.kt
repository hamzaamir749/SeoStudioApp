package com.seo.app.seostudio.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.seo.app.seostudio.R
import com.seo.app.seostudio.models.SimilarKeyword
import java.text.NumberFormat
import java.util.*

class SimilarKeywordAdapter(val context: Context) :
    RecyclerView.Adapter<SimilarKeywordAdapter.myviewholder>() {

    var list: MutableList<SimilarKeyword> = ArrayList()

    inner class myviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setdata(data: SimilarKeyword, p: Int) {

            var titile = itemView.findViewById<TextView>(R.id.SimilarKeywordName)
            var value = itemView.findViewById<TextView>(R.id.SimilarKeywordvalue)
            var copy = itemView.findViewById<ImageView>(R.id.copyButton)
            var cardview = itemView.findViewById<MaterialCardView>(R.id.SimilarItem)
            titile.text = data.keyword
            value.text = NumberFormat.getNumberInstance(Locale.US)
                .format(data.search_volume.toDouble());
            /* cardview.setOnClickListener {
                 if (frag is SingleFragment) {
                     frag.openSimilar(data.keyword)
                 }
             }*/
            copy.setOnClickListener {

                val clipboard =
                    context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("keyword", data.keyword)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "'${data.keyword}' Keyword copied", Toast.LENGTH_SHORT)
                    .show()


            }

        }
    }

    fun setDataList(list: MutableList<SimilarKeyword>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myviewholder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.similar_item_layout, parent, false)
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