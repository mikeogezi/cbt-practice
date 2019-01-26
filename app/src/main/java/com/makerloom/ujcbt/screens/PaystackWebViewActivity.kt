package com.makerloom.ujcbt.screens

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.makerloom.common.activity.MyBackToolbarActivity
import com.makerloom.common.activity.MyPlainToolbarActivity
import com.makerloom.ujcbt.R
import com.makerloom.ujcbt.utils.EmailUtils
import com.makerloom.ujcbt.utils.PINUtils
import java.lang.Exception

class PaystackWebViewActivity : AppCompatActivity() {
    companion object {
        val PAYSTACK_URL = "https://uj-cbt.firebaseapp.com/pay.html"
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
                    Toast.makeText(this@PaystackWebViewActivity,
                            "The email you entered is invalid. Please try again.",
                            Toast.LENGTH_LONG).show()
                    requestUserEmail(invalidEmail, this)
                }

                override fun onUserCancelled() {
                    Toast.makeText(this@PaystackWebViewActivity,
                            "You need to provide your email in order to pay online",
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
            loadUrl(PAYSTACK_URL)

            addJavascriptInterface(PaystackWebViewInterface(this@PaystackWebViewActivity),
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
                .setMessage("For you to pay for the app online, we require you to provide your " +
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
                    PINUtils.fetchUnusedPIN(object: PINUtils.PINFetchCallback {
                        override fun onPINFetched(PIN: String) {
                            val user = FirebaseAuth.getInstance().currentUser

                            PINUtils.registerPIN(user!!, PIN, object: PINUtils.PINRegisterCallback {
                                override fun onPINRegistered(PIN: String) {

                                    // Go here so that user's pin is verified and the app can
                                    // be used
                                    val intent = Intent(this@PaystackWebViewActivity,
                                            CheckPINValidityActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                                override fun onRegisterFailure(e: Exception) {
                                    // TODO
                                    Toast.makeText(this@PaystackWebViewActivity,
                                            "onRegisterFailure $e", Toast.LENGTH_LONG).show()
                                }
                            })
                        }

                        override fun onFetchFailure(e: Exception) {
                            // TODO
                            Toast.makeText(this@PaystackWebViewActivity,
                                    "onFetchFailure $e", Toast.LENGTH_LONG).show()
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
        showMessage("Great", "Your payment was successful.",
                MessageType.SUCCESS_MESSAGE)
    }

    fun showClosedMessage () {
        showMessage("Unfinished Business", "The payment page was closed.",
                MessageType.CLOSED_MESSAGE)
    }

    class PaystackWebViewInterface (private val activity: PaystackWebViewActivity) {
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
