package com.makerloom.ujcbt.screens

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.makerloom.common.utils.Constants
import com.makerloom.ujcbt.R
import com.makerloom.ujcbt.utils.EmailUtils
import com.makerloom.ujcbt.utils.PINUtils
import java.net.URLDecoder


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
            webViewClient = MyWebViewClient()
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            loadUrl(PAYMENT_PORTAL_URL)

            addJavascriptInterface(PaymentPortalWebViewInterface(this@PaymentPortalWebViewActivity),
                    "AndroidInterface")
        }
    }

    private class MyWebViewClient: WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            val query_string = Uri.parse(url)
            val query_scheme = query_string.getScheme()
            val query_host = query_string.getHost()

            if ((query_scheme.equals("https", ignoreCase = true) || query_scheme.equals("http", ignoreCase = true))
                    && query_host != null // && query_host.equalsIgnoreCase(Uri.parse(URL_SERVER).getHost())
                    && query_string.getQueryParameter("new_window") == null) {

                return false // handle the load by webview
            }

            try {
                var intent = Intent(Intent.ACTION_VIEW, query_string)
                val body = url.split("\\?body=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                if (query_scheme.equals("sms", ignoreCase = true) && body.size > 1) {
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse(body[0]))
                    intent.putExtra("sms_body", URLDecoder.decode(body[1]))
                }

                view.context.startActivity(intent) // handle the load by os
            }
            catch (e: Exception) {
                e.printStackTrace()
            }

            return true
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
                .setTitle("Your Email Please...")
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

    fun runPINGeneration (user: FirebaseUser?) {
        PINUtils.generateNewPIN(user!!, object: PINUtils.PINGenerateCallback {
            override fun onPINGenerated(PIN: String) {
                runPINRegistration(user, PIN)
            }

            override fun onFetchFailure(e: Exception) {
                Log.e(TAG, "onFetchFailure $e")
                if (Constants.VERBOSE) {
                    Toast.makeText(this@PaymentPortalWebViewActivity,
                            "onFetchFailure $e", Toast.LENGTH_LONG).show()
                }
                // Retry
                showRetryToast()
                runPINGeneration(user)
            }
        })
    }

    fun runPINRegistration (user: FirebaseUser?, PIN: String) {
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
                Log.e(TAG, "onRegisterFailure $e")
                if (Constants.VERBOSE) {
                    Toast.makeText(this@PaymentPortalWebViewActivity,
                            "onRegisterFailure $e", Toast.LENGTH_LONG).show()
                }
                // Retry
                showRetryToast()
                runPINRegistration(user, PIN)
            }
        })
    }

    fun showRetryToast () {
        Toast.makeText(this@PaymentPortalWebViewActivity,
                "Please reconnect to the Internet so your PIN can be registered", Toast.LENGTH_LONG).show()
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
                    runPINGeneration(user)
//                  runPINGeneration replaced the below
//                  -----------------------------------
//                    PINUtils.generateNewPIN(user!!, object: PINUtils.PINGenerateCallback {
//                        override fun onPINGenerated(PIN: String) {
//                            PINUtils.registerPIN(user!!, PIN, object: PINUtils.PINRegisterCallback {
//                                override fun onPINRegistered(PIN: String) {
//
//                                    // Go here so that user's pin is verified and the app can
//                                    // be used
//                                    val intent = Intent(this@PaymentPortalWebViewActivity,
//                                            CheckPINValidityActivity::class.java)
//                                    startActivity(intent)
//                                    finish()
//                                }
//
//                                override fun onRegisterFailure(e: Exception) {
//                                    // TODO
//                                    Log.e(TAG, "onRegisterFailure $e")
//                                    if (Constants.VERBOSE) {
//                                        Toast.makeText(this@PaymentPortalWebViewActivity,
//                                                "onRegisterFailure $e", Toast.LENGTH_LONG).show()
//                                    }
//                                }
//                            })
//                        }
//
//                        override fun onFetchFailure(e: Exception) {
//                            // TODO
//                            Log.e(TAG, "onFetchFailure $e")
//                            if (Constants.VERBOSE) {
//                                Toast.makeText(this@PaymentPortalWebViewActivity,
//                                        "onFetchFailure $e", Toast.LENGTH_LONG).show()
//                            }
//                        }
//                    })
                }
            })
        }
        else if (type == MessageType.CLOSED_MESSAGE) {
            builder.setPositiveButton("Try Again", object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dismiss(dialog)
                    runOnUiThread {
                        reloadWebView()
                    }
                }
            })
            builder.setNegativeButton("Quit", object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    finish()
                }
            })
        }

        builder.show()
    }

    override fun onBackPressed() {
        val goBack = Runnable {
            try {
                super.onBackPressed()
            }
            catch (e: Exception) {}
        }

        val builder = AlertDialog.Builder(this@PaymentPortalWebViewActivity)
                .setTitle("Sure?")
                .setMessage("Going back will cancel your payment progress. Are you sure you want to do this?")
                .setCancelable(false)
                .setPositiveButton("Yes, Go Back", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        goBack.run()
                    }
                })
                .setNegativeButton("No, Stay", object: DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dismiss(dialog)
                    }
                })

        builder.show()
    }

    fun dismiss (dialog: DialogInterface?) {
        try {
            dialog?.dismiss()
        }
        catch (e: Exception) {}
    }

    fun showSuccessMessage () {
        showMessage("Great Job!", "Your payment was successful. You now have full access to the app.",
                MessageType.SUCCESS_MESSAGE)
    }

    fun showClosedMessage () {
        showMessage("Unfinished Business...", "The payment page was closed. Reopen it to complete your payment.",
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
