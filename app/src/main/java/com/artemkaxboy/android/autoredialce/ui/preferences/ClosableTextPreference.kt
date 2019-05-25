package com.artemkaxboy.android.autoredialce.ui.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.TypedArrayUtils
import androidx.preference.EditTextPreference
import com.artemkaxboy.android.autoredialce.R

open class ClosableTextPreference(
    context: Context?,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : EditTextPreference(context, attrs, defStyleAttr, defStyleRes) {
    @Suppress("unused") /* might be used through xml */
    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) :
            this(context, attrs,
                    TypedArrayUtils.getAttr(context!!, R.attr.editTextPreferenceStyle,
                            android.R.attr.editTextPreferenceStyle))

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, 0)
}