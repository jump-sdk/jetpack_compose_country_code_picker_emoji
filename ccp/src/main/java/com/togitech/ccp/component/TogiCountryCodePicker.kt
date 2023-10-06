package com.togitech.ccp.component

import android.util.Log
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.togitech.ccp.R
import com.togitech.ccp.data.CountryData
import com.togitech.ccp.data.Iso31661alpha2
import com.togitech.ccp.data.PhoneCode
import com.togitech.ccp.data.utils.ValidatePhoneNumber
import com.togitech.ccp.data.utils.getCountryFromPhoneCode
import com.togitech.ccp.data.utils.getUserIsoCode
import com.togitech.ccp.data.utils.numberHint
import com.togitech.ccp.transformation.PhoneNumberTransformation
import kotlinx.collections.immutable.ImmutableSet

private val DEFAULT_TEXT_FIELD_SHAPE = RoundedCornerShape(24.dp)
private const val TAG = "TogiCountryCodePicker"

/**
 * @param onValueChange Called when the text in the text field changes.
 * The first parameter is string pair of (country code, phone number) and the second parameter is
 * a boolean indicating whether the phone number is valid.
 * @param modifier Modifier to be applied to the inner OutlinedTextField.
 * @param enabled Boolean indicating whether the field is enabled.
 * @param shape Shape of the text field.
 * @param showCountryCode Whether to show the country code in the text field.
 * @param showCountryFlag Whether to show the country flag in the text field.
 * @param colors Colors to be used for the text field.
 * @param fallbackCountry The country to be used as a fallback if the user's country cannot be determined.
 * Defaults to the United States.
 * @param showPlaceholder Whether to show the placeholder number in the text field.
 * @param includeOnly A set of 2 digit country codes to be included in the list of countries.
 * Set to null to include all supported countries.
 * @param clearIcon The icon to be used for the clear button. Set to null to disable the clear button.
 * @param initialPhoneNumber an optional phone number to be initial value of the input field
 * @param initialCountryIsoCode Optional ISO-3166-1 alpha-2 country code to set the initially selected country.
 * Note that if a valid initialCountryPhoneCode is provided, this will be ignored.
 * @param initialCountryPhoneCode Optional country phone code to set the initially selected country.
 * This takes precedence over [initialCountryIsoCode].
 * @param label An optional composable to be used as a label for input field
 * @param textStyle An optional [TextStyle] for customizing text style of phone number input field
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
    fallbackCountry: CountryData = CountryData.UnitedStates,
    showPlaceholder: Boolean = true,
    includeOnly: ImmutableSet<String>? = null,
    clearIcon: ImageVector? = Icons.Filled.Clear,
    initialPhoneNumber: String? = null,
    initialCountryIsoCode: Iso31661alpha2? = null,
    initialCountryPhoneCode: PhoneCode? = null,
    label: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = MaterialTheme.typography.body1,
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    var phoneNumber by rememberSaveable(initialPhoneNumber) {
        mutableStateOf(initialPhoneNumber.orEmpty())
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
        if (initialCountryPhoneCode?.startsWith("+") != true) {
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
    val validatePhoneNumber = remember(context) { ValidatePhoneNumber(context) }

    var isNumberValid: Boolean by rememberSaveable(country, phoneNumber) {
        mutableStateOf(
            validatePhoneNumber(
                fullPhoneNumber = country.countryPhoneCode + phoneNumber,
            ),
        )
    }

    OutlinedTextField(
        value = phoneNumber,
        onValueChange = { enteredPhoneNumber ->
            phoneNumber = phoneNumberTransformation.preFilter(enteredPhoneNumber)
            isNumberValid = validatePhoneNumber(
                fullPhoneNumber = country.countryPhoneCode + phoneNumber,
            )
            onValueChange(country.countryPhoneCode to phoneNumber, isNumberValid)
        },
        modifier = modifier
            .fillMaxWidth()
            .focusable()
            .autofill(
                autofillTypes = listOf(AutofillType.PhoneNumberNational),
                onFill = {
                    phoneNumber = phoneNumberTransformation.preFilter(it)
                    isNumberValid = validatePhoneNumber(
                        fullPhoneNumber = country.countryPhoneCode + phoneNumber,
                    )
                    onValueChange(country.countryPhoneCode to phoneNumber, isNumberValid)
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
            TogiCodeDialog(
                modifier = Modifier.padding(DEFAULT_PADDING),
                selectedCountry = country,
                includeOnly = includeOnly,
                onCountryChange = { countryData ->
                    country = countryData
                    isNumberValid = validatePhoneNumber(
                        fullPhoneNumber = country.countryPhoneCode + phoneNumber,
                    )
                    onValueChange(country.countryPhoneCode to phoneNumber, isNumberValid)
                },
                showCountryCode = showCountryCode,
                showFlag = showCountryFlag,
                textStyle = textStyle,
            )
        },
        trailingIcon = {
            clearIcon?.let {
                IconButton(
                    onClick = {
                        phoneNumber = ""
                        isNumberValid = false
                        onValueChange(country.countryPhoneCode to phoneNumber, isNumberValid)
                    },
                ) {
                    Icon(
                        imageVector = it,
                        contentDescription = "Clear",
                        tint = if (!isNumberValid) {
                            colors
                                .trailingIconColor(enabled = true, isError = true)
                                .value
                        } else {
                            colors
                                .trailingIconColor(enabled = true, isError = false)
                                .value
                        },
                    )
                }
            }
        },
        isError = !isNumberValid,
        visualTransformation = phoneNumberTransformation,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Phone,
            autoCorrect = true,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusRequester.freeFocus()
            },
        ),
        singleLine = true,
        shape = shape,
        colors = colors,
    )
}

@Composable
private fun PlaceholderNumberHint(countryIso: Iso31661alpha2) {
    Text(
        text = stringResource(
            id = numberHint.getOrDefault(countryIso, R.string.unknown),
        ),
    )
}

@ExperimentalComposeUiApi
internal fun Modifier.autofill(
    autofillTypes: List<AutofillType>,
    onFill: (String) -> Unit,
    focusRequester: FocusRequester,
): Modifier = this then composed {
    val autofill = LocalAutofill.current
    val autofillNode = AutofillNode(onFill = onFill, autofillTypes = autofillTypes)
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
                    requestAutofillForNode(autofillNode)
                } else {
                    cancelAutofillForNode(autofillNode)
                }
            }
        }
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
