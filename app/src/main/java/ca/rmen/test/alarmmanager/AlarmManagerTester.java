package ca.rmen.test.alarmmanager;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlarmManagerTester extends PreferenceActivity {

    private static final String TAG = AlarmManagerTester.class.getSimpleName();
    private static final String ACTION_ALARM = "carmen_test_alarm";
    private static final int REQUEST_CODE_ALARM = 1;
    private PendingIntent mAlarmIntent;
    private AlarmManager mAlarmManager;
    private final SharedPreferences.OnSharedPreferenceChangeListener mSharedPrefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.v(TAG, "onSharedPreferenceChanged: " + key);

            if(key.equals("PREF_ENABLE")) {
                if (sharedPreferences.getBoolean(key, false)) {
                    String clockName = sharedPreferences.getString("PREF_CLOCK", "RTC");
                    int delay = Integer.valueOf(sharedPreferences.getString("PREF_DELAY_S","60"));
                    String methodName = sharedPreferences.getString("PREF_SET_METHOD", "set");
                    int clock = AlarmManager.RTC;
                    long alarmTime = System.currentTimeMillis() + (delay*1000);
                    if("RTC".equals(clockName)) {
                        clock = AlarmManager.RTC;
                        alarmTime = System.currentTimeMillis() + (delay*1000);
                    } else if("RTC_WAKEUP".equals(clockName)) {
                        clock = AlarmManager.RTC_WAKEUP;
                        alarmTime = System.currentTimeMillis() + (delay*1000);
                    } else if("ELAPSED_REALTIME".equals(clockName)) {
                        clock = AlarmManager.ELAPSED_REALTIME;
                        alarmTime =  SystemClock.elapsedRealtime() + (delay*1000);
                    } else if("ELAPSED_REALTIME_WAKEUP".equals(clockName)) {
                        clock = AlarmManager.ELAPSED_REALTIME_WAKEUP;
                        alarmTime =  SystemClock.elapsedRealtime() + (delay*1000);
                    }

                    if("set".equals(methodName)) {
                        Log.v(TAG, "Set alarm in " + delay + " seconds");
                        mAlarmManager.set(clock, alarmTime, mAlarmIntent);
                    } else {
                        Log.v(TAG, "SetExact alarm in " + delay + " seconds");
                        mAlarmManager.setExact(clock, alarmTime, mAlarmIntent);
                    }

                } else {
                    Log.v(TAG, "Cancel alarm");
                    mAlarmManager.cancel(mAlarmIntent);
                }
            }
            setPreferenceScreen(null);
            addPreferencesFromResource(R.xml.debug_alarm_manager_tester);
            updateSummaries();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccountUtils.createAccount(this);
        addPreferencesFromResource(R.xml.debug_alarm_manager_tester);
        mAlarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ACTION_ALARM).setPackage(getPackageName());
        mAlarmIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_ALARM, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mSharedPrefsListener);
        updateSummaries();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mSharedPrefsListener);
    }

    private void updateSummaries() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Preference preference = findPreference("PREF_DELAY_S");
        preference.setSummary(sharedPrefs.getString("PREF_DELAY_S", "60") + " seconds");
    }
}
