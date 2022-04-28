package com.bluixe.lecture2

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import android.content.res.AssetManager
import android.graphics.Color
import java.io.File
import java.io.InputStream


class SearchItemAdapter(activity:MainActivity) : RecyclerView.Adapter<SearchItemAdapter.SearchItemViewHolder>() {

    private val contentList = mutableListOf<String>()
    private val filteredList = mutableListOf<String>()
    private val mainActivity = activity



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder { //返回值类型为SearchItemViewHolder
        val v = View.inflate(parent.context, R.layout.search_item_layout, null)
        val view = SearchItemViewHolder(v)
        view.itemView.setOnClickListener {
            val position = view.adapterPosition
//            val text = contentList[position]
            val info = mainActivity.getInfo()
            val infoItem = info?.get(position)
            val intent = Intent(mainActivity, Details::class.java)
            intent.putExtra("info", infoItem)
            mainActivity.startActivity(intent)
//            Toast.makeText(parent.context, infoItem, Toast.LENGTH_SHORT).show()

        }

        return view
    }

    override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) {
        holder.bind(position, filteredList[position], mainActivity.difficulty[position])
    }

    override fun getItemCount(): Int = filteredList.size



    fun setContentList(list: List<String>) {
        contentList.clear()
        contentList.addAll(list)
        filteredList.clear()
        filteredList.addAll(list)
        notifyDataSetChanged()
    }

    fun setFilter(keyword: String?) {
        filteredList.clear()
        if (keyword?.isNotEmpty() == true) {
            filteredList.addAll(contentList.filter { it.contains(keyword) })
        } else {
            filteredList.addAll(contentList)
        }
        notifyDataSetChanged()
    }

    class SearchItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tv = view.findViewById<TextView>(R.id.search_item_tv)
        private val diff = view.findViewById<TextView>(R.id.difficulty)
        private val colorMap = mapOf<String, String>("EASY" to "#2E7D32", "MEDIUM" to "#F9A825", "HARD" to "#C62828")
        private val diffMap = mapOf<String, String>("EASY" to "简单", "MEDIUM" to "中等", "HARD" to "困难")

        fun bind(position: Int, content: String, difficulty: String) {
            val pos = "${(position+1).toString()}.$content"
            tv.text = pos
            diff.text = diffMap[difficulty]
            diff.setTextColor(Color.parseColor(colorMap[difficulty]))

        }
    }

}