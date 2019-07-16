package com.artemkaxboy.android.autoredialce.ui.preferences

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import com.artemkaxboy.android.autoredialce.ext.InputFilterMinMax

class EditNumberDialog : androidx.preference.EditTextPreferenceDialogFragmentCompat() {

    var editText: EditText? = null

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        editText = view?.findViewById<EditText>(android.R.id.edit)
        editText?.let {
            setExtraAttrs(it)

            it.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    dialog?.findViewById<Button>(android.R.id.button1)?.performClick()
                }
                true
            }
            it.imeOptions = EditorInfo.IME_ACTION_DONE
            it.inputType = InputType.TYPE_CLASS_NUMBER
            it.selectAll()
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult && editText != null && editText!!.text.isNotEmpty()) {
            // trim insignificant zeros
            val value = editText!!.text.toString().toDouble()
            val string = if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
            editText!!.setText(string)
            super.onDialogClosed(positiveResult)
        }
    }

    private fun setExtraAttrs(editText: EditText) {
        if (preference is EditIntegerPreference) {
            val editIntegerPreference = preference as EditIntegerPreference?
            editText.filters = arrayOf(
                    InputFilterMinMax(editIntegerPreference?.minValue ?: Double.MIN_VALUE,
                            editIntegerPreference?.maxValue ?: Double.MAX_VALUE))
        }
    }

    companion object {
        fun newInstance(key: String): EditNumberDialog {
            val fragment = EditNumberDialog()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }
}