package com.makerloom.ujcbt.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.makerloom.common.activity.MyBackToolbarActivity;

import java.util.Arrays;
import java.util.List;

import com.makerloom.ujcbt.R;

/**
 * Created by michael on 5/7/18.
 */

public class AuthActivity extends MyBackToolbarActivity {
    private static final int RC_SIGN_IN = 123;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build(),
//            new AuthUI.IdpConfig.PhoneBuilder().build(),
            new AuthUI.IdpConfig.EmailBuilder().build()
    );

//    List<AuthUI.IdpConfig> providers = Arrays.asList(
//            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
//            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
//            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
//            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
//    );

    private static String TAG = AuthActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (null == user) {
            Log.d(TAG, "User object is null, go to sign in");
            signIn();
        }
        else {
            Log.d(TAG, "User object is not null, go to check PIN validity");
            // Check if user has payed for app
            checkPINValidity();
        }
    }

    private void goToUnlockActivity () {
        startActivity(new Intent(AuthActivity.this, PINActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                Log.d(TAG, "Signed user in, checking PIN validity");

                checkPINValidity();
            }
            else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Result cancelled");
                finish();
            }
            else {
                Log.d(TAG, "Error signing user in, " +
                        (null != response ? response.getError().getMessage() : ""));
//                showErrorSigningIn();
                signIn();
            }
        }
    }

    public void showErrorSigningIn () {
        AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this)
                .setCancelable(true)
                .setTitle("Authentication Error")
                .setMessage("An error occurred while signing you in. Please try again.")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void signIn () {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(true)
                .setAvailableProviders(providers)
//                .setTheme(R.style.AuthTheme)
                .setLogo(R.drawable.ic_logo)
//                .setTosAndPrivacyPolicyUrls(
//                        getString(R.string.tos_url),
//                        getString(R.string.privacy_policy_url))
                .build(), RC_SIGN_IN);
    }

    public void checkPINValidity () {
        Intent check = new Intent(AuthActivity.this, CheckPINValidityActivity.class);
        check.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(check);
        finish();
    }
}
