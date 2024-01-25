package com.app.leopoly.adpters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.leopoly.R
import com.app.leopoly.activity.OrderActivity.OrderFormActivity
import com.app.leopoly.activity.SalesOrderActivity.SalesOrderActivity
import com.app.leopoly.activity.SalesOrderSlipUpdate.PackageSlipListActivity
import com.app.leopoly.adpters.DashboardMenuAdpter.MenuHolder
import com.app.leopoly.common.Constant
import com.app.leopoly.common.RoundLinerLayoutNormal
import com.app.leopoly.models.DashboardModel

class DashboardMenuAdpter(
    private val menuList: ArrayList<DashboardModel>,
    private val activity: Activity
) : RecyclerView.Adapter<MenuHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val itemView =
            LayoutInflater.from(activity).inflate(R.layout.item_dashboard_layout, parent, false)
        return MenuHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        val model = menuList[position]
        holder.menuNameTxt.text = model.menuName
        holder.menuIconImg.setImageResource(model.menuImage)
//        if (position == 1) {
//            holder.menuIconImg.setPadding(0, 0, 0, 0)
//        }
        holder.itemLayout.setOnClickListener { makeIntent(model.menuId) }
    }

    private fun makeIntent(intentFlag: Int) {
        var intent: Intent? = null
        when (intentFlag) {
            Constant.PACKING_SLIP -> {
                intent = Intent(activity, OrderFormActivity::class.java)
            }
            Constant.SALES_ORDERs -> {
                intent = Intent(activity, SalesOrderActivity::class.java)
            }
            Constant.DISPATCH -> {
                intent = Intent(activity, PackageSlipListActivity::class.java)
            }
        }
        if (intent != null) activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.right_enter, R.anim.left_out)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuNameTxt: TextView
        val itemLayout: RoundLinerLayoutNormal
        val menuIconImg: ImageView

        // MaterialCardView cardView;
        init {
            menuIconImg = itemView.findViewById(R.id.menuIconImg)
            menuNameTxt = itemView.findViewById(R.id.menuNameTxt)
            itemLayout = itemView.findViewById(R.id.itemLayout)
        }
    }
}