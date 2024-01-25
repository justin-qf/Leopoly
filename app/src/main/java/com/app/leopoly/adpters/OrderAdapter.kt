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
import androidx.core.text.HtmlCompat
import com.app.leopoly.R
import com.app.leopoly.apiHandling.orderListResponse.OrderListData
import com.app.leopoly.helper.HELPER.print
import com.app.leopoly.helper.HELPER.setTextColour
import com.app.leopoly.interfaces.ListClickListener
import com.google.gson.Gson
import java.util.*

class OrderAdapter(
    val activity: Activity, //activity context
    val context: Activity, //the layout resource file for the list items
    val resource: Int, //the list values in the List of type hero
    var heroList: ArrayList<OrderListData>, id: Int,
    private var listClickListener: ListClickListener
) : ArrayAdapter<OrderListData?>(
    context, resource, heroList as List<OrderListData?>
), Filterable {
    var selectedId: Int
    var tmpArray: ArrayList<OrderListData>
    var cust: CustomeFilter? = null
    private var mSelectedPosition = -1
    var selectionEvent: handleSelectionEvent? = null

    interface handleSelectionEvent {
        fun selectionEvent(itemmodel: OrderListData?, positino: Int)
    }

    private var isPartyMode = false
    fun setPartyMode(partyMode: Boolean) {
        isPartyMode = partyMode
    }

    fun setInteface(inteface: handleSelectionEvent?) {
        selectionEvent = inteface
    }

    //constructor initializing the values
    init {
        tmpArray = heroList
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
        itemName.text = hero.iNVNO
        print("getITEMNAME", hero.iNVNO)
        if (selectedId == hero.pRTYID!!.toInt()) {
            setTextColour(context as Activity, itemName, R.color.colorPrimary)
        }
        itemName.setOnClickListener { listClickListener.onClickListener(itemName, position, hero)}
        return convertView
    }

    override fun getCount(): Int {
        return heroList.size
    }

    override fun getItem(position: Int): OrderListData? {
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
                val tmpry = ArrayList<OrderListData>()
                for (i in tmpArray.indices) {
                    if (tmpArray[i].iTEMNAME!!.lowercase(Locale.getDefault())
                            .contains(constraints)
                    ) {
                        val gson = Gson()
                        val tmpStr = gson.toJson(tmpArray[i])
                        val model = gson.fromJson(tmpStr, OrderListData::class.java)
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
            heroList = filterResults.values as ArrayList<OrderListData>
            notifyDataSetChanged()
        }
    }

    companion object {
        fun printHtmlText(text: String?, textView: TextView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.text = Html.fromHtml(text, Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
            } else {
                textView.text = HtmlCompat.fromHtml(text!!, 0)
            }
        }
    }
}