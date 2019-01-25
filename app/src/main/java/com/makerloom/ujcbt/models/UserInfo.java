package com.makerloom.ujcbt.models;

import java.util.Calendar;
import java.util.Date;

public class UserInfo {
    private Date unlockedTill;

    public UserInfo() {}
    public UserInfo(Date unlockedTill) {
        setUnlockedTill(unlockedTill);
    }

    public boolean isLocked () {
        return Calendar.getInstance().before(unlockedTill) || null == unlockedTill;
    }

    public Date getUnlockedTill() {
        return unlockedTill;
    }

    public void setUnlockedTill(Date unlockedTill) {
        this.unlockedTill = unlockedTill;
    }
}
