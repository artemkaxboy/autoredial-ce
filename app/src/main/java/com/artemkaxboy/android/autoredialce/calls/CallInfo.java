package com.artemkaxboy.android.autoredialce.calls;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CallInfo {

  private String number;
  private long date;
  private long duration = -1;
  private long id;
  private int type;

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  @SuppressWarnings("WeakerAccess")
  public long getDate() {
    return date;
  }

  @SuppressWarnings("WeakerAccess")
  public void setDate(long date) {
    this.date = date;
  }

  public long getDuration() {
    return duration;
  }

  @SuppressWarnings("WeakerAccess")
  public void setDuration(long duration) {
    this.duration = duration;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  @SuppressWarnings("unused")
  public String getDateString() {
    return new SimpleDateFormat("y/m/d H:m:s", Locale.getDefault())
        .format(new Date(getDate()));
  }
}