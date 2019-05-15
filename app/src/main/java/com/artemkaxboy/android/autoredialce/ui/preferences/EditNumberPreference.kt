package com.artemkaxboy.android.autoredialce.ui.preferences

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import androidx.core.content.res.TypedArrayUtils
import androidx.preference.EditTextPreference
import com.artemkaxboy.android.autoredialce.R

@Suppress("unused") // it is used through xml
class EditNumberPreference(
    context: Context?,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : EditTextPreference(context, attrs, defStyleAttr, defStyleRes) {
    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,
            TypedArrayUtils.getAttr(context!!, R.attr.editTextPreferenceStyle,
                    android.R.attr.editTextPreferenceStyle))

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, 0)

    init {
        onBindEditTextListener = OnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.selectAll()
        }
    }
}