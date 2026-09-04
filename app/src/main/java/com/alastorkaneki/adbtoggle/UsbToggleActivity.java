package com.alastorkaneki.adbtoggle;

public class UsbToggleActivity extends ToggleActivity {
    @Override
    protected DebuggingController.ToggleResult performToggle() {
        return DebuggingController.toggleUsb(this);
    }
}
