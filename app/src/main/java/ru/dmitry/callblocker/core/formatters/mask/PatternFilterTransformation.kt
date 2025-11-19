package ru.dmitry.callblocker.core.formatters.mask

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * PatternFilterTransformation позволяет вводить только значения по паттерну
 **/
open class PatternFilterTransformation(
    private val pattern: Regex = Regex(".")
) : VisualTransformation, Prefilter {

    override fun isAcceptable(text: String): Boolean {
        return text.isEmpty() || pattern.matches(text)
    }

    override fun prefilter(value: TextFieldValue): TextFieldValue {
        return value
    }

    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(text, OffsetMapping.Identity)
    }
}