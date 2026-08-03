package com.alastorkaneki.adbtoggle;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public abstract class ToggleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!DebuggingController.hasPermission(this)) {
            Toast.makeText(this, R.string.permission_needed_toast, Toast.LENGTH_LONG).show();
            finishAndRemoveTask();
            return;
        }

        DebuggingController.ToggleResult result = performToggle();
        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
        finishAndRemoveTask();
    }

    protected abstract DebuggingController.ToggleResult performToggle();
}
