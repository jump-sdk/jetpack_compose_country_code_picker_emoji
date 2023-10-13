package com.togitech.ccp.data.utils

import android.content.Context
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil

private const val MIN_PHONE_LENGTH = 6

internal class PhoneNumberUtils(private val context: Context) {
    private val phoneUtil: PhoneNumberUtil by lazy { PhoneNumberUtil.createInstance(context) }

    fun isValidPhoneNumber(fullPhoneNumber: String): Boolean =
        if (fullPhoneNumber.length > MIN_PHONE_LENGTH) {
            try {
                phoneUtil.isValidNumber(phoneUtil.parse(fullPhoneNumber, null))
            } catch (ex: NumberParseException) {
                false
            }
        } else {
            false
        }

    fun getCountryCode(fullPhoneNumber: String): String {
        return if (fullPhoneNumber.length > MIN_PHONE_LENGTH) {
            try {
                val parsedNumber = phoneUtil.parse(fullPhoneNumber, null)
                phoneUtil.getRegionCodeForCountryCode(parsedNumber.countryCode)
            } catch (ex: NumberParseException) {
                ""
            }
        } else {
            ""
        }
    }

    fun getNationalNumber(fullPhoneNumber: String): String {
        val parsedNumber = phoneUtil.parse(fullPhoneNumber, null)
        return phoneUtil.getNationalSignificantNumber(parsedNumber)
    }
}
