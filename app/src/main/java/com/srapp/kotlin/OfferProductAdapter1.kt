package com.srapp.kotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.srapp.R
import java.util.*

class OfferProductAdapter1(
    private val mContext: Context,
    private val currentOfferProductList: ArrayList<HashMap<String, String>>
) :

    RecyclerView.Adapter<OfferProductAdapter1.BonusCamViewHolder>() {

    private var filteredOfferProductList: ArrayList<HashMap<String, String>>? = null

    inner class BonusCamViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var txtViewMinQty: TextView
        var txtViewPolicyType: TextView
        var txtViewDisAmt: TextView
        var txtViewDisProName: TextView
        var txtViewDisProPrice: TextView
        var txtViewBonusPro: TextView
        var txtViewBonusProQty: TextView
        var txtViewFormula: TextView

        var txtDisAmtTitle: TextView
        var txtFormulaTitle: TextView
        var lebel: TextView
        var txtDisProductNameTitle: TextView
        var txtDisPriceTitle: TextView
        var txtBonusProductNameTitle: TextView
        var txtBonusQtyTitle: TextView

        init {
            txtViewMinQty = view.findViewById<View>(R.id.txtMinQty) as TextView
            txtViewPolicyType = view.findViewById<View>(R.id.txtPolicyType) as TextView
            txtViewDisAmt = view.findViewById<View>(R.id.txtDisAmt) as TextView
            lebel = view.findViewById<View>(R.id.lebel) as TextView
            txtViewDisProName = view.findViewById<View>(R.id.txtDisProductName) as TextView
            txtViewDisProPrice = view.findViewById<View>(R.id.txtDisPrice) as TextView
            txtViewBonusPro = view.findViewById<View>(R.id.txtBonusProductName) as TextView
            txtViewBonusProQty = view.findViewById<View>(R.id.txtBonusQty) as TextView
            txtViewFormula = view.findViewById<View>(R.id.txtFormula) as TextView

            txtDisAmtTitle = view.findViewById<View>(R.id.txtDisAmtTitle) as TextView
            txtFormulaTitle = view.findViewById<View>(R.id.txtFormulaTitle) as TextView
            txtDisProductNameTitle = view.findViewById<View>(R.id.txtDisProductNameTitle) as TextView
            txtDisPriceTitle = view.findViewById<View>(R.id.txtDisPriceTitle) as TextView
            txtBonusProductNameTitle = view.findViewById<View>(R.id.txtBonusProductNameTitle) as TextView
            txtBonusQtyTitle = view.findViewById<View>(R.id.txtBonusQtyTitle) as TextView
        }
    }

    init {
        this.filteredOfferProductList = currentOfferProductList
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BonusCamViewHolder {
        // Inflate the custom layout
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.offer_product_single_data_view, parent, false)
        // Return a new holder instance
        return BonusCamViewHolder(itemView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: BonusCamViewHolder, position: Int) {
        // Get the item model based on position
        val bonusCam = filteredOfferProductList?.get(position)

        holder.txtViewMinQty.text = bonusCam?.get("minQtyUnit")
        if (bonusCam?.get("minQtyUnit")!!.split(" ")[0].toInt()==0){
            holder.lebel.text = "Min Value:"
            holder.txtViewMinQty.text ="${bonusCam?.get("min_value")} BDT"
        }
        holder.txtViewPolicyType.text = bonusCam?.get("policyType")
        if (bonusCam?.get("disAmount").equals(null)){
            holder.txtViewDisAmt.visibility=View.GONE
            holder.txtDisAmtTitle.visibility=View.GONE
            holder.txtViewDisProName.visibility=View.GONE
            holder.txtViewDisProPrice.visibility=View.GONE
            holder.txtDisProductNameTitle.visibility=View.GONE
            holder.txtDisPriceTitle.visibility=View.GONE
        }
        else{
            when {
                bonusCam?.get("disType")!! == "0" -> {
                    holder.txtViewDisAmt.text = bonusCam?.get("disAmount") +" %"
                }
                bonusCam["disType"]!! == "1" -> {
                    holder.txtViewDisAmt.text = bonusCam?.get("disAmount") +" Tk."
                }
                else -> {
                    holder.txtViewDisAmt.text = bonusCam["disAmount"]
                }
            }

            holder.txtViewDisProName.text = bonusCam["disProName"]
            holder.txtViewDisProPrice.text = bonusCam["disProPrice"]
        }

        when{
            bonusCam?.get("policyType").equals("Discount") ->{
                holder.txtViewBonusPro.visibility =View.GONE
                holder.txtViewBonusProQty.visibility=View.GONE
                holder.txtBonusProductNameTitle.visibility=View.GONE
                holder.txtBonusQtyTitle.visibility=View.GONE
            }

            else ->{
                holder.txtViewBonusPro.visibility =View.VISIBLE
                holder.txtViewBonusProQty.visibility=View.VISIBLE
                holder.txtBonusProductNameTitle.visibility=View.VISIBLE
                holder.txtBonusQtyTitle.visibility=View.VISIBLE

                holder.txtViewBonusPro.text = bonusCam?.get("bonusProName")
                holder.txtViewBonusProQty.text = bonusCam?.get("bonusProQty")
            }
        }

        if (bonusCam?.get("formula").equals("")){
            holder.txtViewFormula.visibility=View.GONE
            holder.txtFormulaTitle.visibility=View.GONE
        }
        else{
            holder.txtViewFormula.text = bonusCam?.get("formula")
        }
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return filteredOfferProductList?.size!!
    }

}