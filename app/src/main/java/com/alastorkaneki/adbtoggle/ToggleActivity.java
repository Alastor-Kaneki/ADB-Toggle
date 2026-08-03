package com.alastorkaneki.adbtoggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class ToggleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String className = getComponentName().getClassName();
        if (!DebuggingController.hasPermission(this)) {
            Toast.makeText(this, R.string.permission_needed_toast, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
            return;
        }

        DebuggingController.ToggleResult result;
        if (className.contains("Usb")) {
            result = DebuggingController.toggleUsb(this);
        } else if (className.contains("Wifi")) {
            result = DebuggingController.toggleWifi(this);
        } else {
            Toast.makeText(this, R.string.unknown_toggle, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
        finish();
    }
}
