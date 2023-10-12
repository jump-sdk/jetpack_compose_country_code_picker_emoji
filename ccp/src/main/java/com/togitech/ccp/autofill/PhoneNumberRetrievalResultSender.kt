package com.togitech.ccp.autofill

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.withResumed
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.coroutineScope

class PhoneNumberRetrievalResultSender(private val rootActivity: ComponentActivity) {

    private var callback: ((String) -> Unit)? = null

    private val phoneNumberHintIntentResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        rootActivity.registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) {
            onActivityComplete(it)
        }


    suspend fun triggerPhoneNumberRetrieval(phoneNumberCallback: (String) -> Unit) = coroutineScope {
        // A previous contract may still be pending resolution (via the onActivityComplete method).
        // Wait for the Activity lifecycle to reach the RESUMED state, which guarantees that any
        // previous Activity results will have been received and their callback cleared. Blocking
        // here will lead to either (a) the Activity eventually reaching the RESUMED state, or
        // (b) the Activity terminating, destroying it's lifecycle-linked scope and cancelling this
        // Job.
        rootActivity.lifecycle.withResumed { // NOTE: runs in Dispatchers.MAIN context
            check(callback == null) { "Received an activity start request while another is pending" }
            callback = phoneNumberCallback

            val request = GetPhoneNumberHintIntentRequest.builder().build()

            Identity.getSignInClient(rootActivity)
                .getPhoneNumberHintIntent(request)
                .addOnSuccessListener {
                    phoneNumberHintIntentResultLauncher.launch(
                        IntentSenderRequest.Builder(it.intentSender).build()
                    )
                }
                .addOnFailureListener {
                    Log.d(LOG_TAG, it.message.toString())
                }
        }
    }

    private fun onActivityComplete(activityResult: ActivityResult) {
        try {
            val phoneNumber = Identity.getSignInClient(rootActivity)
                .getPhoneNumberFromIntent(activityResult.data)
            callback?.let { it(phoneNumber) }
        } catch (e: Exception) {
            Log.d(LOG_TAG, e.message.toString())
        }
        callback = null
    }

    companion object {
        private const val LOG_TAG = "PHONE_NUMBER_RETRIEVAL_RESULT_SENDER"
    }
}
