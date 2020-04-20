package org.endeavourhealth.common.config;

import java.util.Date;

public class ConfigHistory {
    private Date dtChanged;
    private String changedFrom;
    private String changedTo;

    public ConfigHistory() {
    }

    public Date getDtChanged() {
        return dtChanged;
    }

    public void setDtChanged(Date dtChanged) {
        this.dtChanged = dtChanged;
    }

    public String getChangedFrom() {
        return changedFrom;
    }

    public void setChangedFrom(String changedFrom) {
        this.changedFrom = changedFrom;
    }

    public String getChangedTo() {
        return changedTo;
    }

    public void setChangedTo(String changedTo) {
        this.changedTo = changedTo;
    }
}