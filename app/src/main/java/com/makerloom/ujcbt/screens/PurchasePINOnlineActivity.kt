package com.makerloom.ujcbt.screens

import android.content.Intent
import android.os.Bundle
import com.makerloom.common.activity.MyBackToolbarActivity
import com.makerloom.ujcbt.R
import com.makerloom.ujcbt.utils.Commons
import com.makerloom.ujcbt.utils.NetworkUtils
import kotlinx.android.synthetic.main.activity_purchase_pin_online.*

class PurchasePINOnlineActivity : MyBackToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_pin_online)

        pay_with_airtime_btn.setEnabled(false)
        pay_with_airtime_btn.setOnClickListener {}

        // Don't try to load webview if there's no internet connection
        pay_with_card_bank_ussd_btn.setOnClickListener {
            if (Commons.hasConnection(this@PurchasePINOnlineActivity)) {
                startActivity(Intent(this@PurchasePINOnlineActivity,
                        PaymentPortalWebViewActivity::class.java))
            } else {
                NetworkUtils.showConnectionErrorMessage(this@PurchasePINOnlineActivity, "pay online")
            }

        }
    }
}
