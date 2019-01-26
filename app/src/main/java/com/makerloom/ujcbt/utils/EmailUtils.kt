package com.makerloom.ujcbt.utils

import android.text.TextUtils

class EmailUtils {
    companion object {
        fun isEmailValid (email: String?): Boolean {
            return !TextUtils.isEmpty(email) &&
                    android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }
}