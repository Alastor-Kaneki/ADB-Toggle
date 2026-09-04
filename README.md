# ADB Toggle

A minimal Android app that exposes USB debugging and Wireless debugging as one-tap launcher entries and a two-button home-screen widget. It does not use Shizuku and does not request network access.

## What is installed

- **ADB Toggle** — setup, status, manual controls, and settings shortcuts.
- **USB Debugging** — a secondary launcher icon that toggles USB debugging and closes.
- **Wireless Debugging** — a secondary launcher icon that toggles Wireless debugging and closes.
- **ADB Toggle widget** — two independent USB and Wireless buttons.

The two secondary launcher icons use matching USB and Wi-Fi artwork. Their icon and label switch between enabled and disabled variants after the app changes the corresponding setting. Some launchers cache aliases, so visual changes may take a few seconds or require returning to the home screen.

## One-time setup

Android does not allow an ordinary third-party app to write these global settings without an elevated development permission. Install the APK, enable Developer options and USB debugging once, connect with ADB, then run:

```shell
adb shell pm grant com.alastorkaneki.adbtoggle android.permission.WRITE_SECURE_SETTINGS
```

Reopen **ADB Toggle**. After the grant, the app, widget, and secondary icons work without Shizuku and without an active ADB connection.

## Important behavior

- Turning USB debugging off ends USB ADB connectivity. The app keeps its granted permission and can turn it back on locally.
- Wireless debugging may still require Wi-Fi and an existing pairing before a computer can connect.
- OEM Android builds can add restrictions beyond AOSP. The main app includes Developer options and Wireless debugging settings fallbacks.
- USB state is read from Android's sticky USB-state broadcast because the public `ADB_ENABLED` getter is intentionally restricted for third-party apps.

## Build

Requirements:

- JDK 17
- Android SDK 35
- Gradle 8.13

From the project root:

```shell
gradle :app:assembleDebug
```

The debug APK is written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

A GitHub Actions workflow builds and uploads the debug APK for every push and pull request.

## Security

The app has no Internet permission. Its only elevated capability is `WRITE_SECURE_SETTINGS`, granted manually through ADB. Source is intentionally small so the setting writes and launcher behavior can be audited directly.

## License

Apache-2.0
