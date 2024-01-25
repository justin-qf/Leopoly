package com.app.leopoly.adpters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.leopoly.R
import com.app.leopoly.apiHandling.orderListResponse.OrderListData
import com.app.leopoly.apiHandling.packgeOrderListResponse.PackageOrderListData
import com.app.leopoly.databinding.SalesOrderItemLayoutBinding
import com.app.leopoly.interfaces.ListClickListener

class PackageSlipDetailAdapter(
    private val act: Activity,
    private var orderList: ArrayList<PackageOrderListData>,
    private var deleteListClickListener: ListClickListener,
    private var itemListClickListener: ListClickListener
) :
    RecyclerView.Adapter<PackageSlipDetailAdapter.DataHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        val binding: SalesOrderItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.sales_order_item_layout,
            parent,
            false
        )

        return DataHolder(binding)
    }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        setItemAnimate(holder)
        val model = orderList[position]
        setData(holder.binding.psNoTxt, model.INVNO)
        holder.binding.partyNameLabel.text = "PARTY NAME : "+  model.PNAME
        setData(holder.binding.itemNameTxt, model.ITEMNAME)

        holder.binding.itemClick.setOnClickListener {
            itemListClickListener.onClickListener(
                holder.binding.itemClick,
                position,
                model
            )
        }

//        setData(holder.binding.sizeTxt, model.SIZE)
//        setData(holder.binding.boxesTxt, model.BOXES)
//        setData(holder.binding.grossWTxt, model.GROSSWT)
//        setData(holder.binding.netWTxt, model.NETWT)
//        setData(holder.binding.unitTxt, model.UNIT)

//        holder.binding.deleteItem.setOnClickListener {
//            deleteListClickListener.onClickListener(
//                holder.binding.deleteItem,
//                position,
//                model
//            )
//        }
    }

    private fun setData(textView: TextView, data: String?) {
        if (data != null && data != "null") {
            textView.text = data
        } else {
            textView.text = act.getText(R.string.not_filled)
        }
    }


    override fun getItemCount(): Int {
        return orderList.size
    }

    private fun setItemAnimate(holder: DataHolder) {
        // Set initial translationX value to make the items appear outside the screen to the left
        holder.itemView.translationX = -holder.itemView.width.toFloat()
        // Apply the slide-in left animation
        holder.itemView.animate()
            .translationX(0f)
            .setDuration(500) // Adjust the animation duration as needed
            .start()
    }

    class DataHolder(itemView: SalesOrderItemLayoutBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: SalesOrderItemLayoutBinding

        init {
            binding = itemView
        }
    }
}
