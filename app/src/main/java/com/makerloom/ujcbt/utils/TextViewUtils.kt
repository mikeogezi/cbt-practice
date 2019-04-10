package com.makerloom.ujcbt.utils

import android.text.Html
import android.text.Spanned

class TextViewUtils {
    companion object {
        val TAG = TextViewUtils::class.java.simpleName;

        fun fromHtml (str: String): Spanned {
            return Html.fromHtml(str.replace("\n", "<br />").replace("\t", "    "))
        }
    }
}