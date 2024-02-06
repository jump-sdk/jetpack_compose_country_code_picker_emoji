# Jetpack Compose Country Code Picker Emoji

[![Android CI](https://github.com/jump-sdk/jetpack_compose_country_code_picker_emoji/actions/workflows/android.yml/badge.svg)](https://github.com/jump-sdk/jetpack_compose_country_code_picker_emoji/actions/workflows/android.yml)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=jump-sdk_jetpack_compose_country_code_picker_emoji&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=jump-sdk_jetpack_compose_country_code_picker_emoji)
[![Jitpack](https://jitpack.io/v/jump-sdk/jetpack_compose_country_code_picker_emoji.svg)](https://jitpack.io/#jump-sdk/jetpack_compose_country_code_picker_emoji)
[![Documentation](https://img.shields.io/badge/Documentation-3d3d41?logo=kotlin)](https://jump-sdk.github.io/jetpack_compose_country_code_picker_emoji/)

* 😍 Emoji flag icons - beautiful and minimizes library size
* 🤔 Country numbers hints (option to disable)
* 🔢 Phone number visualTransformation (Automatic number formatting)
* 🇺🇳 Automatic country recognition (detection by sim card if sim card is inserted)
* ⚙️ Advanced customization options (see usage below)
* 🏁 Fast! Flattened layouts and immutable collections prevent unwanted recomposition.
* ✅ Code quality checks with Ktlint and Detekt, snapshot testing with Paparazzi
* 🗣 Multilingual: Turkish, English, Italian, Arabic, Russian, Dutch, Spanish, Somali, French, Chinese, Hindi

## Screenshots

<div class="row">
  <img src="screenshots/1.png" width="300"> 
  <img src="screenshots/2.png" width="300"> 
  <img src="screenshots/3.png" width="300"> 
  <img src="screenshots/4.png" width="300"> 
 </div>



## Usage


See [MainActivity in the sample app](https://github.com/jump-sdk/jetpack_compose_country_code_picker_emoji/blob/master/app/src/main/java/com/togitech/togii/MainActivity.kt) for a full example.
Also check out the [ComposeCountryCodePicker documentation](https://jump-sdk.github.io/jetpack_compose_country_code_picker_emoji/ccp/com.togitech.ccp.component/-togi-country-code-picker.html) for all available composables and utilities.

```kotlin 
var phoneNumber: String by rememberSaveable { mutableStateOf("") }
var fullPhoneNumber: String by rememberSaveable { mutableStateOf("") }
var isNumberValid: Boolean by rememberSaveable { mutableStateOf(false) }

TogiCountryCodePicker(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
    onValueChange = { (code, phone), isValid ->
        Log.d("CCP", "onValueChange: $code $phone -> $isValid")

        phoneNumber = phone
        fullPhoneNumber = code + phone
        isNumberValid = isValid
    },
    label = { Text("Phone Number") },
)
```


| Parameter               | Description                                                                                                                                                                                                                                                |
|-------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| onValueChange           | Called when the text in the text field changes. The first parameter is string pair of (country phone code, phone number) and the second parameter is a boolean indicating whether the phone number is valid.                                               |
| modifier                | Modifier to be applied to the inner OutlinedTextField.                                                                                                                                                                                                     |
| enabled                 | Boolean indicating whether the field is enabled.                                                                                                                                                                                                           |
| shape                   | Shape of the text field.                                                                                                                                                                                                                                   |
| showCountryCode         | Whether to show the country code in the text field.                                                                                                                                                                                                        |
| showCountryFlag         | Whether to show the country flag in the text field.                                                                                                                                                                                                        |
| colors                  | TextFieldColors to be used for the text field. Defaults to [outlinedTextFieldColors](https://developer.android.com/reference/kotlin/androidx/compose/material/TextFieldDefaults)                                                                           |
| fallbackCountry         | The country to be used as a fallback if the user's country cannot be determined. Defaults to the United States. See [CountryData](https://jump-sdk.github.io/jetpack_compose_country_code_picker_emoji/ccp/com.togitech.ccp.data/-country-data/index.html) |
| showPlaceholder         | Whether to show the placeholder number hint in the text field.                                                                                                                                                                                             |
| includeOnly             | A set of 2 digit country codes to be included in the list of countries. Set to null to include all supported countries.                                                                                                                                    |
| clearIcon               | ImageVector to be used for the clear button. Set to null to disable the clear button. Defaults to Icons.Filled.Clear                                                                                                                                       |
| initialPhoneNumber      | An optional phone number to be initial value of the input field.                                                                                                                                                                                           |
| initialCountryIsoCode   | Optional ISO-3166-1 alpha-2 country code to set the initially selected country. Note that if a valid initialCountryPhoneCode is provided, this will be ignored.                                                                                            |
| initialCountryPhoneCode | Optional country phone code to set the initially selected country. This takes precedence over initialCountryIsoCode.                                                                                                                                       |
| label                   | An optional composable to be used as a label for input field.                                                                                                                                                                                              |                  
| textStyle               | An optional [TextStyle](https://developer.android.com/reference/kotlin/androidx/compose/ui/text/TextStyle) for customizing text style of phone number input field. Defaults to MaterialTheme.typography.body1                                              |
| keyboardOptions         | An optional [KeyboardOptions](https://developer.android.com/reference/kotlin/androidx/compose/foundation/text/KeyboardOptions) to customize keyboard options.                                                                                              |
| keyboardActions         | An optional [KeyboardActions](https://developer.android.com/reference/kotlin/androidx/compose/foundation/text/KeyboardActions) to customize keyboard actions.                                                                                              |
| showError               | Whether to show error on field when number is invalid, default true.                                                                                                                                                                                       |


## How to add in your project

Step 1: In the build.gradle add Jitpack repository.

In Groovy:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

In Kts:

```kotlin
repositories {
    maven("https://jitpack.io")
}
```

Step 2. Add the dependency - n.b. this currently does not support Material 3.

```kotlin 
dependencies {
    implementation("androidx.compose.material:material:1.6.0")
    implementation("com.github.jump-sdk:jetpack_compose_country_code_picker_emoji:2.2.6")
}  
```

## Contributing

* [PRs](https://github.com/jump-sdk/jetpack_compose_country_code_picker_emoji/pulls) and [bug reports / feature requests](https://github.com/jump-sdk/jetpack_compose_country_code_picker_emoji/issues) are welcome!
* This project is linted with [ktlint](https://github.com/pinterest/ktlint) and statically checked with [detekt](https://github.com/detekt/detekt)
* Additional checking done with [Twitter's](https://twitter.github.io/compose-rules/detekt/) and [appKODE's](https://github.com/appKODE/detekt-rules-compose) Jetpack Compose rules for Detekt
* Treat other people with helpfulness, gratitude, and consideration! See the [Code of Conduct](https://github.com/jump-sdk/jetpack_compose_country_code_picker_emoji/blob/master/CODE_OF_CONDUCT.md)
* Use precommit hook `./gradlew :ccp:detektMain :ccp:lint :ccp:verifyPaparazziDebug` to check code quality before commit


## Based on [togisoft's Jetpack Compose Country Code Picker](https://github.com/togisoft/jetpack_compose_country_code_picker)

All support goes to the original author

<a href="https://www.buymeacoffee.com/togitech" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" style="height: 60px !important;width: 217px !important;" ></a>