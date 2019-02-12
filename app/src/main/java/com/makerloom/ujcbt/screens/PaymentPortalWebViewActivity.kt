package com.makerloom.ujcbt.screens

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.makerloom.common.utils.Constants
import com.makerloom.ujcbt.R
import com.makerloom.ujcbt.utils.EmailUtils
import com.makerloom.ujcbt.utils.PINUtils
import java.lang.Exception

class PaymentPortalWebViewActivity : AppCompatActivity() {
    companion object {
        val PAYMENT_PORTAL_URL = "https://uj-cbt.firebaseapp.com/pay.html"
        val TAG = PaymentPortalWebViewActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paystack_webview)

        // Open the payment page if we already possess the user's email
        if (isEmailAvailable()) {
            initWebView()
        }
        // Get the users email then open the payment page
        else {
            requestUserEmail(object: UserEmailRequest {
                override fun onEmailGotten(email: String) {
                    userEmail = email
                    initWebView()
                }

                override fun onInvalidEmailProvided(invalidEmail: String) {
                    Toast.makeText(this@PaymentPortalWebViewActivity,
                            "The email address you entered is invalid. Please try again.",
                            Toast.LENGTH_LONG).show()
                    requestUserEmail(invalidEmail, this)
                }

                override fun onUserCancelled() {
                    Toast.makeText(this@PaymentPortalWebViewActivity,
                            "You need to provide your email address in order to pay online",
                            Toast.LENGTH_LONG).show()
                    finish()
                }
            })
        }
    }

    private var webView: WebView? = null

    fun initWebView () {
        webView = findViewById<WebView>(R.id.paystack_webview)

        webView!!.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            loadUrl(PAYMENT_PORTAL_URL)

            addJavascriptInterface(PaymentPortalWebViewInterface(this@PaymentPortalWebViewActivity),
                    "AndroidInterface")
        }
    }

    fun reloadWebView () {
        webView!!.reload()
    }

    private var userEmail: String? = null

    fun getUserEmail (): String {
        return userEmail!!
    }

    fun isEmailAvailable (): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        val available = user != null && EmailUtils.isEmailValid(user.email)
        if (available) {
            userEmail = user!!.email
        }

        return available
    }

    fun requestUserEmail (emailRequest: UserEmailRequest) {
        requestUserEmail("", emailRequest)
    }

    // This method asks the user for their email if it wasn't gotten when they signed up
    // After the email is provided the user is taken to the payment page
    fun requestUserEmail (currEmail: String, emailRequest: UserEmailRequest) {
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)

        val editText = EditText(this)
        editText.apply {
            setSingleLine(true)
            setText(currEmail)
            layoutParams = params
        }

        val layout = LinearLayout(this)
        layout.apply {
            addView(editText)
            layoutParams = params
            setPadding(paddingLeft + 36, paddingTop,
                    paddingRight + 36, paddingBottom)
        }

        val builder = AlertDialog.Builder(this)
                .setView(layout)
                .setTitle("Your email please...")
                .setMessage("For you to pay for the app online, we require your " +
                        "email address. Please enter it below.")
                .setPositiveButton("Submit", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val email = "${editText.text}"
                        if (EmailUtils.isEmailValid(email)) {
                            emailRequest.onEmailGotten(email)
                        }
                        else {
                            emailRequest.onInvalidEmailProvided(email)
                        }
                    }
                })
                .setNegativeButton("Cancel", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        emailRequest.onUserCancelled()
                    }
                })


        builder.show()
    }

    fun showMessage (title: String, message: String, type: MessageType) {
        val builder = AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(message)
                .setTitle(title)

        if (type == MessageType.SUCCESS_MESSAGE) {
            builder.setPositiveButton("OK", object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    // Get available pin
                    val user = FirebaseAuth.getInstance().currentUser

                    PINUtils.generateNewPIN(user!!, object: PINUtils.PINGenerateCallback {
                        override fun onPINGenerated(PIN: String) {
                            PINUtils.registerPIN(user!!, PIN, object: PINUtils.PINRegisterCallback {
                                override fun onPINRegistered(PIN: String) {

                                    // Go here so that user's pin is verified and the app can
                                    // be used
                                    val intent = Intent(this@PaymentPortalWebViewActivity,
                                            CheckPINValidityActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                                override fun onRegisterFailure(e: Exception) {
                                    // TODO
                                    Log.e(TAG, "onRegisterFailure $e")
                                    if (Constants.VERBOSE) {
                                        Toast.makeText(this@PaymentPortalWebViewActivity,
                                                "onRegisterFailure $e", Toast.LENGTH_LONG).show()
                                    }
                                }
                            })
                        }

                        override fun onFetchFailure(e: Exception) {
                            // TODO
                            Log.e(TAG, "onFetchFailure $e")
                            if (Constants.VERBOSE) {
                                Toast.makeText(this@PaymentPortalWebViewActivity,
                                        "onFetchFailure $e", Toast.LENGTH_LONG).show()
                            }
                        }
                    })
                }
            })
        }
        else if (type == MessageType.CLOSED_MESSAGE) {
            builder.setPositiveButton("Try Again", object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    reloadWebView()
                }
            })
            builder.setPositiveButton("Quit", object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    finish()
                }
            })
        }

        builder.show()
    }

    fun showSuccessMessage () {
        showMessage("Great!", "Your payment was successful. You now have full access to the app.",
                MessageType.SUCCESS_MESSAGE)
    }

    fun showClosedMessage () {
        showMessage("Unfinished Business", "The payment page was closed. Reopen it to complete your payment.",
                MessageType.CLOSED_MESSAGE)
    }

    class PaymentPortalWebViewInterface (private val activity: PaymentPortalWebViewActivity) {
        @JavascriptInterface
        fun onSuccess () {
            activity.showSuccessMessage()
        }

        @JavascriptInterface
        fun onClosed () {
            activity.showClosedMessage()
        }

        @JavascriptInterface
        fun getUserEmail (): String {
            return activity.getUserEmail()
        }
    }

    interface UserEmailRequest {
        fun onEmailGotten (email: String)

        fun onInvalidEmailProvided (invalidEmail: String)

        fun onUserCancelled ()
    }

    enum class MessageType {
        CLOSED_MESSAGE,
        SUCCESS_MESSAGE
    }
}
