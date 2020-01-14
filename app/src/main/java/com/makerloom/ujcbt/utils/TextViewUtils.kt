package com.makerloom.ujcbt.utils

import android.text.Html
import android.text.SpannableString
import android.text.Spanned

class TextViewUtils {
    companion object {
        val TAG = TextViewUtils::class.java.simpleName

        val matchingTags = arrayOf("<u>", "</u>", "<b>", "</b>", "<br />", "<br/>")

        // Ignore tags that are not <u>, <b>, or <br>
        fun fromHtml (str: String): Spanned {
            for (tag in matchingTags) {
                if (str.contains(tag)) {
                    return Html.fromHtml(str.replace("\n", "<br />").replace("\t", "    "))
                }
            }
            return SpannableString(str)
        }
    }
}