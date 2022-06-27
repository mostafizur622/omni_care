package com.srapp.kotlin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.srapp.R

class BonusCamAdapter(
    private val mContext: Context,
    private val bonusCampaignList: List<BonusCampaign.BonusCamInfo>
) :
    RecyclerView.Adapter<BonusCamAdapter.BonusCamViewHolder>() {
    private var filteredBonusCamList: List<BonusCampaign.BonusCamInfo>? = null

    inner class BonusCamViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var productBonusName: TextView
        var action: ImageView
        var startDate: TextView
        var endDate: TextView

        init {
            productBonusName = view.findViewById<View>(R.id.productBonus) as TextView
            startDate = view.findViewById<View>(R.id.sDate) as TextView
            endDate = view.findViewById<View>(R.id.eDate) as TextView
            action = view.findViewById<View>(R.id.view) as ImageView
        }
    }

    init {
        this.filteredBonusCamList = bonusCampaignList
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BonusCamViewHolder {
        // Inflate the custom layout
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.bonus_campaign_item, parent, false)
        // Return a new holder instance
        return BonusCamViewHolder(itemView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: BonusCamViewHolder, position: Int) {
        // Get the item model based on position
        val bonusCam = filteredBonusCamList?.get(position)
        holder.productBonusName.text = bonusCam?.getProductList()
        holder.startDate.text = bonusCam?.getStartDate()
        holder.endDate.text = bonusCam?.getEndDate()

        holder.action.setOnClickListener {
            val intent = Intent(mContext, BonusDetailsActivity::class.java)
            intent.putExtra("bonusDetails", bonusCam?.getDetails())
            intent.putExtra("attachment", bonusCam?.getAttachmentLink())
            intent.putExtra("motherProduct", bonusCam?.getProductList())
            mContext.startActivity(intent)
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return filteredBonusCamList?.size!!
    }

}