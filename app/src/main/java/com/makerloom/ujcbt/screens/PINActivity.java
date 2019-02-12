package com.makerloom.ujcbt.screens;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.makerloom.common.activity.MyPlainToolbarActivity;
import com.makerloom.common.utils.Constants;
import com.makerloom.ujcbt.R;
import com.makerloom.ujcbt.models.PINInfo;
import com.makerloom.ujcbt.utils.Commons;

import java.util.Calendar;
import java.util.HashMap;

import mehdi.sakout.fancybuttons.FancyButton;

public class PINActivity extends MyPlainToolbarActivity {
    TextView infoTV;

    EditText pinET;

    FancyButton verifyBtn;

    FancyButton signOutBtn;

    FancyButton pinEnquiry;

    FancyButton buyOnline;

    FirebaseUser user;

    FirebaseFirestore db;

    public static boolean hasShownPINDialog = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (null == user) {
            startActivity(new Intent(PINActivity.this, AuthActivity.class));
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        Commons.enableFirestoreOffline(db);

        infoTV = findViewById(R.id.info);

        pinET = findViewById(R.id.unlock_code);

        verifyBtn = findViewById(R.id.verify_button);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pinText = String.valueOf(pinET.getText()).trim();
                if (!pinText.isEmpty()) {
                    verifyUnlockPIN(pinText);
                }
            }
        });

        signOutBtn = findViewById(R.id.signout_btn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        buyOnline = findViewById(R.id.pay_online_btn);
        buyOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PINActivity.this, PurchasePINOnlineActivity.class));
            }
        });

        if (fromExpired()) {
            showExpiredPINMessage();
        }
        else {
            showUsePINMessage();
        }

        pinEnquiry = findViewById(R.id.info_btn);
        pinEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToInfoActivity();
            }
        });
    }

    private void dismiss (ProgressDialog dialog) {
        try {
            dialog.dismiss();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean fromExpired () {
        return getIntent().hasExtra(Commons.EXPIRED_PIN_KEY);
    }

    private static String TAG =  PINActivity.class.getSimpleName();

    private void verifyUnlockPIN(String PIN) {
        ProgressDialog dialog = new ProgressDialog(PINActivity.this);
        dialog.setMessage("Just a minute, we're verifying the PIN you just submitted");
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        DocumentReference ref = db.collection(Constants.PIN_COLLECTION_NAME)
                .document(PIN);

        ref.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dismiss(dialog);

                        if (Constants.TOAST_VERBOSE) {
                            Toast.makeText(PINActivity.this,
                                    e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        showConnectionErrorMessage();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        dismiss(dialog);

                        // Entered PIN is valid
                        if (documentSnapshot.exists()) {
                            Log.d(TAG, "The entered PIN exists on the database");

                            PINInfo pinInfo = documentSnapshot.toObject(PINInfo.class);

                            Gson gson = new Gson();
                            String pinString = gson.toJson(pinInfo);
                            Log.d(TAG, pinString);

                            // PIN has been used by user
                            if (null != pinInfo && pinInfo.hasBeenUsed() && null != pinInfo.getUid() && pinInfo.getUid().equals(user.getUid())) {
                                Log.d(TAG, "PIN has been used by user");

                                // User PIN hasn't expired
                                if (pinInfo.isNotExpired()) {
                                    Log.d(TAG, "PIN hasn't expired");
                                    goToApp();
                                }
                                // User PIN has expired
                                else {
                                    Log.d(TAG, "PIN has expired");
                                    showExpiredPINMessage();
                                }
                            }
                            // PIN has been used by someone else
                            else if (null != pinInfo && pinInfo.hasBeenUsed() && null != pinInfo.getUid() && !pinInfo.getUid().equals(user.getUid())) {
                                Log.d(TAG, "PIN has been used by someone else");
                                showPINUsedBySomeoneElseMessage();
                            }
                            // PIN has not been used yet
                            else if (null != pinInfo && !pinInfo.hasBeenUsed()) {
                                Log.d(TAG, "PIN hasn't been used yet");

                                // Associate user account with PIN and set PIN's validity
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.MONTH, Commons.VALIDITY_MONTHS);

                                final Boolean used = true;
                                final String uid = user.getUid();
                                final Long validTill = calendar.getTime().getTime();

                                // Update PIN info
                                HashMap<String, Object> pinMap = new HashMap<>();
                                pinMap.put(Commons.UID_KEY, uid);
                                pinMap.put(Commons.USED_KEY, used);
                                pinMap.put(Commons.VALID_TILL_KEY, validTill);

                                pinInfo.setUid(uid);
                                pinInfo.setUsed(used);
                                pinInfo.setValidTill(validTill);

                                // Save PIN info
                                ref.update(pinMap)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                showConnectionErrorMessage();
                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                goToApp();
                                            }
                                        });
                            }
                            else {
                                Log.d(TAG, "pinInfo is null");
                                startActivity(new Intent(PINActivity.this, WelcomeActivity.class));
                                finish();
                            }
                        }
                        // Entered PIN doesn't exist on database
                        else {
                            Log.d(TAG, "PIN doesn't exist on database");
                            showInvalidPINMessage();
                        }
                    }
                });
    }

    public void signOut () {
        ProgressDialog dialog = new ProgressDialog(PINActivity.this);
        dialog.setMessage("Signing Out");
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        AuthUI.getInstance()
                .signOut(PINActivity.this)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        try {
                            dialog.dismiss();
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        Intent out = new Intent(PINActivity.this, WelcomeActivity.class);
                        out.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(out);

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PINActivity.this,
                                "Unable to sign out. Please check your internet connection then try again.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void goToApp () {
        Intent app = new Intent(PINActivity.this, DepartmentsActivity.class);
        app.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(app);
        finish();
    }

    public void showPINUsedBySomeoneElseMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PINActivity.this)
                .setCancelable(true)
                .setTitle("Already Used PIN")
                .setMessage("The PIN you entered has already been used by someone else. Please get a new one then try again.")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showUsePINMessage() {
        if (hasShownPINDialog) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(PINActivity.this)
                .setCancelable(true)
                .setTitle("PIN Required")
                .setMessage("You need a PIN to access the full app. Please get one and enter it on this screen.")
                .setPositiveButton("Get PIN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToInfoActivity();
                    }
                })
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Buy PIN Online", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToBuyPINOnlineActivity();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        hasShownPINDialog = true;
    }

    void goToInfoActivity () {
        startActivity(new Intent(PINActivity.this, InfoActivity.class));
    }

    void goToBuyPINOnlineActivity () {
        startActivity(new Intent(PINActivity.this, PurchasePINOnlineActivity.class));
    }

    public void showExpiredPINMessage () {
        AlertDialog.Builder builder = new AlertDialog.Builder(PINActivity.this)
                .setCancelable(true)
                .setTitle("Expired PIN")
                .setMessage("Your PIN has expired. Please get a new one and try again.")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showInvalidPINMessage () {
        AlertDialog.Builder builder = new AlertDialog.Builder(PINActivity.this)
                .setCancelable(true)
                .setTitle("Invalid PIN")
                .setMessage("The PIN you entered is invalid. Please try again.")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showConnectionErrorMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PINActivity.this)
                .setCancelable(true)
                .setTitle("Internet Connection Error")
                .setMessage("We were unable to connect to your account. Please check your internet connection and try again.")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
