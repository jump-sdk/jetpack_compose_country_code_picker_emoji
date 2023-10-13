package com.togitech.ccp.component

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.togitech.ccp.R
import com.togitech.ccp.autofill.PhoneNumberRetrievalResultSender
import com.togitech.ccp.data.CountryData
import com.togitech.ccp.data.Iso31661alpha2
import com.togitech.ccp.data.PhoneCode
import com.togitech.ccp.data.utils.PhoneNumberUtils
import com.togitech.ccp.data.utils.getCountryFromPhoneCode
import com.togitech.ccp.data.utils.getUserIsoCode
import com.togitech.ccp.data.utils.numberHint
import com.togitech.ccp.transformation.PhoneNumberTransformation
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.launch

private val DEFAULT_TEXT_FIELD_SHAPE = RoundedCornerShape(24.dp)
private const val TAG = "TogiCountryCodePicker"

/**
 * @param onValueChange Called when the text in the text field changes.
 * The first parameter is string pair of (country phone code, phone number) and the second parameter is
 * a boolean indicating whether the phone number is valid.
 * @param modifier Modifier to be applied to the inner OutlinedTextField.
 * @param enabled Boolean indicating whether the field is enabled.
 * @param shape Shape of the text field.
 * @param showCountryCode Whether to show the country code in the text field.
 * @param showCountryFlag Whether to show the country flag in the text field.
 * @param colors TextFieldColors to be used for the text field.
 * @param fallbackCountry The country to be used as a fallback if the user's country cannot be determined.
 * Defaults to the United States.
 * @param showPlaceholder Whether to show the placeholder number hint in the text field.
 * @param includeOnly A set of 2 digit country codes to be included in the list of countries.
 * Set to null to include all supported countries.
 * @param clearIcon ImageVector to be used for the clear button. Set to null to disable the clear button.
 * Defaults to Icons.Filled.Clear
 * @param initialPhoneNumber an optional phone number to be initial value of the input field
 * @param initialCountryIsoCode Optional ISO-3166-1 alpha-2 country code to set the initially selected country.
 * Note that if a valid initialCountryPhoneCode is provided, this will be ignored.
 * @param initialCountryPhoneCode Optional country phone code to set the initially selected country.
 * This takes precedence over [initialCountryIsoCode].
 * @param label An optional composable to be used as a label for input field
 * @param textStyle An optional [TextStyle] for customizing text style of phone number input field.
 * Defaults to MaterialTheme.typography.body1
 * @param [keyboardOptions] An optional [KeyboardOptions] to customize keyboard options.
 * @param [keyboardActions] An optional [KeyboardActions] to customize keyboard actions.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Suppress("LongMethod")
@Composable
fun TogiCountryCodePicker(
    onValueChange: (Pair<PhoneCode, String>, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = DEFAULT_TEXT_FIELD_SHAPE,
    showCountryCode: Boolean = true,
    showCountryFlag: Boolean = true,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(),
    fallbackCountry: CountryData = CountryData.India,
    showPlaceholder: Boolean = true,
    includeOnly: ImmutableSet<String>? = null,
    clearIcon: ImageVector? = Icons.Filled.Clear,
    initialPhoneNumber: String? = null,
    initialCountryIsoCode: Iso31661alpha2? = null,
    initialCountryPhoneCode: PhoneCode? = null,
    label: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = MaterialTheme.typography.body1.copy(
        color = MaterialTheme.colors.onSurface,
    ),
    phoneNumberIntentSender: PhoneNumberRetrievalResultSender? = null,
    keyboardOptions: KeyboardOptions? = null,
    keyboardActions: KeyboardActions? = null,
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    var phoneNumber by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialPhoneNumber.orEmpty(),
                selection = TextRange(initialPhoneNumber?.length ?: 0),
            ),
        )
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    var country: CountryData by rememberSaveable(
        context,
        initialCountryPhoneCode,
        initialCountryIsoCode,
    ) {
        if (initialPhoneNumber?.startsWith("+") == true) {
            Log.e(TAG, "initialPhoneNumber must not include the country code")
        }
        if (initialCountryPhoneCode?.run { !startsWith("+") } == true) {
            Log.e(TAG, "initialCountryPhoneCode must start with +")
        }
        mutableStateOf(
            initialCountryPhoneCode?.let { getCountryFromPhoneCode(it, context) }
                ?: CountryData.entries.firstOrNull { it.countryIso == initialCountryIsoCode }
                ?: CountryData.isoMap[getUserIsoCode(context)]
                ?: fallbackCountry,
        )
    }

    val phoneNumberTransformation = remember(country) {
        PhoneNumberTransformation(country.countryIso, context)
    }
    val phoneNumberUtils = remember(context) { PhoneNumberUtils(context) }

    var isNumberValid: Boolean by rememberSaveable(country, phoneNumber) {
        mutableStateOf(
            phoneNumberUtils.isValidPhoneNumber(
                fullPhoneNumber = country.countryPhoneCode + phoneNumber.text,
            ),
        )
    }

    val coroutineScope = rememberCoroutineScope()

    OutlinedTextField(
        value = phoneNumber,
        onValueChange = { enteredPhoneNumber ->
            val preFilteredPhoneNumber = phoneNumberTransformation.preFilter(enteredPhoneNumber)
            phoneNumber = TextFieldValue(
                text = preFilteredPhoneNumber,
                selection = TextRange(preFilteredPhoneNumber.length),
            )
            isNumberValid = phoneNumberUtils.isValidPhoneNumber(
                fullPhoneNumber = country.countryPhoneCode + phoneNumber.text,
            )
            onValueChange(country.countryPhoneCode to phoneNumber.text, isNumberValid)
        },
        modifier = modifier
            .fillMaxWidth()
            .focusable()
            .autofill(
                autofillTypes = listOf(AutofillType.PhoneNumber),
                phoneNumberIntentSender = phoneNumberIntentSender,
                onFill = { filledPhoneNumber ->
                    val preFilteredPhoneNumber =
                        phoneNumberTransformation.preFilter(filledPhoneNumber)
                    isNumberValid =
                        phoneNumberUtils.isValidPhoneNumber(fullPhoneNumber = preFilteredPhoneNumber)
                    val countryCode = phoneNumberUtils.getCountryCode(preFilteredPhoneNumber)
                    country = CountryData.isoMap.getOrDefault(countryCode, country)

                    val nationalPhoneNumber =
                        phoneNumberUtils.getNationalNumber(preFilteredPhoneNumber)

                    phoneNumber = TextFieldValue(
                        text = nationalPhoneNumber,
                        selection = TextRange(nationalPhoneNumber.length),
                    )
                    onValueChange(country.countryPhoneCode to nationalPhoneNumber, isNumberValid)
                    keyboardController?.hide()
                    coroutineScope.launch {
                        focusRequester.safeFreeFocus()
                    }
                },
                focusRequester = focusRequester,
            )
            .focusRequester(focusRequester = focusRequester),
        enabled = enabled,
        textStyle = textStyle,
        label = label,
        placeholder = {
            if (showPlaceholder) {
                PlaceholderNumberHint(country.countryIso)
            }
        },
        leadingIcon = {
            key(country) {
                TogiCodeDialog(
                    selectedCountry = country,
                    includeOnly = includeOnly,
                    onCountryChange = { countryData ->
                        country = countryData
                        isNumberValid = phoneNumberUtils.isValidPhoneNumber(
                            fullPhoneNumber = country.countryPhoneCode + phoneNumber.text,
                        )
                        onValueChange(country.countryPhoneCode to phoneNumber.text, isNumberValid)
                    },
                    showCountryCode = showCountryCode,
                    showFlag = showCountryFlag,
                    textStyle = textStyle,
                )
            }
        },
        trailingIcon = {
            if (clearIcon != null) {
                ClearIconButton(
                    imageVector = clearIcon,
                    colors = colors,
                    isNumberValid = isNumberValid,
                ) {
                    phoneNumber = TextFieldValue("")
                    isNumberValid = false
                    onValueChange(country.countryPhoneCode to phoneNumber.text, isNumberValid)
                }
            }
        },
        isError = !isNumberValid,
        visualTransformation = phoneNumberTransformation,
        keyboardOptions = keyboardOptions ?: KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Phone,
            autoCorrect = true,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = keyboardActions ?: KeyboardActions(
            onDone = {
                keyboardController?.hide()
                coroutineScope.launch {
                    focusRequester.safeFreeFocus()
                }
            },
        ),
        singleLine = true,
        shape = shape,
        colors = colors,
    )
}

private fun FocusRequester.safeFreeFocus() {
    try {
        this.freeFocus()
    } catch (exception: IllegalStateException) {
        Log.e(TAG, "Unable to free focus", exception)
    }
}

@Composable
private fun PlaceholderNumberHint(countryIso: Iso31661alpha2) {
    Text(
        text = stringResource(
            id = numberHint.getOrDefault(countryIso, R.string.unknown),
        ),
    )
}

@Composable
private fun ClearIconButton(
    imageVector: ImageVector,
    colors: TextFieldColors,
    isNumberValid: Boolean,
    onClick: () -> Unit,
) = IconButton(onClick = onClick) {
    Icon(
        imageVector = imageVector,
        contentDescription = stringResource(id = R.string.clear),
        tint = colors.trailingIconColor(enabled = true, isError = !isNumberValid).value,
    )
}

@Preview
@Composable
private fun TogiCountryCodePickerPreview() {
    TogiCountryCodePicker(
        onValueChange = { _, _ -> },
        showCountryCode = true,
        showCountryFlag = true,
        showPlaceholder = true,
        includeOnly = null,
    )
}
