package com.srapp.kotlin

import android.content.ContentValues.TAG
import android.content.Intent
import android.database.DatabaseUtils
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.gson.Gson
import com.srapp.Dashboard
import com.srapp.Db_Actions.Data_Source
import com.srapp.Db_Actions.Tables
import com.srapp.R
import com.srapp.Tools
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CurrentOfferProductActivity : AppCompatActivity() {

    private lateinit var dataViewModel: DataViewModel
    private var bonusCampaignList = ArrayList<BonusCampaign.BonusCamInfo>()
    private var recyclerView: RecyclerView? = null
    private lateinit var layoutManager: RecyclerView.LayoutManager
    var fromDate: TextView? = null
    var toDate: TextView? = null
    var dateFormat: SimpleDateFormat? = null
    var db: Data_Source? = null
    var mac: String? = null

    var from = ""
    var HeaderTitleTv: TextView? = null
    var txtUser: TextView? = null
    var btnBack: Button? = null
    var btnHome: Button? = null

    var spProductName: Spinner? = null
    var productItemMap = ArrayList<HashMap<String, String>>()
    var productId: String? = null
    var searchBtn: Button? = null


    var toolbar: Toolbar? = null
    var home: ImageView? = null
    var back: ImageView? = null
    var userIdTV: TextView? = null
    var titleTV:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.current_offer_product_list)

        db = Data_Source(this)

        dataViewModel = ViewModelProvider(
            this,
            DataViewModelFactory(this.application)
        )[DataViewModel::class.java]

        recyclerView = findViewById<View>(R.id.currentProduct_list_id) as RecyclerView

        val productList = java.util.ArrayList<String>()

        val cursorProduct = db!!.rawQueryCoustom("SELECT policy_name, start_date, end_date, policy_id FROM  Policy_Table where end_date>='"+getCurrentDate()+"' and start_date<='"+getCurrentDate()+"'")

        Log.i(TAG, "SELECT policy_name, start_date, end_date, policy_id FROM  Policy_Table where end_date>='"+getCurrentDate()+"' and start_date<='"+getCurrentDate()+"'")
        Log.e("Current_product:", cursorProduct.toString())

        if (cursorProduct != null) {
            if (cursorProduct.moveToFirst()) {
                do {
                    val pmap = HashMap<String, String>()
                    pmap["policy_name"] = cursorProduct.getString(0)
                    pmap["start_date"] = cursorProduct.getString(1)
                    pmap["end_date"] = cursorProduct.getString(2)
                    pmap["policy_id"] = cursorProduct.getString(3)
                    productItemMap.add(pmap)
                } while (cursorProduct.moveToNext())
            }
        }

        Log.i(TAG, "productItemMap: " + Gson().toJson(productItemMap))

        Log.d("dbdump", DatabaseUtils.dumpCursorToString(cursorProduct))

        initObservables()

    }

    fun getCurrentDate(): String {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = Date()
        return "" + dateFormat.format(date)
    }

    private fun initObservables() {

        userIdTV = findViewById(R.id.user_txt_view)
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val value = prefs.getString(Tables.SR_ID, "0")
        userIdTV?.text = value

        back = findViewById<ImageView>(R.id.back)
        back?.setOnClickListener {
            val intent = Intent(this, Tools::class.java)
            startActivity(intent)
            finishAffinity()
        }

        home = findViewById<View>(R.id.home) as ImageView
        home!!.setOnClickListener {
            BonusCampaignActivity.startDate =""
            BonusCampaignActivity.endDate =""
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finishAffinity()
        }

        layoutManager = LinearLayoutManager(applicationContext)

        setAdapter(productItemMap)

    }

    private fun setAdapter(policyData: ArrayList<HashMap<String, String>>) {
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = OfferProductAdapter(this, policyData)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Tools::class.java)
        startActivity(intent)
        finishAffinity()
    }

}