package com.makerloom.ujcbt.screens;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.makerloom.common.activity.MyAppCompatActivity;
import com.makerloom.common.utils.Constants;
import com.makerloom.ujcbt.models.PINInfo;
import com.makerloom.ujcbt.models.UserInfo;
import com.makerloom.ujcbt.utils.Commons;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CheckPINValidityActivity extends MyAppCompatActivity {
    FirebaseUser user;

    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Not signed in
        if (null == user) {
            startActivity(new Intent(CheckPINValidityActivity.this, AuthActivity.class));
        }
        // UserInfo signed in, now check if he has a valid PIN
        else {
            checkPIN();
        }
    }

    private static String TAG =  CheckPINValidityActivity.class.getSimpleName();

    public void checkPIN () {
        ProgressDialog dialog = new ProgressDialog(CheckPINValidityActivity.this);
        dialog.setMessage("Just a minute, we're verifying the status of your account");
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        db = FirebaseFirestore.getInstance();
        Commons.enableFirestoreOffline(db);

        Query pinQuery = db.collection(Constants.PIN_COLLECTION_NAME)
                .whereEqualTo(Commons.UID_KEY, user.getUid());

        pinQuery.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try {
                            dialog.dismiss();
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        showFailedValidityCheckMessage();

                        if (Constants.TOAST_VERBOSE) {
                            Toast.makeText(CheckPINValidityActivity.this,
                                    e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        try {
                            dialog.dismiss();
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        // Sort results of PINs to sort by most recent PIN activation
                        Collections.sort(snapshotList, new Comparator<DocumentSnapshot>() {
                            @Override
                            public int compare(DocumentSnapshot lhs, DocumentSnapshot rhs) {
                                PINInfo lhsPin = lhs.toObject(PINInfo.class);
                                PINInfo rhsPin = rhs.toObject(PINInfo.class);
                                return rhsPin.getValidTill().compareTo(lhsPin.getValidTill());
                            }
                        });

                        if (snapshotList.isEmpty()) {
                            Log.d(TAG, "No valid PIN registered for user");
                            // No valid PIN registered for user
                            goToUnlockActivity();
                        }
                        else {
                            if (snapshotList.size() > 1) {
                                Log.d(TAG, "Multiple PINs registered by user, Will use most recent");
                            }
                            else {
                                Log.d(TAG, "One PIN registered by user");
                            }

                            DocumentSnapshot snapshot = snapshotList.get(0);

                            PINInfo pinInfo = snapshot.toObject(PINInfo.class);

                            if (null == pinInfo) {
                                startActivity(new Intent(CheckPINValidityActivity.this,
                                        WelcomeActivity.class));
                                finish();
                            }
                            else if (pinInfo.isExpired()) {
                                Log.d(TAG, "PIN is expired");

                                Intent unlock = new Intent(CheckPINValidityActivity.this,
                                        PINActivity.class);
                                unlock.putExtra(Commons.EXPIRED_PIN_KEY, true);
                                startActivity(unlock);
                                finish();
                            }
                            else {
                                Log.d(TAG, "Valid PIN registered by user");
                                // Valid PIN(s) registered for user
                                goToApp();
                            }

                            Log.d(TAG, "Valid Till: " + new Date(pinInfo.getValidTill()));
                            Log.d(TAG, "Today's Date: " + Calendar.getInstance().getTime());
                        }
                    }
                });
    }

    private void goToApp () {
        Intent go = new Intent(CheckPINValidityActivity.this, DepartmentsActivity.class);
        startActivity(go);
        finish();
    }

    private void goToUnlockActivity () {
        Intent unlock = new Intent(CheckPINValidityActivity.this, PINActivity.class);
        startActivity(unlock);
        finish();
    }

    public void showFailedValidityCheckMessage () {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckPINValidityActivity.this)
                .setCancelable(false)
                .setTitle("Internet Connection Error")
                .setMessage("We were unable to connect to your account. Please check your internet connection and try again.")
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkPIN();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
