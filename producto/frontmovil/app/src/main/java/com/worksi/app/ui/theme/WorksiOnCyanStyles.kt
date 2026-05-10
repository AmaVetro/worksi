package com.worksi.app.ui.theme

import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable

@Composable
fun worksiOnCyanOutlinedFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor = White,
        unfocusedBorderColor = White.copy(alpha = 0.5f),
        disabledBorderColor = White.copy(alpha = 0.35f),
        focusedLabelColor = White,
        unfocusedLabelColor = White.copy(alpha = 0.7f),
        disabledLabelColor = White.copy(alpha = 0.5f),
        cursorColor = White,
        focusedTextColor = White,
        unfocusedTextColor = White,
        disabledTextColor = White.copy(alpha = 0.55f),
        focusedTrailingIconColor = White,
        unfocusedTrailingIconColor = White.copy(alpha = 0.75f),
        disabledTrailingIconColor = White.copy(alpha = 0.4f),
        errorBorderColor = OrangeAccent,
        errorLabelColor = OrangeAccent,
        errorCursorColor = OrangeAccent,
        errorTrailingIconColor = OrangeAccent,
        focusedPlaceholderColor = White.copy(alpha = 0.5f),
        unfocusedPlaceholderColor = White.copy(alpha = 0.45f)
    )
}

@Composable
fun worksiOnCyanFilterChipColors() =
    FilterChipDefaults.filterChipColors(
        containerColor = White.copy(alpha = 0.18f),
        labelColor = White,
        selectedContainerColor = OrangeAccent,
        selectedLabelColor = White,
        iconColor = White.copy(alpha = 0.85f),
        selectedLeadingIconColor = White
    )

@Composable
fun worksiOnCyanCheckboxColors() =
    CheckboxDefaults.colors(
        checkedColor = OrangeAccent,
        uncheckedColor = White.copy(alpha = 0.75f),
        checkmarkColor = White
    )
