package com.currencyexchangecalculator.presentation.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }

        val formatted = formatWithCommas(raw)
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, raw.length)
                return formatWithCommas(raw.take(safeOffset)).length
            }

            override fun transformedToOriginal(offset: Int): Int {
                val safeOffset = offset.coerceIn(0, formatted.length)
                return formatted
                    .take(safeOffset)
                    .count { it.isDigit() || it == '.' }
                    .coerceAtMost(raw.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }

    private fun formatWithCommas(input: String): String {
        val parts = input.split(".", limit = 2)
        val integerPart = parts.firstOrNull().orEmpty().filter { it.isDigit() }
        val decimalPart = parts.getOrNull(1)?.filter { it.isDigit() }

        if (integerPart.isEmpty()) {
            return input
        }

        val groupedInteger = integerPart
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()

        return when {
            input.endsWith(".") -> "$$groupedInteger."
            decimalPart != null -> "$$groupedInteger.$decimalPart"
            else -> "$$groupedInteger"
        }
    }
}