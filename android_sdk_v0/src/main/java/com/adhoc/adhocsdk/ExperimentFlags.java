package com.adhoc.adhocsdk;

import org.json.JSONObject;

/**
 * Access to experiment flags.
 */
public class ExperimentFlags {
    private JSONObject mFlags;

    public JSONObject getRawFlags() {
        return mFlags;
    }

    public ExperimentFlags(JSONObject flags) {
        mFlags = flags;
    }

    public boolean has(String key) {
      return mFlags.has(key);
    }

    public boolean getBooleanFlag(String key) {
        return mFlags.optBoolean(key, false);
    }

    public int getIntegerFlag(String key) {
        return mFlags.optInt(key, 0);
    }

    public double getNumberFlag(String key) {
        return mFlags.optDouble(key, 0);
    }

    public String getStringFlag(String key) {
        return mFlags.optString(key, "");
    }

    public String getFlagState() {
        return flagState;
    }

    public void setFlagState(String flagState) {
        this.flagState = flagState;
    }

    private String flagState;
    enum ExperimentFlagsState{
        EXPERIMENT_OK,
        EXPERIMENT_NULL
    }

    @Override
    public String toString() {
        return mFlags == null ? "" : mFlags.toString();
    }
}
