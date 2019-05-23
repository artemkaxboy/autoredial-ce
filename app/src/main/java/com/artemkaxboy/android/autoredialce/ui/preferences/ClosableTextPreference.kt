package com.artemkaxboy.android.autoredialce.ui.preferences

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.content.res.TypedArrayUtils
import androidx.preference.EditTextPreference
import androidx.preference.EditTextPreference.OnBindEditTextListener
import com.artemkaxboy.android.autoredialce.R

open class ClosableTextPreference(
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
        onAttached()
        onBindEditTextListener = OnBindEditTextListener { editText ->
            setupEditText(editText)
        }
        /*preferenceManager?.onDisplayPreferenceDialogListener =
                PreferenceManager.OnDisplayPreferenceDialogListener { preference ->
                    println(preference) }*/
    }

    override fun onAttached() {
        super.onAttached()
        /*preferenceManager?.onDisplayPreferenceDialogListener =
                PreferenceManager.OnDisplayPreferenceDialogListener { preference ->
                    println(preference) }*/
    }

    protected open fun setupEditText(editText: EditText) {
        editText.selectAll()
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitDialog()
            }
            true
        }
    }

    private fun submitDialog() {
//        preferenceManager.showDialog(null)
    }
}