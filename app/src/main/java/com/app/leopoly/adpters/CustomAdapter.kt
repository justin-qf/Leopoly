package com.app.leopoly.adpters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.app.leopoly.R
import com.app.leopoly.models.MultiModel
import java.util.*

// Creating an Adapter Class
class CustomAdapter(context: Context?, var customers: ArrayList<MultiModel>) :
    ArrayAdapter<MultiModel?>(
        context!!, android.R.layout.simple_list_item_1, customers as List<MultiModel?>
    ) {
    var tempCustomer: ArrayList<MultiModel> = ArrayList(customers)
    var suggestions: ArrayList<MultiModel> = ArrayList(customers)
    override fun getItem(position: Int): MultiModel? {
        return suggestions[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val customer = getItem(position)
        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.item_party_layout, parent, false)
        }
        val txtCustomer = convertView!!.findViewById<View>(R.id.itemTxt) as TextView
        if (txtCustomer != null) txtCustomer.text = customer!!.party_name
        return convertView
    }

    override fun getFilter(): Filter {
        return myFilter
    }

    var myFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            val customer = resultValue as MultiModel
            return customer.party_name!!
        }

        override fun performFiltering(constraint: CharSequence): FilterResults {
            return if (constraint != null) {
                suggestions!!.clear()
                for (people in tempCustomer!!) {
                    if (people.party_name!!.lowercase(Locale.getDefault()).startsWith(
                            constraint.toString().lowercase(
                                Locale.getDefault()
                            )
                        )
                    ) {
                        suggestions!!.add(people)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions!!.size
                filterResults
            } else {
                FilterResults()
            }
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            val c = results.values as ArrayList<MultiModel>
            if (results != null && results.count > 0) {
                clear()
                for (cust in c) {
                    add(cust)
                    notifyDataSetChanged()
                }
            }
        }
    }


}