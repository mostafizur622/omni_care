package com.srapp.kotlin

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.srapp.Dashboard
import com.srapp.Db_Actions.Data_Source
import com.srapp.Db_Actions.Tables
import com.srapp.R
import com.srapp.Util.ParentActivity
import kotlin.collections.set
import kotlin.math.floor

class OfferDetailsActivity1 : ParentActivity() {

    var HeaderTitleTv: TextView? = null
    var txtUser: TextView? = null
    var btnBack: Button? = null
    var btnHome: Button? = null

    var db: Data_Source? = null
    var rootProduct: String? = ""
    var bProductName: String? = ""
    var bProductQty: String? = ""
    var productName: String? = ""
    var productPrice: String? = ""

    var policyOption: String? = ""
    var minqrty: String? = ""

    var bonusProduct: String? = ""

    var productItemMap = ArrayList<HashMap<String, String>>()

    var recyclerView: RecyclerView? = null

    var toolbar: Toolbar? = null

    var home: ImageView? = null
    var back: ImageView? = null
    var userIdTV: TextView? = null
    var titleTV: TextView? = null
    var policyName: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.offer_details)

        recyclerView = findViewById(R.id.recyclerView)

        initViews()

        initRecyclerView()
    }

    private fun initViews() {

        val motherProduct: TextView = findViewById(R.id.motherProduct)

        val detailsMessage: TextView = findViewById(R.id.bonusDetails)

        db = Data_Source(this)

        val intent = intent

        val mProduct = intent.getStringExtra("motherProduct")

        val policyId = intent.getStringExtra("policyId")

        RootProductNameGet(policyId.toString())

        PolicyOptionDataGet(policyId.toString())

        motherProduct.text = "Mother product"

        detailsMessage.text = rootProduct

        userIdTV = findViewById(R.id.user_txt_view)
        policyName = findViewById(R.id.policyName)
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val value = prefs.getString(Tables.SR_ID, "0")

        userIdTV?.text = value

        policyName!!.text = mProduct

        ReturnBackFunction()
    }

    private fun RootProductNameGet(policyId: String) {

        val cursorProduct = db!!.rawQueryCoustom(
            "SELECT product.product_name\n" +
                    "FROM product \n" +
                    "INNER JOIN policy_root_product ON " +
                    "policy_root_product.root_product_id = product.product_id WHERE policy_id='" + policyId + "'"
        )

        Log.i(ContentValues.TAG, cursorProduct.toString())
        Log.e("Current_product:", cursorProduct.toString())

        if (cursorProduct != null) {
            if (cursorProduct.moveToFirst()) {
                do {
                    rootProduct += "\n" + cursorProduct.getString(0)
                } while (cursorProduct.moveToNext())
            }
        }
    }

    private fun ProductNameAndPriceGet(cursorPolicyOption: Cursor, disPrice: String) {

        //--------------------Product  and price ----------------------------------
        val cursorProducts = db!!.rawQueryCoustom(
            "SELECT product.product_name, price\n" +
                    "from product_combinations pc\n" +
                    "INNER join policy_option_price_slab pop on pc.slab_id=pop.slab_id\n" +
                    "INNER join product on pc.product_id=product.product_id\n" +
                    "where policy_product_option_id='" + cursorPolicyOption.getString(6) + "'"
        )

        Log.i(ContentValues.TAG, cursorProducts.toString())
        Log.e("Current_product:", cursorProducts.toString())

        if (cursorProducts != null) {
            if (cursorProducts.moveToFirst()) {
                do {
                    productName += cursorProducts.getString(0) + "\n"
                    val disPriceAmt=cursorProducts.getString(1).toDouble() * disPrice.toDouble()/100
                    Log.e("disPriceAmt__", disPriceAmt.toString()+cursorProducts.getString(1))
                    val disProductPrice=cursorProducts.getString(1).toDouble()-disPriceAmt
                    productPrice += cursorProducts.getString(1) + "\n"
                } while (cursorProducts.moveToNext())
            }
        }
    }

    private fun BonusProductAndQtyGet(cursorPolicyOption: Cursor) {

        //--------------------Bonus product and Qty ----------------------------------
        val cursorBonusProduct = db!!.rawQueryCoustom(
            "SELECT product_name, \n" +
                    "policy_bonus_product.bonus_qty, \n" +
                    "unit.unit_name\n" +
                    "from product \n" +
                    "INNER join policy_bonus_product on product.product_id=policy_bonus_product.bonus_product_id\n" +
                    " INNER join unit on policy_bonus_product.unit_id=unit.unit_id\n" +
                    "where option_id='" + cursorPolicyOption.getString(6) + "'"
        )

        Log.i(ContentValues.TAG, cursorBonusProduct.toString())
        Log.e("Current_product:", cursorBonusProduct.toString())

        if (cursorBonusProduct != null) {
            if (cursorBonusProduct.moveToFirst()) {
                do {
                    bProductName += cursorBonusProduct.getString(0) + "\n"
                    bProductQty += cursorBonusProduct.getString(1) + " (" + cursorBonusProduct.getString(
                        2
                    ) + ") " + "\n"

                } while (cursorBonusProduct.moveToNext())
            }
        }
    }
    private fun getUnitName(unit_id: String?): String {

        val unitquery = db!!.sqLiteDatabase.rawQuery("select unit_name from unit where unit_id='$unit_id'",null)
        unitquery.moveToFirst();
        if (unitquery.count>0 && unitquery!=null){
            return unitquery.getString(0)
        }

        return ""
    }
    private fun PolicyOptionDataGet(policyId: String) {

        //--------------------Policy Option----------------------------------
        val cursorPolicyOption = db!!.rawQueryCoustom(
            "SELECT \n" +
                    "\tmain_min_qty,\n" +
                    "\tunit_id,\n" +
                    "\tpolicy_type,\n" +
                    "\tdiscount_amount,\n" +
                    "\tdiscount_type,\n" +
                    "\tformula_text,\n" +
                    "\toption_id,\n" +
                    "\tmin_value\n" +
                    " from policy_product_Option \n" +
                    "where policy_id='" + policyId + "'"
        )

        Log.i(ContentValues.TAG, cursorPolicyOption.toString())

        Log.e("Current_product_:", cursorPolicyOption.toString())

        if (cursorPolicyOption != null) {
            if (cursorPolicyOption.moveToFirst()) {
                do {

                    val pmap = HashMap<String, String>()
                    pmap["minQtyUnit"] =
                        cursorPolicyOption.getString(0) + " " + getUnitName(cursorPolicyOption.getString(1))
                    when {
                        cursorPolicyOption.getString(2).equals("0") -> {
                            pmap["policyType"] = "Discount"
                        }

                        cursorPolicyOption.getString(2).equals("1") -> {
                            pmap["policyType"] = "Only Bonus"
                        }

                        cursorPolicyOption.getString(2).equals("2") -> {
                            pmap["policyType"] = "Dis & Bonus"
                        }
                        else -> {
                            pmap["policyType"] = "Dis/Bonus"
                        }
                    }

                    if (!cursorPolicyOption.getString(3).equals("null")) {
                        pmap["disAmount"] = floor(cursorPolicyOption.getString(3).toDouble()).toString()
                    }

                    BonusProductAndQtyGet(cursorPolicyOption)
                    ProductNameAndPriceGet(cursorPolicyOption, cursorPolicyOption.getString(3))

                    pmap["disProName"] = productName.toString()
                    pmap["disProPrice"] = productPrice.toString()
                    pmap["bonusProName"] = bProductName.toString()
                    pmap["bonusProQty"] = bProductQty.toString()
                    pmap["disType"] = cursorPolicyOption.getString(4)
                    pmap["formula"] = cursorPolicyOption.getString(5)
                    pmap["min_value"] = cursorPolicyOption.getString(7)

                    productItemMap.add(pmap)
                    bProductName = ""
                    bProductQty = ""
                    productName = ""
                    productPrice = ""

                } while (cursorPolicyOption.moveToNext())
            }
        }
    }

    private fun initRecyclerView() {
        val offerProductAdapter = OfferProductAdapter1(this, productItemMap!!)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = offerProductAdapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, CurrentOfferProductActivity::class.java)
        productName = ""
        productPrice = ""
        bProductName = ""
        bProductQty = ""
        startActivity(intent)
        finishAffinity()
    }

    private fun ReturnBackFunction() {
        back = findViewById<ImageView>(R.id.back)
        home = findViewById<View>(R.id.home) as ImageView

        back?.setOnClickListener {
            val idd = Intent(this, CurrentOfferProductActivity::class.java)
            productName = ""
            productPrice = ""
            bProductName = ""
            bProductQty = ""
            startActivity(idd)
            finish()
        }


        home?.setOnClickListener {
            productName = ""
            productPrice = ""
            bProductName = ""
            bProductQty = ""
            val idd = Intent(this, Dashboard::class.java)
            startActivity(idd)
            finish()
        }
    }
}