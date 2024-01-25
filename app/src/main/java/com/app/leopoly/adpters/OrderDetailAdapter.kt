package com.app.leopoly.adpters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.leopoly.R
import com.app.leopoly.apiHandling.orderDetailListResponse.OrderDetailListData
import com.app.leopoly.databinding.OrderItemLayoutBinding
import com.app.leopoly.helper.HELPER
import com.app.leopoly.interfaces.ListClickListener

class OrderDetailAdapter(
    private val act: Activity,
    private var orderDetailList: ArrayList<OrderDetailListData>,
    private var deleteListClickListener: ListClickListener
) :
    RecyclerView.Adapter<OrderDetailAdapter.DataHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        val binding: OrderItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.order_item_layout,
            parent,
            false
        )

        return DataHolder(binding)
    }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        setItemAnimate(holder)
        val model = orderDetailList[position]
        HELPER.print("BARCODE",model.BARCODE.toString())
        setData(holder.binding.barcodeTxt, model.BARCODE)
        setData(holder.binding.rollNoTxt, model.ROLLNO)
        setData(holder.binding.sizeTxt, model.SIZE)
        setData(holder.binding.boxesTxt, model.BOXES)
        setData(holder.binding.grossWTxt, model.GROSSWT)
        setData(holder.binding.netWTxt, model.NETWT)
        setData(holder.binding.unitTxt, model.UNIT)

        holder.binding.deleteItem.setOnClickListener {
            deleteListClickListener.onClickListener(
                holder.binding.deleteItem,
                position,
                model
            )
        }
    }

    private fun setData(textView: TextView, data: String?) {
        if (data != null && data != "null") {
            textView.text = data
        } else {
            textView.text = act.getText(R.string.not_filled)
        }
    }


    override fun getItemCount(): Int {
        return orderDetailList.size
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

    class DataHolder(itemView: OrderItemLayoutBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: OrderItemLayoutBinding

        init {
            binding = itemView
        }
    }
}
