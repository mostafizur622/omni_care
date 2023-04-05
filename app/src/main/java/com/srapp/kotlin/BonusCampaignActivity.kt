package com.srapp.kotlin

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.srapp.Dashboard
import com.srapp.Db_Actions.Data_Source
import com.srapp.Db_Actions.Tables
import com.srapp.R
import com.srapp.Tools
import com.tanvir.BasicFun.BasicFunction
import com.tanvir.BasicFun.BasicFunctionListener
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BonusCampaignActivity : AppCompatActivity() , BasicFunctionListener{
    private var basicFunction: BasicFunction? = null

    var toolbar: Toolbar? = null
    private lateinit var dataViewModel: DataViewModel
    private var bonusCampaignList = ArrayList<BonusCampaign.BonusCamInfo>()
    private var recyclerView: RecyclerView? = null
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var calendar = Calendar.getInstance()
    var fromDate: TextView? = null
    var toDate: TextView? = null
    private var dateSelection: Int? = null
    var dateFormat: SimpleDateFormat? = null

    private var fromDatePickerDialog: DatePickerDialog? = null
    private var toDatePickerDialog: DatePickerDialog? = null

    var home: ImageView? = null
    var back: ImageView? = null
    var userIdTV: TextView? = null
    var titleTV:TextView? = null

    var mac_number: String?=null
    var sr_id: String?=null

    private var year1 = 0
    private var previous_month = 0
    private var day1 = 0
    private val year2 = 0
    private var current_month = 0
    private val day2 = 0

    private var currentMonth = 0
    private var PreviousMonth = 0

    var from = ""

    var DateFrom1 = ""
    var DateTo1 = ""

    var spProductName: Spinner? = null
    var productItemMap = ArrayList<HashMap<String, String>>()
    var productId: String?=null

    var searchBtn: Button?=null
    var db: Data_Source? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bonus_campaign)
        db=Data_Source(this)
        basicFunction = BasicFunction(this, this)
        mac_number=basicFunction!!.getPreference("mac")
        sr_id=basicFunction!!.getPreference("sales_person_id")

        Log.e("sr_id", sr_id!!)

        userIdTV = findViewById(R.id.user_txt_view)
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val value = prefs.getString(Tables.SR_ID, "0")
        userIdTV?.text = value

        dataViewModel = ViewModelProvider(this, DataViewModelFactory(this.application))[DataViewModel::class.java]

        recyclerView = findViewById<View>(R.id.bonusCam_list_id) as RecyclerView

        spProductName = findViewById<View>(R.id.spProduct) as Spinner
        searchBtn = findViewById<View>(R.id.SearchBtn) as Button

        searchBtn?.setOnClickListener {
            dataViewModel.getBonusCampaignData(fromDate?.text.toString(), toDate?.text.toString(), mac_number!!, productId!!, sr_id!!)
        }

        val productList = java.util.ArrayList<String>()
        val cursorProduct = db!!.rawQueryCoustom("SELECT product_name,product_id FROM  product where product_type_id=1")
        if (cursorProduct != null) {
            if (cursorProduct.moveToFirst()) {
                do {
                    val pmap = HashMap<String, String>()
                    pmap["product_id"] = cursorProduct.getString(1)
                    pmap["name"] = cursorProduct.getString(0)
                    productItemMap.add(pmap)
                    productList.add(cursorProduct.getString(0))
                } while (cursorProduct.moveToNext())
            }
        }

        val productAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.spinner_text, productList)
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spProductName?.adapter = productAdapter

        spProductName?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View,
                arg2: Int, arg3: Long
            ) {
                // TODO Auto-generated method stub
                productId=productItemMap[arg2]["product_id"]
                dataViewModel.getBonusCampaignData(fromDate?.text.toString(), toDate?.text.toString(), mac_number!!, productId!!, sr_id!!)
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {


            }
        }

        initObservables()
    }

    private fun initObservables() {

        dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        fromDate = findViewById(R.id.FromDateEd)
        toDate = findViewById(R.id.ToDateEd)

        //------------Selected One month -----------------------------------------------------------

        val c2 = Calendar.getInstance()
        year1 = c2[Calendar.YEAR]
        currentMonth = c2[Calendar.MONTH]
        current_month = c2[Calendar.MONTH] + 1
        day1 = c2[Calendar.DAY_OF_MONTH]
        if (current_month == 1) {
            previous_month = 12
            PreviousMonth = 12
        } else {
            previous_month = current_month - 1
            PreviousMonth = currentMonth - 1
        }


        Log.e("--------", "  previous_month $previous_month")
        Log.e("--------", "  current_month $current_month")
        Log.e("--------", "  c2.get(Calendar.MONTH) " + c2[Calendar.MONTH])

        var day = ""
        var month = ""
        var monthPrev = ""
        day = if (day1 < 10) "0" + day1.toString() else day1.toString()

        month = if (current_month < 10) "0" + current_month.toString() else current_month.toString()

        monthPrev =
            if (previous_month < 10) "0" + previous_month.toString() else previous_month.toString()

        Log.e("day", "Day$day")
        Log.e("month", month)

        if (monthPrev.equals("12", ignoreCase = true)) {
            DateFrom1 = StringBuilder().append(year1 - 1)
                .append("-").append(monthPrev).append("-").append(day)
                .append("").toString()
        } else {
            DateFrom1 = StringBuilder().append(year1)
                .append("-").append(monthPrev).append("-").append(day)
                .append("").toString()
        }

        DateTo1 = StringBuilder().append(year1)
            .append("-").append(month).append("-").append(day)
            .append("").toString()

        if (startDate == "") {
            fromDate?.text = DateFrom1
        } else {
            fromDate?.text = startDate
        }
        if (endDate == "") {
            toDate?.text = DateTo1
        } else {
            toDate?.text = endDate
        }

        setDateTimeField()


        fromDate?.setOnClickListener {
            fromDatePickerDialog?.show()
            /* dateSelection=1
             MyUtilsClass.showsDatePicker(this, dateSetListener)*/
        }

        toDate?.setOnClickListener {
            toDatePickerDialog?.show()
            /*dateSelection = 2
            MyUtilsClass.showsDatePicker(this, dateSetListener)*/
        }

        back = findViewById<ImageView>(R.id.back)
        back?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, Tools::class.java)
            startActivity(intent)
            finishAffinity()
        })

        home = findViewById<View>(R.id.home) as ImageView
        home!!.setOnClickListener {
            startDate=""
            endDate=""
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finishAffinity()
        }

        /* toolbar = findViewById(R.id.toolbar)
         setSupportActionBar(toolbar)
         supportActionBar!!.setDisplayHomeAsUpEnabled(true)
         supportActionBar!!.setDisplayShowHomeEnabled(true)
         title = "Bonus Campaign "

         toolbar?.setTitleTextColor(Color.parseColor("#ffffff"))

         toolbar?.setNavigationOnClickListener {
             val i = Intent(this, Dashboard::class.java)
             startActivity(i)
             finish()
         }*/

        layoutManager = LinearLayoutManager(applicationContext)

        dataViewModel.bonusCampaignList.observe(this, androidx.lifecycle.Observer { bonusCamList ->
            this.bonusCampaignList = bonusCamList as ArrayList<BonusCampaign.BonusCamInfo>
            setAdapter(bonusCamList)
        })
    }

    private fun setAdapter(bonusCamList: List<BonusCampaign.BonusCamInfo>) {
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = BonusCamAdapter(this, bonusCamList)
    }

    private val dateSetListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        when (dateSelection) {
            1 -> {
                startDate = dateFormat?.format(calendar.time)!!
                fromDate?.text = startDate
                dataViewModel.getBonusCampaignData(fromDate?.text.toString(), toDate?.text.toString(), mac_number!!, productId!!, sr_id!!)
            }
            2 -> {
                endDate = dateFormat?.format(calendar.time)!!
                toDate?.text = endDate
                dataViewModel.getBonusCampaignData(fromDate?.text.toString(), toDate?.text.toString(), mac_number!!, productId!!, sr_id!!)
            }
        }

    }

    companion object {
        var startDate: String = ""
        var endDate: String = ""
        var dayOfMonth: String = ""
    }

    private fun setDateTimeField() {

        val newCalendar = Calendar.getInstance()
        fromDatePickerDialog = DatePickerDialog(
            this, { view, year, monthOfYear, dayOfMonth ->
                val newDate = Calendar.getInstance()
                newDate[year, monthOfYear] = dayOfMonth

                startDate = dateFormat?.format(newDate.time)!!
                fromDate?.text = startDate
                dataViewModel.getBonusCampaignData(fromDate?.text.toString(), toDate?.text.toString(), mac_number!!, productId!!, sr_id!!)

            }, newCalendar[Calendar.YEAR], newCalendar[Calendar.MONTH],
            newCalendar[Calendar.DAY_OF_MONTH]
        )

        fromDatePickerDialog!!.datePicker.minDate=newCalendar.timeInMillis-5184000000
        fromDatePickerDialog!!.datePicker.maxDate=newCalendar.timeInMillis



        toDatePickerDialog = DatePickerDialog(
            this, { view, year, monthOfYear, dayOfMonth ->
                val newDate = Calendar.getInstance()
                newDate[year, monthOfYear] = dayOfMonth

                endDate = dateFormat?.format(newDate.time)!!
                toDate?.text = endDate
                dataViewModel.getBonusCampaignData(fromDate?.text.toString(), toDate?.text.toString(), mac_number!!, productId!!, sr_id!!)

            }, newCalendar[Calendar.YEAR], newCalendar[Calendar.MONTH],
            newCalendar[Calendar.DAY_OF_MONTH]
        )

        toDatePickerDialog!!.datePicker.minDate=newCalendar.timeInMillis-5184000000
        toDatePickerDialog!!.datePicker.maxDate=newCalendar.timeInMillis
    }

    override fun OnServerResponce(jsonObject: JSONObject?, RequestCode: Int) {
        TODO("Not yet implemented")
    }

    override fun OnConnetivityError() {
        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Tools::class.java)
        startActivity(intent)
        finishAffinity()
    }
}