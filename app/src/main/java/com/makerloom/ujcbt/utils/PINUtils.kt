package com.makerloom.ujcbt.utils

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.makerloom.common.utils.Constants
import com.makerloom.ujcbt.models.PINInfo
import com.makerloom.ujcbt.screens.PINActivity
import com.makerloom.ujcbt.screens.PaystackWebViewActivity
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class PINUtils {
    companion object {
        private fun buildUpdatedPINMap (user: FirebaseUser, pinInfo: PINInfo):
                HashMap<String, Any> {

            // Associate user account with PIN and set PIN's validity
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, Commons.VALIDITY_MONTHS)

            val used = true
            val uid = user.uid
            val validTill = calendar.time.time

            // Update PIN info
            val pinMap = HashMap<String, Any>()
            pinMap[Commons.UID_KEY] = uid
            pinMap[Commons.USED_KEY] = used
            pinMap[Commons.VALID_TILL_KEY] = validTill

            pinInfo.uid = uid
            pinInfo.setUsed(true)
            pinInfo.validTill = validTill

            return pinMap
        }

        fun fetchUnusedPIN (pinFetchCallback: PINFetchCallback) {
            val db = FirebaseFirestore.getInstance()

            val ref = db.collection(Constants.PIN_COLLECTION_NAME)
                    .whereEqualTo(Commons.USED_KEY, false)
                    .limit(1)

            ref.get()
                    .addOnSuccessListener {
                        val pinInfo = it.documents[0].toObject(PINInfo::class.java)
                        pinFetchCallback.onPINFetched(pinInfo!!.pin)
                    }
                    .addOnFailureListener {
                        pinFetchCallback.onFetchFailure(it)
                    }
        }

        fun registerPIN (user: FirebaseUser, PIN: String,
                         pinRegisterCallback: PINRegisterCallback) {

            // Associate user account with PIN and set PIN's validity
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, Commons.VALIDITY_MONTHS)

            val used = true
            val uid = user.uid
            val validTill = calendar.time.time

            // Set new pin info in update map
            val pinMap = HashMap<String, Any>()
            pinMap[Commons.UID_KEY] = uid
            pinMap[Commons.USED_KEY] = used
            pinMap[Commons.VALID_TILL_KEY] = validTill

            val db = FirebaseFirestore.getInstance()
            val ref = db.collection(Constants.PIN_COLLECTION_NAME)
                    .document(PIN)

            ref.update(pinMap)
                    .addOnFailureListener {
                        pinRegisterCallback.onRegisterFailure(it)
                    }
                    .addOnSuccessListener {
                        pinRegisterCallback.onPINRegistered(PIN)
                    }
        }

        fun registerPIN (user: FirebaseUser, pinInfo: PINInfo,
                         ref: DocumentReference, pinRegisterCallback: PINRegisterCallback) {

            val pinMap = buildUpdatedPINMap(user, pinInfo)

            // Save Updated PIN info
            ref.update(pinMap)
                    .addOnFailureListener {
                        pinRegisterCallback.onRegisterFailure(it)
                    }
                    .addOnSuccessListener {
                        pinRegisterCallback.onPINRegistered(pinInfo.pin)
                    }
        }
    }

    interface PINFetchCallback {
        fun onPINFetched (PIN: String)

        fun onFetchFailure (e: Exception)
    }

    interface PINRegisterCallback {
        fun onRegisterFailure (e: Exception)

        fun onPINRegistered (PIN: String)
    }
}