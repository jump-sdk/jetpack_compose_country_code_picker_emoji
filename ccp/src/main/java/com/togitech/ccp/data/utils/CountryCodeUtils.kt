package com.togitech.ccp.data.utils

import android.content.Context
import android.telephony.TelephonyManager
import com.togitech.ccp.data.CountryData
import com.togitech.ccp.data.Iso31661alpha2
import com.togitech.ccp.data.PhoneCode

private const val EMOJI_UNICODE = 0x1F1A5

internal fun getCountryFromPhoneCode(code: PhoneCode, context: Context): CountryData? {
    val countries = CountryData.entries.filter { it.countryPhoneCode == code }
    return when (countries.size) {
        0 -> null
        1 -> countries.firstOrNull()
        else -> countries.firstOrNull { it.countryIso == getUserIsoCode(context) }
            ?: if (code == "+1") CountryData.UnitedStates else countries.firstOrNull()
    }
}

@Suppress("SwallowedException", "TooGenericExceptionCaught", "Deprecation")
internal fun getUserIsoCode(context: Context): Iso31661alpha2 = try {
    val telephonyManager = context.telephonyManager
    telephonyManager?.networkCountryIso ?: telephonyManager?.simCountryIso
} catch (ex: Exception) {
    null
}.takeIf { !it.isNullOrBlank() } ?: context.resources.configuration.locale.country

fun countryCodeToEmojiFlag(countryCode: String): String =
    countryCode
        .uppercase()
        .map { char -> Character.codePointAt("$char", 0) + EMOJI_UNICODE }
        .joinToString("") { String(Character.toChars(it)) }

private val Context.telephonyManager: TelephonyManager?
    get() = getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
