package com.makerloom.ujcbt.utils

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class NetworkUtils {
    companion object {
        fun showConnectionErrorMessage (activity: AppCompatActivity, shortMessage: String) {
            val builder = AlertDialog.Builder(activity)
                    .setCancelable(true)
                    .setTitle("Internet Connection Error")
                    .setMessage("You need a good internet connection to $shortMessage. Please check that you're online then try again.")
                    .setNeutralButton("OK") { dialog, which ->
                        try {
                            dialog.dismiss()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

            val dialog = builder.create()
            dialog.show()
        }
    }
}