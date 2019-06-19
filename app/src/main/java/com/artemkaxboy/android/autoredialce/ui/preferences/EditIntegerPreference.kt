package com.artemkaxboy.android.autoredialce.ui.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.TypedArrayUtils
import com.artemkaxboy.android.autoredialce.R

@Suppress("unused") // it is used through xml
class EditIntegerPreference(
    context: Context?,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : ClosableTextPreference(context, attrs, defStyleAttr, defStyleRes) {
    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,
            TypedArrayUtils.getAttr(context!!, R.attr.editTextPreferenceStyle,
                    android.R.attr.editTextPreferenceStyle))

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, 0)

    private val minValueIndex = R.styleable
            .com_artemkaxboy_android_autoredialce_ui_preferences_EditIntegerPreference_minValue
    private val maxValueIndex = R.styleable
            .com_artemkaxboy_android_autoredialce_ui_preferences_EditIntegerPreference_maxValue

    var minValue: Double? = null
    var maxValue: Double? = null
    var summaryTemplate: String? = null

    init {
        val array = context!!.obtainStyledAttributes(attrs, R.styleable
                .com_artemkaxboy_android_autoredialce_ui_preferences_EditIntegerPreference)

        array.getString(minValueIndex)?.let { minValue = it.toDoubleOrNull() }
        array.getString(maxValueIndex)?.let { maxValue = it.toDoubleOrNull() }
        array.getString(maxValueIndex)?.let { summaryTemplate = it }

        array.recycle()
    }
}
