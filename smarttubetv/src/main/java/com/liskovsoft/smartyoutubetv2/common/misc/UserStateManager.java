package com.liskovsoft.smartyoutubetv2.common.misc;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.UUID;

public class UserStateManager {
    private static final String PREF_NAME = "user_state";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_CREDIT_STATUS = "credit_status";
    private static final String STATUS_EXHAUSTED = "exhausted";

    private static UserStateManager sInstance;
    private final SharedPreferences mPrefs;

    private UserStateManager(Context context) {
        mPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (getUserId() == null) {
            // Generate and store a new user ID if one doesn't exist
            mPrefs.edit().putString(KEY_USER_ID, UUID.randomUUID().toString()).apply();
        }
    }

    public static synchronized UserStateManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new UserStateManager(context.getApplicationContext());
        }
        return sInstance;
    }

    public String getUserId() {
        return mPrefs.getString(KEY_USER_ID, null);
    }

    public boolean areCreditsExhausted() {
        return STATUS_EXHAUSTED.equals(mPrefs.getString(KEY_CREDIT_STATUS, ""));
    }

    public void setCreditsStatus(String status) {
        mPrefs.edit().putString(KEY_CREDIT_STATUS, status).apply();
    }
}