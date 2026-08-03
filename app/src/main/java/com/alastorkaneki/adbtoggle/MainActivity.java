package com.alastorkaneki.adbtoggle;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String GRANT_COMMAND = "adb shell pm grant com.alastorkaneki.adbtoggle android.permission.WRITE_SECURE_SETTINGS";
    private TextView permissionStatus;
    private TextView usbStatus;
    private TextView wifiStatus;
    private ImageView usbIcon;
    private ImageView wifiIcon;
    private Button usbButton;
    private Button wifiButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        setContentView(R.layout.activity_main);

        permissionStatus = findViewById(R.id.permission_status);
        usbStatus = findViewById(R.id.usb_status);
        wifiStatus = findViewById(R.id.wifi_status);
        usbIcon = findViewById(R.id.usb_icon);
        wifiIcon = findViewById(R.id.wifi_icon);
        usbButton = findViewById(R.id.usb_toggle_button);
        wifiButton = findViewById(R.id.wifi_toggle_button);

        usbButton.setOnClickListener(view -> showResult(DebuggingController.toggleUsb(this)));
        wifiButton.setOnClickListener(view -> showResult(DebuggingController.toggleWifi(this)));
        findViewById(R.id.copy_command_button).setOnClickListener(view -> copyGrantCommand());
        findViewById(R.id.developer_settings_button).setOnClickListener(view -> openDeveloperSettings());
        findViewById(R.id.wireless_settings_button).setOnClickListener(view -> openWirelessSettings());
        findViewById(R.id.refresh_button).setOnClickListener(view -> refresh());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        boolean permission = DebuggingController.hasPermission(this);
        boolean usb = DebuggingController.isUsbEnabled(this);
        boolean wifi = DebuggingController.isWifiEnabled(this);

        permissionStatus.setText(permission ? R.string.permission_granted : R.string.permission_missing);
        permissionStatus.setTextColor(getColor(permission ? R.color.enabled : R.color.disabled));

        usbStatus.setText(usb ? R.string.enabled : R.string.disabled);
        wifiStatus.setText(wifi ? R.string.enabled : R.string.disabled);
        usbStatus.setTextColor(getColor(usb ? R.color.enabled : R.color.text_secondary));
        wifiStatus.setTextColor(getColor(wifi ? R.color.enabled : R.color.text_secondary));
        usbIcon.setImageResource(usb ? R.drawable.ic_usb_on : R.drawable.ic_usb_off);
        wifiIcon.setImageResource(wifi ? R.drawable.ic_wifi_on : R.drawable.ic_wifi_off);
        usbButton.setText(usb ? R.string.turn_off : R.string.turn_on);
        wifiButton.setText(wifi ? R.string.turn_off : R.string.turn_on);
        usbButton.setEnabled(permission);
        wifiButton.setEnabled(permission);

        DebuggingController.refreshAll(this);
    }

    private void showResult(DebuggingController.ToggleResult result) {
        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
        refresh();
    }

    private void copyGrantCommand() {
        ClipboardManager clipboard = getSystemService(ClipboardManager.class);
        clipboard.setPrimaryClip(ClipData.newPlainText("ADB Toggle grant command", GRANT_COMMAND));
        Toast.makeText(this, R.string.command_copied, Toast.LENGTH_SHORT).show();
    }

    private void openDeveloperSettings() {
        try {
            startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
        } catch (ActivityNotFoundException exception) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private void openWirelessSettings() {
        try {
            startActivity(new Intent("android.settings.WIRELESS_DEBUGGING_SETTINGS"));
        } catch (ActivityNotFoundException exception) {
            openDeveloperSettings();
        }
    }
}
