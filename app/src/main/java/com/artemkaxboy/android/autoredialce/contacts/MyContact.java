package com.artemkaxboy.android.autoredialce.contacts;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class MyContact {

  private long id = 0;
  private String displayName = null;
  private ArrayList<MyPhone> phones;

  public MyContact() {
    phones = new ArrayList<>();
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  @SuppressWarnings("unused")
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName != null ? displayName : "";
  }

  @SuppressWarnings("unused")
  public void setPhones(ArrayList<MyPhone> phones) {
    this.phones = phones;
  }

  public ArrayList<MyPhone> getPhones() {
    return phones;
  }

  /**
   * Loads contact from contacts list by given number
   *
   * @param context app context
   * @param number  number to find the contact
   * @return true if contact found, false - otherwise
   */
  public boolean loadByNumber(Context context, String number) {
    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
    Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
    if (cursor != null) {
      if (cursor.moveToFirst()) {
        id = cursor.getLong(cursor.getColumnIndex(PhoneLookup._ID));
        Cursor c = context.getContentResolver()
            .query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + id, null, null);
        if (c != null) {
          if (c.moveToFirst()) {
            displayName = c.getString(c.getColumnIndex(Phone.DISPLAY_NAME));
            do {
              number = c.getString(c.getColumnIndex(Phone.NUMBER));
              if (contains(number)) {
                continue;
              }
              MyPhone phone = new MyPhone();
              phone.setId(c.getLong(c.getColumnIndex(Phone._ID)));
              phone.setNumber(number);
              phone.setType(c.getInt(c.getColumnIndex(Phone.TYPE)));
              phone.setLabel(Phone.getTypeLabel(context.getResources(), phone.getType(),
                  c.getString(c.getColumnIndex(Phone.LABEL))).toString());
              phones.add(phone);
            } while (c.moveToNext());
          }
          c.close();
        }
      } else {
        return false;
      }
      cursor.close();
    }
    return true;
  }

  public boolean contains(String number) {
    for (MyPhone phone : phones) {
      if (MyPhone.compare(phone.getNumber(), number)) {
        return true;
      }
    }
    return false;
  }


  public static String getNameByNumber(Context context, String number) {
    String ret = context.getString(android.R.string.unknownName);
    if (isPermissionNeeded(context)) {
      return ret;
    }
    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
    Cursor cursor = context.getContentResolver()
        .query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
    try {
      assert cursor != null;
      cursor.moveToFirst();
      ret = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
      cursor.close();
    } catch (Exception ignored) {
    }
    return ret;
  }

  public static String getNameByNumber(Context context, String number, String defValue) {
    String name = getNameByNumber(context, number);
    return ((name.equals(context.getString(android.R.string.unknownName))) ? defValue : name);
  }

  public static String getTypeByNumber(Context context, String number) {
    String ret = "";
    if (isPermissionNeeded(context)) {
      return ret;
    }
    Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
    Cursor cursor = context.getContentResolver()
        .query(uri, new String[]{PhoneLookup.TYPE, PhoneLookup.LABEL}, null, null, null);
    try {
      assert cursor != null;
      cursor.moveToFirst();
      ret = Phone.getTypeLabel(context.getResources(),
          cursor.getInt(cursor.getColumnIndex(PhoneLookup.TYPE)),
          cursor.getString(cursor.getColumnIndex(PhoneLookup.LABEL))).toString();
      cursor.close();
    } catch (Exception ignored) {
    }
    return ret;
  }

  private static boolean isPermissionNeeded(Context context) {
    return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED;
  }
}
