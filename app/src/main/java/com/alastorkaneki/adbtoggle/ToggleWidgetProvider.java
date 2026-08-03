package com.alastorkaneki.adbtoggle;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ToggleWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateWidgets(context, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (DebuggingController.ACTION_TOGGLE_USB.equals(action)) {
            handleToggle(context, true);
        } else if (DebuggingController.ACTION_TOGGLE_WIFI.equals(action)) {
            handleToggle(context, false);
        }
    }

    private void handleToggle(Context context, boolean usb) {
        if (!DebuggingController.hasPermission(context)) {
            Intent setup = new Intent(context, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(setup);
            return;
        }
        DebuggingController.ToggleResult result = usb
                ? DebuggingController.toggleUsb(context)
                : DebuggingController.toggleWifi(context);
        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show();
        DebuggingController.refreshAll(context);
    }

    public static void updateWidgets(Context context, int[] appWidgetIds) {
        if (appWidgetIds == null || appWidgetIds.length == 0) {
            return;
        }
        boolean usb = DebuggingController.isUsbEnabled(context);
        boolean wifi = DebuggingController.isWifiEnabled(context);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_toggle);
            views.setTextViewText(R.id.widget_usb_status, usb
                    ? context.getString(R.string.on)
                    : context.getString(R.string.off));
            views.setTextViewText(R.id.widget_wifi_status, wifi
                    ? context.getString(R.string.on)
                    : context.getString(R.string.off));
            views.setImageViewResource(R.id.widget_usb_icon, usb
                    ? R.drawable.ic_usb_on
                    : R.drawable.ic_usb_off);
            views.setImageViewResource(R.id.widget_wifi_icon, wifi
                    ? R.drawable.ic_wifi_on
                    : R.drawable.ic_wifi_off);
            views.setOnClickPendingIntent(R.id.widget_usb_button,
                    toggleIntent(context, DebuggingController.ACTION_TOGGLE_USB, 100));
            views.setOnClickPendingIntent(R.id.widget_wifi_button,
                    toggleIntent(context, DebuggingController.ACTION_TOGGLE_WIFI, 200));
            manager.updateAppWidget(appWidgetId, views);
        }
    }

    private static PendingIntent toggleIntent(Context context, String action, int requestCode) {
        Intent intent = new Intent(context, ToggleWidgetProvider.class).setAction(action);
        return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
}
