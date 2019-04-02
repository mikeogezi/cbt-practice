package com.makerloom.ujcbt.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.makerloom.ujcbt.models.Questions
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class QuestionsFilesUtils {
    companion object {
        val TAG = QuestionUpdateUtils::class.java.simpleName

        fun getQuestionsFile (context: Context, courseCode: String): Questions {
            val gson = Gson()

            var inputStream: InputStream

            if (FileSystemUtils.hasDownloadedQuestions(context, courseCode)) {
                inputStream = FileInputStream(FileSystemUtils.getFullQuestionsPath(context, courseCode))
            }
            else {
                val filename = Commons.QUESTIONS_PREFIX + courseCode.replace(" ", "")
                        .toLowerCase(Locale.ENGLISH)
                Log.d(TAG, filename)
                inputStream = context.resources.openRawResource(
                        context.resources.getIdentifier(filename, "raw", context.packageName))
            }

            val reader = BufferedReader(InputStreamReader(inputStream))

            return gson.fromJson(reader, Questions::class.java)
        }
    }
}