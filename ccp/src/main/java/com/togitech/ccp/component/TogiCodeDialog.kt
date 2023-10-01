package com.togitech.ccp.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.togitech.ccp.data.CountryData
import com.togitech.ccp.data.utils.emojiFlag
import com.togitech.ccp.data.utils.sortedByLocalizedName
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList

internal val DEFAULT_PADDING = 10.dp

@Composable
internal fun TogiCodeDialog(
    selectedCountry: CountryData,
    includeOnly: ImmutableSet<String>?,
    onCountryChange: (CountryData) -> Unit,
    textColor: Color,
    showCountryCode: Boolean,
    showFlag: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var country by remember { mutableStateOf(selectedCountry) }
    var isOpenDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val allCountries = CountryData.entries.sortedByLocalizedName(context)
    val countryList = includeOnly?.run {
        val includeUppercase = map { it.uppercase() }
        allCountries.filter { it.countryIso in includeUppercase }
    } ?: allCountries

    Column(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                isOpenDialog = true
            },
    ) {
        CountryRow(
            showCountryCode = showCountryCode,
            showFlag = showFlag,
            country = country,
            textColor = textColor,
        )

        if (isOpenDialog) {
            CountryDialog(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(DEFAULT_ROUNDING)),
                onDismissRequest = { isOpenDialog = false },
                onSelect = { countryItem ->
                    onCountryChange(countryItem)
                    country = countryItem
                    isOpenDialog = false
                },
                filteredCountryList = countryList.toImmutableList(),
            )
        }
    }
}

@Composable
private fun CountryRow(
    showCountryCode: Boolean,
    showFlag: Boolean,
    country: CountryData,
    textColor: Color,
    fontWeight: FontWeight = FontWeight.Medium,
) = Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
) {
    Text(
        text = emojiCodeText(
            showFlag = showFlag,
            isPickCountry = country,
            showCountryCode = showCountryCode,
        ),
        modifier = Modifier.padding(start = DEFAULT_PADDING),
        fontStyle = MaterialTheme.typography.body1.fontStyle,
        fontWeight = fontWeight,
        color = textColor,
    )
    Icon(
        imageVector = Icons.Default.ArrowDropDown,
        contentDescription = null,
        tint = textColor,
    )
}

@Composable
private fun emojiCodeText(
    showFlag: Boolean,
    isPickCountry: CountryData,
    showCountryCode: Boolean,
) = (if (showFlag) isPickCountry.emojiFlag else "") +
    (if (showCountryCode && showFlag) "  " else "") +
    (if (showCountryCode) isPickCountry.countryPhoneCode else "")

@Preview
@Composable
private fun TogiCodeDialogPreview() {
    TogiCodeDialog(
        selectedCountry = CountryData.UnitedStates,
        includeOnly = null,
        textColor = MaterialTheme.colors.onSurface,
        showCountryCode = true,
        showFlag = true,
        onCountryChange = {},
    )
}
