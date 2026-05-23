package com.worksi.app.ui.register

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.worksi.app.ui.theme.DarkGreen
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.worksiRegisterOutlinedFieldColors

val RegisterFieldShape = RoundedCornerShape(12.dp)

private val RegisterNormalUnfocusedBorder = 1.dp
private val RegisterNormalFocusedBorder = 2.dp
private val RegisterErrorBorderWidth = 2.5.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: TextFieldColors = worksiRegisterOutlinedFieldColors(),
    shape: Shape = RegisterFieldShape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val textColor =
        when {
            !enabled -> DarkGreen.copy(alpha = 0.55f)
            else -> DarkGreen
        }
    val focusedThickness = if (isError) RegisterErrorBorderWidth else RegisterNormalFocusedBorder
    val unfocusedThickness = if (isError) RegisterErrorBorderWidth else RegisterNormalUnfocusedBorder

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        minLines = if (singleLine) 1 else minLines,
        maxLines = if (singleLine) 1 else maxOf(minLines, 8),
        textStyle = LocalTextStyle.current.copy(color = textColor),
        cursorBrush = SolidColor(if (isError) OrangeAccent else DarkGreen),
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = singleLine,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                isError = isError,
                label = label,
                trailingIcon = trailingIcon,
                colors = colors,
                contentPadding = OutlinedTextFieldDefaults.contentPadding(),
                container = {
                    OutlinedTextFieldDefaults.ContainerBox(
                        enabled = enabled,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = colors,
                        shape = shape,
                        focusedBorderThickness = focusedThickness,
                        unfocusedBorderThickness = unfocusedThickness,
                    )
                }
            )
        }
    )
}

@Composable
fun RegisterRequiredFieldsHint(visible: Boolean) {
    if (visible) {
        Text(
            text = "Completa los campos requeridos",
            color = OrangeAccent,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
    }
}
