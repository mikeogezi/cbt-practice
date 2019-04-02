package com.makerloom.ujcbt.utils

import android.content.Context
import java.io.File

class FileSystemUtils {
    companion object {
        val TAG = FileSystemUtils::class.java.simpleName

        fun getQuestionsFilePath(courseCode: String): String {
            val fmtCourseCode = courseCode.toLowerCase().replace(" ", "")
            return "${Commons.QUESTIONS_PATH}/${Commons.QUESTIONS_PREFIX}$fmtCourseCode.${Commons.DATA_FILE_EXT}"
        }

        fun getDepartmentsFilePath (): String {
            return "${Commons.QUESTIONS_PATH}/${Commons.DEPARTMENTS_FILENAME}.${Commons.DATA_FILE_EXT}"
        }

        fun getFullDepartmentsPath (context: Context): File {
            return File(context.filesDir, getDepartmentsFilePath())
        }

        fun getFullQuestionsPath (context: Context, courseCode: String): File {
            return File(context.filesDir, getQuestionsFilePath(courseCode))
        }

        fun getFullQuestionsDir (context: Context): File {
            return File(context.filesDir, Commons.QUESTIONS_PATH)
        }

        fun hasDownloadedDepartments (context: Context): Boolean {
            val deptFile = getFullDepartmentsPath(context)
            return deptFile.exists()
        }

        fun hasDownloadedQuestions (context: Context, courseCode: String): Boolean {
            val questionsFile = getFullQuestionsPath(context, courseCode)
            return questionsFile.exists()
        }

        fun getUpdateableFilePaths(): List<String> {
            val updateables = ArrayList<String>()

            updateables.add(getDepartmentsFilePath())



            updateables.add(getQuestionsFilePath("CS 101"))
            updateables.add(getQuestionsFilePath("CS 102"))
            updateables.add(getQuestionsFilePath("CS 201"))
            updateables.add(getQuestionsFilePath("GST 101"))
            updateables.add(getQuestionsFilePath("GST 102"))
            updateables.add(getQuestionsFilePath("GST 103"))
            updateables.add(getQuestionsFilePath("GST 104"))
            updateables.add(getQuestionsFilePath("GST 222"))
            updateables.add(getQuestionsFilePath("GST 223"))

            return updateables
        }
    }
}