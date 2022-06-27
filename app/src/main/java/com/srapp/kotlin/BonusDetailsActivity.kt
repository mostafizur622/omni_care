package com.srapp.kotlin

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.srapp.Dashboard
import com.srapp.Db_Actions.Tables
import com.srapp.R
import com.srapp.Tools
import com.srapp.kotlin.BonusCampaignActivity.Companion.endDate
import com.srapp.kotlin.BonusCampaignActivity.Companion.startDate

class BonusDetailsActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null

    var home: ImageView? = null
    var back: ImageView? = null
    var userIdTV: TextView? = null
    var titleTV:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bonus_campaign_details)
        initViews()
    }

    private fun initViews() {

        userIdTV = findViewById(R.id.user_txt_view)
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val value = prefs.getString(Tables.SR_ID, "0")
        userIdTV?.text = value

        back = findViewById<ImageView>(R.id.back)
        back?.setOnClickListener {
            val intent = Intent(this, BonusCampaignActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        home = findViewById<View>(R.id.home) as ImageView
        home!!.setOnClickListener {
            startDate=""
            endDate=""
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finishAffinity()
        }

        val detailsMessage: TextView = findViewById(R.id.bonusDetails)
        val motherProduct: TextView = findViewById(R.id.motherProduct)
        val imageView: ImageView = findViewById(R.id.attachment)

        val intent = intent
        val tMessage = intent.getStringExtra("bonusDetails")
        val attachment = intent.getStringExtra("attachment")
        val mProduct = intent.getStringExtra("motherProduct")
        detailsMessage.text = tMessage
        motherProduct.text = mProduct

        if (!attachment.isNullOrEmpty()) {
            Glide.with(applicationContext)
                .load(attachment)
                .asBitmap()
                .placeholder(R.drawable.logo)
                .into(imageView)
        }

        /*toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        title = "Bonus Details "
        toolbar?.setTitleTextColor(Color.parseColor("#ffffff"))

        toolbar?.setNavigationOnClickListener {
            val i = Intent(this, BonusCampaignActivity::class.java)
            startActivity(i)
            finish()
        }*/
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, BonusCampaignActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

}