package com.makerloom.ujcbt.screens

import android.content.Intent
import android.os.Bundle
import com.makerloom.common.activity.MyBackToolbarActivity
import com.makerloom.ujcbt.R
import kotlinx.android.synthetic.main.activity_buy_pin_online.*

class PurchasePINOnlineActivity : MyBackToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_pin_online)

        pay_with_airtime_btn.setEnabled(false)
        pay_with_airtime_btn.setOnClickListener {}

        pay_with_card_bank_ussd_btn.setOnClickListener {
            startActivity(Intent(this@PurchasePINOnlineActivity,
                    PaymentPortalWebViewActivity::class.java))
        }
    }
}
