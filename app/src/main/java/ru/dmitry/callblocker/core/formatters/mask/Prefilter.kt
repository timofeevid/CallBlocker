package ru.dmitry.callblocker.core.formatters.mask

import androidx.compose.ui.text.input.TextFieldValue

/**
 * Интерфейс для расширения [androidx.compose.ui.text.input.VisualTransformation], который позволяет синхронно
 * подготовить данные поля ввода перед вызывом визуальной трансформации
 * */
interface Prefilter {

    /**Проверяет допустимость вводимых данных*/
    fun isAcceptable(text: String): Boolean

    /**Подготавливает данные для записи в состояние textFieldValue*/
    fun prefilter(value: TextFieldValue): TextFieldValue
}