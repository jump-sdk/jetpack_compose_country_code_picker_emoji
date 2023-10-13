package com.togitech.ccp.component

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import com.togitech.ccp.autofill.PhoneNumberRetrievalResultSender
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
internal fun Modifier.autofill(
    autofillTypes: List<AutofillType>,
    onFill: (String) -> Unit,
    focusRequester: FocusRequester,
    phoneNumberIntentSender: PhoneNumberRetrievalResultSender? = null,
): Modifier = this then composed {
    val autofill = LocalAutofill.current
    val autofillNode = AutofillNode(onFill = onFill, autofillTypes = autofillTypes)
    val coroutineScope = rememberCoroutineScope()
    LocalAutofillTree.current += autofillNode

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    this
        .onGloballyPositioned {
            autofillNode.boundingBox = it.boundsInWindow()
        }
        .onFocusChanged { focusState ->
            autofill?.run {
                if (focusState.isFocused) {
                    coroutineScope.launch {
                        try {
                            phoneNumberIntentSender?.triggerPhoneNumberRetrieval {
                                onFill(it)
                            }
                        } catch (exception: IllegalStateException) {
                            Log.e("AutoFill", "Unable to autofill phone number", exception)
                        }
                    }
                    requestAutofillForNode(autofillNode)
                } else {
                    cancelAutofillForNode(autofillNode)
                }
            }
        }
}
