## Telegram messenger for Android

[Telegram](https://telegram.org) is a messaging app with a focus on speed and security. It’s superfast, simple and free.
This repo contains the official source code for [Telegram App for Android](https://play.google.com/store/apps/details?id=org.telegram.messenger).

## Creating your Telegram Application

We welcome all developers to use our API and source code to create applications on our platform.
There are several things we require from **all developers** for the moment.

1. [**Obtain your own api_id**](https://core.telegram.org/api/obtaining_api_id) for your application.
2. Please **do not** use the name Telegram for your app — or make sure your users understand that it is unofficial.
3. Kindly **do not** use our standard logo (white paper plane in a blue circle) as your app's logo.
3. Please study our [**security guidelines**](https://core.telegram.org/mtproto/security_guidelines) and take good care of your users' data and privacy.
4. Please remember to publish **your** code too in order to comply with the licences.

### API, Protocol documentation

Telegram API manuals: https://core.telegram.org/api

MTproto protocol manuals: https://core.telegram.org/mtproto

### Compilation Guide

**Note**: In order to support [reproducible builds](https://core.telegram.org/reproducible-builds), this repo contains dummy release.keystore,  google-services.json and filled variables inside BuildVars.java. Before publishing your own APKs please make sure to replace all these files with your own.

You will require Android Studio 2025.1.4, Android NDK 27.2.12479018 and Android SDK 36.

1. Clone the Telegram source code with its submodules:
   ```bash
   git clone --recursive --shallow-submodules https://github.com/DrKLO/Telegram.git Telegram
   ```
   In case you forgot the `--recursive` flag, change to the `Telegram` directory and run:
   ```bash
   git submodule init && git submodule update --init --recursive --depth=1
   ```
2. Copy your release.keystore into TMessagesProj/config
3. Fill out RELEASE_KEY_PASSWORD, RELEASE_KEY_ALIAS, RELEASE_STORE_PASSWORD in gradle.properties to access your  release.keystore
4.  Go to https://console.firebase.google.com/, create two android apps with application IDs org.telegram.messenger and org.telegram.messenger.beta, turn on firebase messaging and download google-services.json, which should be copied to the same folder as TMessagesProj.
5. Open the project in the Studio (note that it should be opened, NOT imported).
6. Fill out values in TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java – there’s a link for each of the variables showing where and which data to obtain.
7. You are ready to compile Telegram.

### Building the regular Android client

The default Gradle project includes the regular client (`TMessagesProj_App`) and the shared Telegram library. Huawei, HockeyApp, standalone, and test app modules are excluded from normal project configuration. To opt into those additional modules, pass `-PincludeAdditionalApps=true`.

Install Android SDK Platform 36, Build Tools 36.0.0, NDK 27.2.12479018, and CMake 3.22.1, then point Gradle at your local SDK (for example, set `ANDROID_HOME` or add the SDK's path to an untracked `local.properties`). Build the regular debug APK with:

```bash
./gradlew :TMessagesProj_App:assembleAfatDebug
```

The APK is written to `TMessagesProj_App/build/outputs/apk/afat/debug/app.apk`. This is the standard Google Play-services client; it does not include Huawei services.

GitHub Actions builds this same variant on pushes and pull requests to `main` or `master`, and can also be started manually from the Actions tab. Download the `nextogram-regular-client-afat-debug` artifact from a successful workflow run to get the APK. The workflow checks out submodules and installs the required Android SDK, NDK, and CMake versions.

### Localization

We moved all translations to https://translations.telegram.org/en/android/. Please use it.
