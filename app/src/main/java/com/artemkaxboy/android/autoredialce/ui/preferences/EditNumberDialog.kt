package com.artemkaxboy.android.autoredialce.ui.preferences

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText

class EditNumberDialog : androidx.preference.EditTextPreferenceDialogFragmentCompat() {
    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        view?.findViewById<EditText>(android.R.id.edit)?.let { editText ->
            editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    dialog?.findViewById<Button>(android.R.id.button1)?.performClick()
                    true
                }
                false
            }
            editText.imeOptions = EditorInfo.IME_ACTION_DONE
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.selectAll()
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