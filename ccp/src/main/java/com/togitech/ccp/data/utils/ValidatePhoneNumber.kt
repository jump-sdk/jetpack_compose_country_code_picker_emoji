package com.togitech.ccp.data.utils

import android.content.Context
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

private const val MIN_PHONE_LENGTH = 6

internal class ValidatePhoneNumber(private val context: Context) {
    private val phoneUtil: PhoneNumberUtil by lazy { PhoneNumberUtil.createInstance(context) }

    operator fun invoke(fullPhoneNumber: String): Boolean =
        if (fullPhoneNumber.length > MIN_PHONE_LENGTH) {
            try {
                phoneUtil.isValidNumber(phoneUtil.parse(fullPhoneNumber, null))
            } catch (ex: NumberParseException) {
                false
            }
        } else {
            false
        }
}
