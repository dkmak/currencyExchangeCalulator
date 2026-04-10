package com.currencyexchangecalculator.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.currencyexchangecalculator.R

@Composable
fun CustomRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val selectableModifier =
        if (onClick != null) {
            Modifier.selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 40.0.dp / 2),
            )
        } else {
            Modifier
        }

    if (selected){
        Icon(
            painterResource(R.drawable.radio_success),
            contentDescription = "",
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )
    }else {
        Canvas(
            modifier
                .then(
                    if (onClick != null) {
                        Modifier.minimumInteractiveComponentSize()
                    } else {
                        Modifier
                    }
                )
                .then(selectableModifier)
                .wrapContentSize(Alignment.Center)
                .padding(RadioButtonPadding)
                .requiredSize(20.0.dp)
        ) {
            // Draw the radio button
            val strokeWidth = RadioStrokeWidth.toPx()
            drawCircle(
                colors.unselectedColor,
                radius = (20.0.dp / 2).toPx() - strokeWidth / 2,
                style = Stroke(strokeWidth),
            )
        }
    }
}

private val RadioButtonPadding = 2.dp
private val RadioStrokeWidth = 2.dp
