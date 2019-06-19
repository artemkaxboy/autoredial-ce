package com.artemkaxboy.android.autoredialce.ext

import android.text.InputFilter
import android.text.Spanned

/**
 * Created by artem.kolin on 2016/03/18.
 *
 * Filter allows to enter integer and float numbers into EditText within range.
 * <b>Caution</b> - it allows to enter empty string
 */
class InputFilterMinMax(private val min: Double, private val max: Double) : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence {

        val result = dest.subSequence(0, dstart).toString() + source +
                dest.subSequence(dend, dest.length).toString()

        try {
            val value = java.lang.Double.parseDouble(result)
            if (value < min || value > max) {
                return ""
            }
        } catch (e: Exception) {
            return ""
        }

        return source
    }
}
