package com.app.leopoly.adpters

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.app.leopoly.R
import com.app.leopoly.models.MultiModel
import com.google.gson.Gson
import java.util.*

class ProductAdpter(
    val activity: Activity, //activity context
    val context: Activity, //the layout resource file for the list items
    val resource: Int, //the list values in the List of type hero
    var heroList: ArrayList<MultiModel>
) : ArrayAdapter<MultiModel?>(
    context, resource, heroList as List<MultiModel?>
), Filterable {
    var tmpArray: ArrayList<MultiModel> = heroList
    var cust: CustomeFilter? = null
    private var mSelectedPosition = -1
    var selectionEvent: handleSelectionEvent? = null

    interface handleSelectionEvent {
        fun selectionEvent(itemmodel: MultiModel?, positino: Int)
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
        val hero = heroList[position]
        //adding values to the list item
        itemName.text = hero.productName
        itemName.setOnClickListener { selectionEvent!!.selectionEvent(hero, position) }
        if (hero.isHighLighted) {
            itemName.setTextColor(ContextCompat.getColor(activity, R.color.colorBlack))
            itemName.setTypeface(itemName.typeface, Typeface.BOLD)
        }
        return convertView
    }

    override fun getCount(): Int {
        return heroList.size
    }

    override fun getItem(position: Int): MultiModel? {
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
            if (constraints != null && constraints.length > 1) {
                constraints = constraints.toString().lowercase(Locale.getDefault())
                val tmpry = ArrayList<MultiModel>()
                for (i in tmpArray.indices) {
                    if (tmpArray[i].productName!!.lowercase(Locale.getDefault())
                            .startsWith(constraints.toString())
                    ) {
                        val gson = Gson()
                        val tmpStr = gson.toJson(tmpArray[i])
                        val model = gson.fromJson(tmpStr, MultiModel::class.java)
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
            heroList = filterResults.values as ArrayList<MultiModel>
            notifyDataSetChanged()
        }
    }
}