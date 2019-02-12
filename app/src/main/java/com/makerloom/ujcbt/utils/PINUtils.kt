package com.makerloom.ujcbt.utils

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.makerloom.common.utils.Constants
import com.makerloom.ujcbt.models.PINInfo
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
            pinMap[Commons.PIN_KEY] = pinInfo.pin
            pinMap[Commons.UID_KEY] = uid
            pinMap[Commons.USED_KEY] = used
            pinMap[Commons.VALID_TILL_KEY] = validTill

            pinInfo.uid = uid
            pinInfo.setUsed(true)
            pinInfo.validTill = validTill

            return pinMap
        }

        private fun generatePIN (): String {
            return "1111-online-${UUID.randomUUID()}".toUpperCase()
        }

        fun generateNewPIN (user: FirebaseUser, pinGenerateCallback: PINGenerateCallback) {
            val db = FirebaseFirestore.getInstance()

            val PIN = generatePIN()
            val pinInfo = PINInfo(PIN)
            val pinMap = buildUpdatedPINMap(user, pinInfo)

            // Add PIN to the Firestore database
            val ref = db.collection(Constants.PIN_COLLECTION_NAME).document(PIN)
            ref.set(pinMap)
                    .addOnSuccessListener {
                        pinGenerateCallback.onPINGenerated(PIN)
                    }
                    .addOnFailureListener {
                        pinGenerateCallback.onFetchFailure(it)
                    }
        }

        // Associate the supplied PIN with a user
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

    interface PINGenerateCallback {
        fun onPINGenerated (PIN: String)

        fun onFetchFailure (e: Exception)
    }

    interface PINRegisterCallback {
        fun onRegisterFailure (e: Exception)

        fun onPINRegistered (PIN: String)
    }
}