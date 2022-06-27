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
import java.util.*


class OfferProductAdapter(
    private val mContext: Context,
    private val currentOfferProductList: ArrayList<HashMap<String, String>>
) :

    RecyclerView.Adapter<OfferProductAdapter.BonusCamViewHolder>() {

    private var filteredOfferProductList: ArrayList<HashMap<String, String>>? = null

    inner class BonusCamViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var productBonusName: TextView
        var startDate: TextView
        var endDate: TextView
        var action: ImageView

        init {
            productBonusName = view.findViewById<View>(R.id.policyName) as TextView
            startDate = view.findViewById<View>(R.id.stDate) as TextView
            endDate = view.findViewById<View>(R.id.endDate) as TextView
            action = view.findViewById<View>(R.id.view) as ImageView
        }
    }

    init {
        this.filteredOfferProductList = currentOfferProductList
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BonusCamViewHolder {
        // Inflate the custom layout
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.current_offer_product_item, parent, false)
        // Return a new holder instance
        return BonusCamViewHolder(itemView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: BonusCamViewHolder, position: Int) {
        // Get the item model based on position
        val bonusCam = filteredOfferProductList?.get(position)

        holder.productBonusName.text = bonusCam?.get("policy_name")
        holder.startDate.text = bonusCam?.get("start_date")
        holder.endDate.text = bonusCam?.get("end_date")

        holder.action.setOnClickListener {
            val intent = Intent(mContext, OfferDetailsActivity1::class.java)
            intent.putExtra("motherProduct",bonusCam?.get("policy_name"))
            intent.putExtra("policyId",bonusCam?.get("policy_id"))
           // intent.putExtra("optionId",bonusCam?.get("option_id"))
            mContext.startActivity(intent)
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return filteredOfferProductList?.size!!
    }

}