package com.artemkaxboy.android.autoredialce.ext;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import de.mrapp.android.dialog.MaterialDialog;
import de.mrapp.android.preference.EditTextPreference;
import de.mrapp.android.validation.EditText;

/**
 * Created by artem.kolin on 2016/07/02.
 * EditTextPreference в стиле Material
 */
public class MaterialEditNumberPreference extends EditTextPreference {
    private static final String PREFERENCE_NS = "http://schemas.android.com/apk/res-auto";
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    private static final String ATTR_SUMMARY_TEMPLATE = "summaryTemplate";

    private static final String ATTR_DEFAULT_VALUE = "defaultValue";
    private static final String ATTR_INPUT_TYPE = "inputType";

    private static final String ATTR_MIN_VALUE = "minValue";
    private static final String ATTR_MAX_VALUE = "maxValue";

    long mMinLong, mMaxLong, mDefaultLong, mCurrentLong;
    double mMinDouble, mMaxDouble, mDefaultDouble, mCurrentDouble;
    boolean isFloat = false, isSigned = false;
    String mSummaryTemplate = "%s";


    EditText mEditText;
    EditText getEditText() { return getEditText( false ); }
    EditText getEditText( boolean renew ) {
        if( renew || mEditText == null )
            mEditText = (EditText)getDialog().findViewById( android.R.id.edit );
        return mEditText;
    }

    MaterialDialog getMaterialDialog() {
        return (MaterialDialog)getDialog();
    }

    @SuppressWarnings("unused")
    public MaterialEditNumberPreference(@NonNull Context context,
                                        @Nullable AttributeSet attributeSet) throws CustomException {
        super(context, attributeSet);
        init( attributeSet );
    }

    @SuppressWarnings("unused")
    public MaterialEditNumberPreference(@NonNull Context context,
                                        @Nullable AttributeSet attributeSet,
                                        @AttrRes int defaultStyle) throws CustomException {
        super(context, attributeSet, defaultStyle);
        init( attributeSet );
    }

    @SuppressWarnings("unused")
    public MaterialEditNumberPreference(@NonNull Context context,
                                        @Nullable AttributeSet attributeSet,
                                        @AttrRes int defaultStyle,
                                        @StyleRes int defaultStyleResource) throws CustomException {
        super(context, attributeSet, defaultStyle, defaultStyleResource);
        init( attributeSet );
    }

    void init( AttributeSet attrs ) throws CustomException {
        if(( attrs.getAttributeIntValue( ATTR_SUMMARY_TEMPLATE, ATTR_INPUT_TYPE, 0 )
                & android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL ) > 0 )
            isFloat = true;
        if(( attrs.getAttributeIntValue( ATTR_SUMMARY_TEMPLATE, ATTR_INPUT_TYPE, 0 )
                & android.text.InputType.TYPE_NUMBER_FLAG_SIGNED ) > 0 )
            isSigned = true;
        int templateRes = attrs.getAttributeResourceValue( PREFERENCE_NS, ATTR_SUMMARY_TEMPLATE, 0 );
        if( templateRes != 0 ) mSummaryTemplate = getContext().getString( templateRes );

        if( isFloat ) {
            try {
                mMinDouble = Double.parseDouble( attrs.getAttributeValue( PREFERENCE_NS, ATTR_MIN_VALUE ));
            } catch( Exception e ) {
                if( isSigned )
                    mMinDouble = Integer.MIN_VALUE;
                else
                    mMinDouble = 0;
            }
            try {
                mMaxDouble = Double.parseDouble( attrs.getAttributeValue( PREFERENCE_NS, ATTR_MAX_VALUE ));
            } catch( Exception e ) {
                mMaxDouble = Integer.MAX_VALUE;
            }
            try {
                mDefaultDouble = Double.parseDouble( attrs.getAttributeValue( ANDROID_NS, ATTR_DEFAULT_VALUE ));
            } catch( Exception e ) {
                if( mMinDouble > 0 )
                    mDefaultDouble = mMinDouble;
                else if( mMaxDouble < 0 )
                    mDefaultDouble = mMaxDouble;
                else
                    mDefaultDouble = 0;
            }
            if( mMinDouble > mMaxDouble || mDefaultDouble > mMaxDouble || mDefaultDouble < mMinDouble )
                throw new CustomException( "Check Your min/max/default values in XML file." );
        } else {
            try {
                mMinLong = Long.parseLong(attrs.getAttributeValue(PREFERENCE_NS, ATTR_MIN_VALUE));
            } catch (Exception e) {
                if (isSigned)
                    mMinLong = Integer.MIN_VALUE;
                else
                    mMinLong = 0;
            }
            try {
                mMaxLong = Long.parseLong(attrs.getAttributeValue(PREFERENCE_NS, ATTR_MAX_VALUE));
            } catch (Exception e) {
                mMaxLong = Integer.MAX_VALUE;
            }
            try {
                mDefaultLong = Long.parseLong(attrs.getAttributeValue(ANDROID_NS, ATTR_DEFAULT_VALUE));
            } catch (Exception e) {
                if (mMinLong > 0)
                    mDefaultLong = mMinLong;
                else if (mMaxLong < 0)
                    mDefaultLong = mMaxLong;
                else
                    mDefaultLong = 0;
            }
            if (mMinLong > mMaxLong || mDefaultLong > mMaxLong || mDefaultLong < mMinLong)
                throw new CustomException("Check Your min/max/default values in XML file.");
        }

        setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference p) {
                String value;
                if( isFloat ) {
                    value = getPersistedString( Double.toString( mDefaultDouble ));
                    mCurrentDouble = Double.valueOf( value );
                } else {
                    value = getPersistedString( Long.toString( mDefaultLong ));
                    mCurrentLong = Long.valueOf( value );
                }
                getEditText( true ).setText(value);
                getEditText().setSelectAllOnFocus( true );
                getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            try {
                                getMaterialDialog()
                                        .getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                                return true;
                            } catch (Exception ignore) {
                                ignore.printStackTrace();
                            }
                        }
                        return false;
                    }
                });
                getEditText().setInputType( android.text.InputType.TYPE_CLASS_NUMBER );
                if( isSigned ) getEditText().setInputType( android.text.InputType.TYPE_NUMBER_FLAG_SIGNED );
                if( isFloat ) {
                    getEditText().setInputType( android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL );
                    getEditText().setFilters( new InputFilter[]{ new InputFilterMinMax( mMinDouble, mMaxDouble )});
                    getEditText().addTextChangedListener( new TextWatcher(){
                        @Override public void afterTextChanged( Editable arg0 ) {}

                        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged( CharSequence s, int start, int before, int count ) {
                            try {
                                mCurrentDouble = Double.valueOf( s.toString());
                            } catch( Exception ignore ) {}
                        }
                    });
                } else {
                    getEditText().setFilters( new InputFilter[]{ new InputFilterMinMax( mMinLong, mMaxLong )});
                    getEditText().addTextChangedListener( new TextWatcher(){
                        @Override public void afterTextChanged( Editable arg0 ) {}

                        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged( CharSequence s, int start, int before, int count ) {
                            try {
                                mCurrentLong = Long.valueOf( s.toString());
                            } catch( Exception ignore ) {}
                        }
                    });
                }


                return false;
            }
        });
    }



    @Override
    protected boolean persistString(String value) {
        try {
            String clearValue;
            if( isFloat )
                clearValue = Double.toString( Double.valueOf( value ));
            else
                clearValue = Long.toString( Long.valueOf( value ));
            setCustomSummary( clearValue );
            return super.persistString( clearValue );
        } catch( Exception e ) {
            setCustomSummary( "0" );
            return super.persistString( "0" );
        }
    }

    void setCustomSummary( String value ) {
        try {
            setSummary( String.format( mSummaryTemplate, value ));
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
}
