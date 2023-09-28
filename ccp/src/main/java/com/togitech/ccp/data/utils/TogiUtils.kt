package com.togitech.ccp.data.utils

import android.content.Context
import android.telephony.TelephonyManager
import androidx.compose.ui.text.intl.Locale
import com.togitech.ccp.data.CountryData

private const val EMOJI_UNICODE = 0x1F1A5

@Suppress("SwallowedException")
private fun getDefaultLangCode(context: Context): String {
    val countryCode = try {
        context.telephonyManager?.networkCountryIso
    } catch (ex: java.lang.AssertionError) {
        null
    }
    return countryCode.takeIf { !it.isNullOrBlank() } ?: Locale.current.language
}
internal fun getDefaultCountryAndPhoneCode(
    context: Context,
    fallbackCountryData: CountryData,
    initialCountryData: CountryData?,
): Pair<String, String> {
    // return initial country data if passed
    if (initialCountryData != null) {
        val initialCountryCode = initialCountryData.countryCode
        val initialCountryPhoneCode = initialCountryData.countryPhoneCode
        return initialCountryCode to initialCountryPhoneCode
    }
    val defaultCountry = getDefaultLangCode(context)
    val defaultCode: CountryData? = getLibCountries.firstOrNull { it.countryCode == defaultCountry }
    return defaultCountry to (
        defaultCode?.countryPhoneCode.takeIf {
            !it.isNullOrBlank()
        } ?: fallbackCountryData.countryPhoneCode
        )
}

fun countryCodeToEmojiFlag(countryCode: String): String =
    countryCode
        .uppercase()
        .map { char ->
            Character.codePointAt("$char", 0) + EMOJI_UNICODE
        }
        .joinToString("") {
            String(Character.toChars(it))
        }

private val Context.telephonyManager: TelephonyManager?
    get() = getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
