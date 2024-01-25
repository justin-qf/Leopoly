package com.app.leopoly.adpters

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.app.leopoly.R
import com.app.leopoly.apiHandling.partyResponse.PartyListData
import com.app.leopoly.apiHandling.partyResponse.TransportListData
import com.app.leopoly.helper.HELPER.setTextColour
import com.app.leopoly.interfaces.ListClickListener
import com.google.gson.Gson
import java.util.*

class TransportAdapter(
    val activity: Activity, //activity context
    val context: Activity, //the layout resource file for the list items
    val resource: Int, //the list values in the List of type hero
    var heroList: ArrayList<TransportListData>, id: Int,
    private var listClickListener: ListClickListener,

    ) : ArrayAdapter<TransportListData?>(
    context, resource, heroList as List<TransportListData?>
), Filterable {
    private var selectedId: Int
    var tmpArray: ArrayList<TransportListData> = heroList
    private var cust: CustomeFilter? = null
    private var mSelectedPosition = -1
    private var isPartyMode = false
    fun setPartyMode(partyMode: Boolean) {
        isPartyMode = partyMode
    }

    init {
        selectedId = id
    }

    //this will return the ListView Item as a View
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val layoutInflater = LayoutInflater.from(context)
        convertView = layoutInflater.inflate(resource, null, false)
        val itemName = convertView.findViewById<TextView>(R.id.labeled)
        val hero = heroList[position]
        //adding values to the list item
        itemName.text = hero.TRPNAME
        if (selectedId == hero.TRPID!!.toInt()) {
            setTextColour(context, itemName, R.color.colorPrimary)
        }
        itemName.setOnClickListener { listClickListener.onClickListener(itemName, position, hero) }
        return convertView
    }

    override fun getCount(): Int {
        return heroList.size
    }

    override fun getItem(position: Int): TransportListData? {
        return heroList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {
        if (cust == null) {
            cust = CustomeFilter()
        }
        return cust!!
    }

    inner class CustomeFilter : Filter() {
        override fun performFiltering(constraints: CharSequence): FilterResults {
            var constraints: CharSequence? = constraints
            val filterResults = FilterResults()
            if (constraints != null && constraints.length > 1) {
                constraints = constraints.toString().lowercase(Locale.getDefault())
                val tmpry = ArrayList<TransportListData>()
                for (i in tmpArray.indices) {
                    if (tmpArray[i].TRPNAME!!.lowercase(Locale.getDefault()).contains(constraints)) {
                        val gson = Gson()
                        val tmpStr = gson.toJson(tmpArray[i])
                        val model = gson.fromJson(tmpStr, TransportListData::class.java)
                        tmpry.add(model)
                    }
                }
                filterResults.count = tmpry.size
                filterResults.values = tmpry
            } else {
                filterResults.count = tmpArray.size
                filterResults.values = tmpArray
            }
            return filterResults
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            heroList = filterResults.values as ArrayList<TransportListData>
            notifyDataSetChanged()
        }
    }
}