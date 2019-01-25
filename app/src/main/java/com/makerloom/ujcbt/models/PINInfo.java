package com.makerloom.ujcbt.models;

import java.util.Calendar;

public class PINInfo {
    private String pin;

    private Long validTill;

    private Boolean used;

    private String uid;

    public Boolean hasBeenUsed () {
        if (null != getValidTill()) {
            return true;
        }

        if (null != getUid()) {
            return true;
        }

        if (null != used) {
            return used;
        }

        return false;
    }

    public String getUid () {
        return uid;
    }

    public Long getValidTill () {
        return validTill;
    }

    public String getPin () {
        return pin;
    }

    public void setPin (String pin) {
        this.pin = pin;
    }

    public void setUsed (Boolean used) {
        this.used = used;
    }

    public void setValidTill (Long validTill) {
        this.validTill = validTill;
    }

    public Boolean isExpired () {
        return Calendar.getInstance().getTime().getTime() > getValidTill();
    }

    public Boolean isNotExpired () {
        return Calendar.getInstance().getTime().getTime() <= getValidTill();
    }

    public void setUid (String uid) {
        this.uid = uid;
    }
}
