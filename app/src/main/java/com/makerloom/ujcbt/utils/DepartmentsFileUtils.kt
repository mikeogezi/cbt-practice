package com.makerloom.ujcbt.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.makerloom.ujcbt.models.Department
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader

class DepartmentsFileUtils {
    companion object {
        val TAG = DepartmentsFileUtils::class.java.simpleName

        fun getDeptsFile (context: Context): List<Department> {
            val gson = Gson()

            var inputStream: InputStream

            val deptsList = object: TypeToken<ArrayList<Department>>(){}.type

            if (FileSystemUtils.hasDownloadedDepartments(context)) {
                inputStream = FileInputStream(FileSystemUtils.getFullDepartmentsPath(context))
            }
            else {
//                Log.d(TAG, FileSystemUtils.getDepartmentsFilePath())
                inputStream = context.resources.openRawResource(
                        context.resources.getIdentifier("${Commons.DEPARTMENTS_FILENAME}",
                                "raw", context.packageName))
            }

            val reader = BufferedReader(InputStreamReader(inputStream))

            return gson.fromJson(reader, deptsList)
        }
    }
}