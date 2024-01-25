package com.app.leopoly.adpters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.app.leopoly.R
import java.util.*

class UnitAdpter(
    val activity: Activity, //activity context
    val context: Activity, //the layout resource file for the list items
    val resource: Int, //the list values in the List of type hero
    var heroList: List<String>
) : ArrayAdapter<String?>(
    context, resource, heroList
), Filterable {
    var tmpArray: List<String> = heroList
    var cust: CustomeFilter? = null
    private var mSelectedPosition = -1
    var selectionEvent: handleSelectionEvent? = null

    interface handleSelectionEvent {
        fun selectionEvent(itemmodel: String?, positino: Int)
    }

    fun setInteface(inteface: handleSelectionEvent?) {
        selectionEvent = inteface
    }

    //this will return the ListView Item as a View
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val layoutInflater = LayoutInflater.from(context)
        convertView = layoutInflater.inflate(resource, null, false)
        val itemName = convertView.findViewById<TextView>(R.id.labeled)
        //adding values to the list item
        itemName.text = heroList[position]
        itemName.setOnClickListener {
            selectionEvent!!.selectionEvent(
                heroList[position], position
            )
        }
        return convertView
    }

    override fun getCount(): Int {
        return heroList.size
    }

    override fun getItem(position: Int): String? {
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

    fun setSelectedPosition(mSelectedPosition: Int) {
        this.mSelectedPosition = mSelectedPosition
    }

    inner class CustomeFilter : Filter() {
        override fun performFiltering(constraints: CharSequence): FilterResults {
            var constraints: CharSequence? = constraints
            val filterResults = FilterResults()
            if (constraints != null && constraints.length > 5) {
                constraints = constraints.toString().lowercase(Locale.getDefault())
                val tmpry: MutableList<String> = ArrayList()
                for (i in tmpArray.indices) {
                    if (tmpArray[i].lowercase(Locale.getDefault()).contains(constraints)) {
                        tmpry.add(tmpArray[i])
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
            heroList = filterResults.values as List<String>
            notifyDataSetChanged()
        }
    }
}