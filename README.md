# My Media Nation: Android apps

All of My Media Nation's Android apps live in this one repository, with shared code and one build.

| App | Folder | Play ID | Status |
|---|---|---|---|
| What'z Direct Chat | `apps/direct-chat` | `com.mymedianation.directchat` | v1.0.0, ready for closed testing |

## Layout

```
apps/<app>/          one Android app per folder (its own name, icon, version, code)
core/                shared by every app: brand colours, base theme (Theme.MyMediaNation, no window
                     action bar: use an in-layout Toolbar + insets, see MainActivity), About dialog
buildSrc/Company.kt  shared settings: ID prefix, SDK levels, upload-key signing
play/<app>/          Play Store listing text (en-IN, hi-IN), graphics, Play Console form answers
docs/                GitHub Pages site: privacy policies (docs/privacy/<app>.html)
tools/               helper scripts (Play graphics renderer)
.github/workflows/   CI: test + lint + signed .aab for every app
PLAY_STORE_GUIDE.md  step-by-step Play Console guide (works for every app)
```

## Build

Needs JDK 17+ and the Android SDK (platform 36): set `ANDROID_HOME` or put `sdk.dir=…` in `local.properties`.

```bash
./gradlew test lint                      # all apps
./gradlew :apps:direct-chat:bundleRelease  # Play bundle → apps/direct-chat/build/outputs/bundle/release/
./gradlew :apps:direct-chat:assembleRelease  # APK to install on a phone for testing
```

Release signing reads `MMN_UPLOAD_KEYSTORE_FILE`, `MMN_UPLOAD_KEYSTORE_PASSWORD`,
`MMN_UPLOAD_KEY_ALIAS` and `MMN_UPLOAD_KEY_PASSWORD`. Without them the release is debug-signed.
That works for testing, but Play rejects it. One upload key is used for all apps, and Play App Signing holds the real keys.

## Adding a new app

1. Copy `apps/direct-chat` to `apps/<new-app>` and delete what you don't need.
2. In its `build.gradle.kts`, set `namespace` and `applicationId` to `"${Company.ID_PREFIX}.<newapp>"`.
   **Choose carefully: the ID can never change after the first Play upload.** Reset `versionCode = 1`.
3. Move the Kotlin package to `com/mymedianation/<newapp>/` and set `app_name`.
4. Add `include(":apps:<new-app>")` in `settings.gradle.kts`.
5. Add `play/<new-app>/` (listing text, graphics, form answers) and `docs/privacy/<new-app>.html`,
   and link it from `docs/index.html`.
6. Code that more than one app needs goes in `core/`.

## Verification status

What'z Direct Chat: 39 tests pass (16 logic tests and 23 Robolectric tests on Android 8, 14 and 15),
Android lint has 0 errors, and the release AAB/APK is signed with the upload key. **Not yet run on a real phone.**
See the device checklist in `apps/direct-chat/README.md` before sending it to testers.
