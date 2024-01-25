package com.app.leopoly.adpters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.app.leopoly.R
import com.app.leopoly.adpters.DropDownAdpt.TenamentHolder
import com.google.gson.Gson

class DropDownAdpt(
    private val act: Activity,
    private val arrayList: ArrayList<String>,
    private val calledFlag: Int
) : RecyclerView.Adapter<TenamentHolder>() {
    private val checkedPosition = -1
    private var isFilterMode = false
    //var layout: dissmissLayout? = null
            var id: dissmissLayout? = null
    var dismissLayout: DismissLayout? = null

    interface dissmissLayout {
        fun onClick()
    }

    fun setLayout(layout: dissmissLayout?) {
        this.id = layout
    }

    init {
        val gson = Gson()
    }

    fun setFilterMode(filterMode: Boolean) {
        isFilterMode = filterMode
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TenamentHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_layout, parent, false)
        return TenamentHolder(view)
    }

    override fun onBindViewHolder(holder: TenamentHolder, position: Int) {
        holder.radioButton.text = arrayList[position]
        holder.radioButton.setOnClickListener { id!!.onClick() }
    }

    interface DismissLayout {
        fun onSelection()
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class TenamentHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioButton: RadioButton

        init {
            radioButton = itemView.findViewById(R.id.radioButton)
        }
    }
}