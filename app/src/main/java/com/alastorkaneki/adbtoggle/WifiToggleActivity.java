package com.alastorkaneki.adbtoggle;

public class WifiToggleActivity extends ToggleActivity {
    @Override
    protected DebuggingController.ToggleResult performToggle() {
        return DebuggingController.toggleWifi(this);
    }
}
