package com.alastorkaneki.adbtoggle;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;

public final class DebuggingController {
    public static final String KEY_USB = "adb_enabled";
    public static final String KEY_WIFI = "adb_wifi_enabled";
    public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";
    public static final String EXTRA_USB_ADB = "adb";
    public static final String ACTION_TOGGLE_USB = "com.alastorkaneki.adbtoggle.TOGGLE_USB";
    public static final String ACTION_TOGGLE_WIFI = "com.alastorkaneki.adbtoggle.TOGGLE_WIFI";
    private static final String PREFS = "debugging_state";
    private static final String PREF_USB = "usb_expected";
    private static final String PREF_WIFI = "wifi_expected";

    private DebuggingController() {
    }

    public static boolean hasPermission(Context context) {
        return context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isUsbEnabled(Context context) {
        Intent state = context.registerReceiver(null, new IntentFilter(ACTION_USB_STATE));
        if (state != null && state.hasExtra(EXTRA_USB_ADB)) {
            boolean enabled = state.getBooleanExtra(EXTRA_USB_ADB, false);
            preferences(context).edit().putBoolean(PREF_USB, enabled).apply();
            return enabled;
        }
        return preferences(context).getBoolean(PREF_USB, false);
    }

    public static boolean isWifiEnabled(Context context) {
        boolean enabled = Settings.Global.getInt(
                context.getContentResolver(),
                KEY_WIFI,
                preferences(context).getBoolean(PREF_WIFI, false) ? 1 : 0
        ) == 1;
        preferences(context).edit().putBoolean(PREF_WIFI, enabled).apply();
        return enabled;
    }

    public static ToggleResult toggleUsb(Context context) {
        return setUsbEnabled(context, !isUsbEnabled(context));
    }

    public static ToggleResult toggleWifi(Context context) {
        return setWifiEnabled(context, !isWifiEnabled(context));
    }

    public static ToggleResult setUsbEnabled(Context context, boolean enabled) {
        return write(context, KEY_USB, PREF_USB, enabled, "USB debugging");
    }

    public static ToggleResult setWifiEnabled(Context context, boolean enabled) {
        return write(context, KEY_WIFI, PREF_WIFI, enabled, "Wireless debugging");
    }

    private static ToggleResult write(
            Context context,
            String key,
            String preferenceKey,
            boolean enabled,
            String label
    ) {
        if (!hasPermission(context)) {
            return new ToggleResult(false, enabled, label + " needs setup permission");
        }
        try {
            boolean accepted = Settings.Global.putInt(
                    context.getContentResolver(),
                    key,
                    enabled ? 1 : 0
            );
            if (!accepted) {
                return new ToggleResult(false, !enabled, label + " was rejected by Android");
            }
            preferences(context).edit().putBoolean(preferenceKey, enabled).apply();
            refreshAll(context);
            return new ToggleResult(true, enabled, label + (enabled ? " enabled" : " disabled"));
        } catch (SecurityException exception) {
            return new ToggleResult(false, !enabled, label + " permission was denied");
        } catch (RuntimeException exception) {
            return new ToggleResult(false, !enabled, label + " could not be changed");
        }
    }

    public static void refreshAll(Context context) {
        int[] widgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
                new ComponentName(context, ToggleWidgetProvider.class)
        );
        ToggleWidgetProvider.updateWidgets(context, widgetIds);
    }

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static final class ToggleResult {
        public final boolean success;
        public final boolean enabled;
        public final String message;

        public ToggleResult(boolean success, boolean enabled, String message) {
            this.success = success;
            this.enabled = enabled;
            this.message = message;
        }
    }
}
