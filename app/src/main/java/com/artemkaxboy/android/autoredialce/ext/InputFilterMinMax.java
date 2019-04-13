package com.artemkaxboy.android.autoredialce.ext;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by artem.kolin on 2016/03/18.
 * Фильтр позволяет вводить в EditText целые и дробные числа в диапазоне.
 */
public class InputFilterMinMax implements InputFilter {
    long mMinLong, mMaxLong;
    double mMinDouble, mMaxDouble;
    boolean isFloat;

    public InputFilterMinMax( long min, long max ) {
        super();
        mMinLong = min;
        mMaxLong = max;
        isFloat = false;
    }
    public InputFilterMinMax( double min, double max ) {
        super();
        mMinDouble = min;
        mMaxDouble = max;
        isFloat = true;
    }
    @Override
    public CharSequence filter( CharSequence source,
                                int start, int end, Spanned dest, int dstart, int dend ) {
        String str = "";
        try {
            str += dest.toString().substring( 0, dstart );
        } catch( Exception ignored ) {}
        str += source.toString();
        try {
            str += dest.toString().substring( dend );
        } catch( Exception ignored ) {}
        try {
            if( isFloat ) {
                double val = Double.parseDouble( str );
                if( val < mMinDouble || val > mMaxDouble )
                    return "";
            } else {
                long val = Long.parseLong( str );
                if( val < mMinLong || val > mMaxLong )
                    return "";
            }
        } catch( Exception e ) {
            return null;
        }
        return source;
    }
}
