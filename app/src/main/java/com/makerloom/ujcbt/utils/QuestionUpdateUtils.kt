package com.makerloom.ujcbt.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.makerloom.common.utils.Constants
import com.makerloom.ujcbt.events.QuestionsUpdateEvent
import com.makerloom.ujcbt.services.AlarmReceiver
import io.paperdb.Paper
import org.apache.commons.io.FileUtils
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class QuestionUpdateUtils {
    companion object {
        val TAG = QuestionUpdateUtils::class.java.simpleName

        fun setAlarmManager (context: Context) {
            Log.d(TAG, "setAlarmManager")

            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(context, 0, intent, 0)
            }

            alarmMgr?.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    AlarmManager.INTERVAL_HALF_HOUR,
                    alarmIntent
            )
        }

        fun checkForNewQuestions (context: Context) {
            Log.d(TAG, "checkForNewQuestions")

            val storageReference = FirebaseStorage.getInstance().reference
            val updateables = FileSystemUtils.getUpdateableFilePaths()
            val states = ArrayList<Boolean>()

            Log.d(TAG, "Updateables: ${updateables}")

            updateables.forEach {
                val updateablePath = it

                Log.d(TAG, "Updateable File Path: $updateablePath")

                val pathRef = storageReference.child(updateablePath)
                pathRef.metadata
                        .addOnSuccessListener {
                            // Last time new questions were uploaded to the server
                            val lastQuestionsUpdateDate = Date(it.updatedTimeMillis)
                            // Last time new questions were downloaded by the user
                            val lastUpdateDownloadDate = getLastUpdateDownloaded(updateablePath)

                            // Updateable file has been updated since last download
                            if (lastQuestionsUpdateDate.after(lastUpdateDownloadDate)) {
                                val parentDir = FileSystemUtils.getFullQuestionsDir(context)
                                // Create directory that holds updateable files if none exists
                                if (!parentDir.exists()) {
                                    if (!parentDir.mkdirs()) {
                                        Log.e(TAG,"Couldn't create parent directory")
                                        return@addOnSuccessListener
                                    }
                                }

                                Log.d(TAG, "Downloading new questions to $updateablePath")
                                if (Constants.TOAST_VERBOSE) {
                                    Toast.makeText(context,
                                            "Downloading new questions to $updateablePath",
                                            Toast.LENGTH_LONG).show()
                                }

                                // Download questions update
                                val filePath = File(context.filesDir, updateablePath)
                                val tempFilePath = File.createTempFile("update_",
                                        Commons.DATA_FILE_EXT, context.cacheDir)

                                pathRef.getFile(tempFilePath)
                                        .addOnSuccessListener {
//                                            FileUtils.moveFile(tempFilePath, filePath)
                                            FileUtils.copyFile(tempFilePath, filePath)
                                            FileUtils.deleteQuietly(tempFilePath)
                                            if (Constants.TOAST_VERBOSE) {
                                                Toast.makeText(context,
                                                        "Downloaded $filePath (${it.totalByteCount} Bytes)",
                                                        Toast.LENGTH_LONG).show()
                                            }
                                            Companion.setLastUpdateDownloaded(updateablePath)

                                            handleStates(states, updateables, updateablePath)
                                        }
                                        .addOnFailureListener {
                                            it.printStackTrace()
                                            handleStates(states, updateables, updateablePath)
                                        }
                                        .addOnProgressListener {
                                            Log.d(TAG, "Downloading $updateablePath " +
                                                    "(${it.bytesTransferred}/${it.totalByteCount})")
                                        }
                            }
                            else {
                                handleStates(states, updateables, updateablePath)
                            }
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                            handleStates(states, updateables, updateablePath)
                        }
            }
        }

        fun handleStates (states: ArrayList<Boolean>, updateables: List<String>, msg: String) {
            Log.d(TAG, "After $msg, States Size: ${states.size}/${updateables.size}")

            states.add(true)
            // All updateable files have been attended to
            if (states.size == updateables.size) {
                Log.d(TAG, "All updateable files have been attended to, " +
                        "Size: ${states.size}")
                EventBus.getDefault().post(QuestionsUpdateEvent())
            }
        }

        fun genUpdateableKey (uPath: String): String {
            return "${Commons.LAST_QUESTION_UPDATE_KEY}-$uPath"
                    .replace("/", "_")
        }

        fun getLastUpdateDownloaded (uPath: String): Date {
            return Paper.book(Commons.LAST_QUESTION_UPDATE_KEY).read(genUpdateableKey(uPath), Date(0L))
        }

        fun setLastUpdateDownloaded (uPath: String) {
            setLastUpdateDownloaded(uPath, Calendar.getInstance().time)
        }

        fun setLastUpdateDownloaded (uPath: String, date: Date) {
            Paper.book(Commons.LAST_QUESTION_UPDATE_KEY).write(genUpdateableKey(uPath), date)
        }
    }
}