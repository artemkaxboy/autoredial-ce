package com.artemkaxboy.android.autoredialce;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import com.artemkaxboy.android.autoredialce.utils.FirstRunHelper;
import com.artemkaxboy.android.autoredialce.utils.PermissionHelper;
import java.util.ArrayList;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class ActivityMain extends AppCompatPreferenceActivity {
    static final String TAG = "AMain";

    Settings mSettings;

    Settings getSettings() {
        if (mSettings == null) mSettings = new Settings( getContext());
        return mSettings;
    }

    public Context getContext() {
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String fragment = getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT);
        if (fragment == null || fragment.isEmpty()) {
            askPermission();
            FirstRunHelper.INSTANCE.showIfNeeded(getContext());
            setupActionBar(false);
            getFragmentManager().beginTransaction().replace(android.R.id.content,
                    new PreferenceMain()).commit();
        } else
            setupActionBar(true);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar(boolean displayHomeAsUpEnabled) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled( displayHomeAsUpEnabled );
        }
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
    */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceMain.class.getName().equals( fragmentName )
                || PreferenceAutoredial.class.getName().equals( fragmentName )
                || PreferenceAutocallback.class.getName().equals( fragmentName )
                || PreferenceConfirmation.class.getName().equals( fragmentName );
    }/**/

    public static class PreferenceFragmentSec extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                getActivity().finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    public static class PreferenceConfirmation extends PreferenceFragmentSec {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_confirmation);
        }
    }

    public static class PreferenceAutocallback extends PreferenceFragmentSec {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_autocallback);
        }
    }


    public static class PreferenceAutoredial extends PreferenceFragmentSec {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_autoredial);

            findPreference("lastCount").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    return true;
                }
            });

        }
    }

    public static class PreferenceMain extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);
            setHasOptionsMenu(true);

            String version = "";
            try {
                PackageInfo pInfo = getActivity().getPackageManager()
                        .getPackageInfo(getActivity().getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException ignored) {
            }

            final PreferenceCategory additional = (PreferenceCategory) findPreference("additional");

            //TODO uncomment
            /*
            Preference send = new Preference(getActivity());
            send.setTitle("2SIM info");
            send.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DialogInterface.OnClickListener onClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == AlertDialog.BUTTON_POSITIVE) {
                                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }
                                        Cursor c = getActivity().getContentResolver().query(
                                                CallLog.Calls.CONTENT_URI,
                                                null,
                                                null,
                                                null,
                                                CallLog.Calls.DATE + " DESC LIMIT 2");

                                        String text = "";
                                        if (c != null) {
                                            for (int i = 0; i < c.getColumnCount(); i++) {
                                                text += c.getColumnName(i) + "|";
                                            }
                                            text += "\n";
                                            if (c.moveToFirst()) do {
                                                for (int i = 0; i < c.getColumnCount(); i++) {
                                                    text += c.getString(i) + "|";
                                                }
                                                text += "\n";
                                            } while (c.moveToNext());
                                            c.close();
                                        }

                                        Intent mail = new Intent(Intent.ACTION_SEND);
                                        mail.setType("message/rfc822");
                                        mail.putExtra(Intent.EXTRA_EMAIL,
                                                new String[]{"artemkaxboy@gmail.com"});
                                        mail.putExtra(Intent.EXTRA_SUBJECT,
                                                getString(R.string.app_name));
                                        mail.putExtra(Intent.EXTRA_TEXT, text);
                                        startActivity(Intent.createChooser(mail,
                                                getString(R.string.send_mail)));
                                    }
                                }
                            };
                    new AlertDialog.Builder(getActivity())
                            .setMessage(getString(R.string.send_mail_to))
                            .setPositiveButton(android.R.string.yes, onClickListener)
                            .setNegativeButton(android.R.string.no, onClickListener)
                            .setCancelable(true)
                            .create().show();
                    return true;
                }
            });
            additional.addPreference(send);
            */

            Preference rate = new Preference(getActivity());
            rate.setTitle(R.string.rate);
            rate.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
            additional.addPreference(rate);

            Preference about = new Preference(getActivity());
            about.setTitle(getString(R.string.version, version));
            about.setSummary(getString(R.string.developer, "artemkaxboy@gmail.com"));
            about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DialogInterface.OnClickListener onClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == AlertDialog.BUTTON_POSITIVE) {
                                        Intent mail = new Intent(Intent.ACTION_SEND);
                                        mail.setType("message/rfc822");
                                        mail.putExtra(Intent.EXTRA_EMAIL,
                                                new String[]{"artemkaxboy@gmail.com"});
                                        mail.putExtra(Intent.EXTRA_SUBJECT,
                                                getString(R.string.app_name));
                                        startActivity(Intent.createChooser(mail,
                                                getString(R.string.send_mail)));
                                    }
                                }
                            };
                    new AlertDialog.Builder(getActivity())
                            .setMessage(getString(R.string.send_mail_to))
                            .setPositiveButton(android.R.string.yes, onClickListener)
                            .setNegativeButton(android.R.string.no, onClickListener)
                            .setCancelable(true)
                            .create().show();
                    return true;
                }
            });
            additional.addPreference(about);

            if (P.redialing(getActivity())) {
                final Preference reset = new Preference(getActivity());
                reset.setKey("reset");
                reset.setTitle(R.string.reset);
                reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        getActivity().sendBroadcast(
                                new Intent(ReceiverCommand.ACTION_REDIALING_STOP));
                        additional.removePreference(reset);
                        return true;
                    }
                });
                additional.addPreference(reset);
            }

            Preference policy = new Preference(getActivity());
            policy.setTitle( R.string.policy );
            policy.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse( "http://artemkaxboy.com/policy_autoredial.html" )));
            additional.addPreference(policy);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        }
    }

    /*
     * ANDROID 6 Permissions
     */
    static final int MY_PERMISSIONS_REQUEST_READ = 12;
    void askPermission() {
        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.M ) return;
        ArrayList<String> permissions = new ArrayList<>();
        if( ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.READ_CALL_LOG )
                != PackageManager.PERMISSION_GRANTED )
            permissions.add( Manifest.permission.READ_CALL_LOG );
        if( ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.READ_CONTACTS )
                != PackageManager.PERMISSION_GRANTED )
            permissions.add( Manifest.permission.READ_CONTACTS );
        if( ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.CALL_PHONE )
                != PackageManager.PERMISSION_GRANTED )
            permissions.add( Manifest.permission.CALL_PHONE );
        if( ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.GET_ACCOUNTS )
                != PackageManager.PERMISSION_GRANTED )
            permissions.add( Manifest.permission.GET_ACCOUNTS );
        if( !permissions.isEmpty()) {
            ActivityCompat.requestPermissions( this,
                    permissions.toArray( new String[ permissions.size()] ),
                    MY_PERMISSIONS_REQUEST_READ );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ:
                // If request is cancelled, the result arrays are empty.
                for( int i = 0; i < permissions.length; i ++ ) {
                    if( grantResults[i] == PackageManager.PERMISSION_GRANTED )
                        Log.v( TAG, permissions[i] + " granted" );
                    else {
                        if( Manifest.permission.READ_CALL_LOG.equals( permissions[i]))
                            PermissionHelper.INSTANCE.complain(getContext());
                        else if( Manifest.permission.GET_ACCOUNTS.equals( permissions[i]))
                            PermissionHelper.INSTANCE.complain(getContext());
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
