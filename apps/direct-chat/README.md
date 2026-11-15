# What'z Direct Chat

Opens a WhatsApp or WhatsApp Business chat with any number **without saving it to contacts**.

- Native Kotlin, **no libraries** (only the Kotlin stdlib). Release APK is about **48 KB**.
- **Zero permissions.** No internet permission, no contacts, no call log.
- minSdk 26 (Android 8.0), target/compile SDK 36. UI in Hindi (हिंदी) and English, following the phone language.
- App ID `com.mymedianation.directchat` (permanent). Shared theme and About dialog come from `:core`.

## What it does (MVP)

| Feature | How |
|---|---|
| Number to chat | Official click-to-chat link `https://wa.me/<number>?text=<msg>` |
| Personal vs Business | Same link with `setPackage("com.whatsapp")` or `("com.whatsapp.w4b")`. A button is disabled if that app isn't installed |
| Smart number cleanup | `+91 98765-43210`, `09876543210`, `0091…`, `919876…`: all become `919876543210`. The default country code (+91) can be edited and is remembered |
| Paste | "Paste number" pulls the first phone number out of whatever text is on the clipboard |
| Share to app | Share any SMS, note or page text to this app and the number is picked out |
| Text selection menu | Select a number in any app and choose **this app** in the copy/paste menu |
| Message templates | Quick chips (नमस्ते, ऑर्डर तैयार है, …). The message is only pre-filled; the user taps send in WhatsApp |
| Recent list | Last 50 numbers, **on the phone only** and excluded from backup. Tap to reuse, long-press to delete, menu → clear all |
| Share chat link | Shares the `wa.me` link, which also works when neither app is installed |
| About | Has the "not affiliated with WhatsApp/Meta" disclaimer |

Tests (`./gradlew :apps:direct-chat:test`):
- `PhoneUtilsTest` (16): number cleanup, number extraction, link building
- `MainActivityTest` (20, Robolectric): the real screen on Android 8, 14 and 15, covering buttons, the exact intents sent to
  WhatsApp and Business, errors, share and text-selection, paste, templates, history, About and privacy link,
  rotation, Hindi, dark mode, and edge-to-edge layout
- `PlayScreenshots` (3): renders the Play Store screenshots to `build/play-screenshots/`

## Build

See the root README. Play Store steps: root `PLAY_STORE_GUIDE.md` and `play/direct-chat/`.

## What was verified and what wasn't

Verified in the authoring environment: 39 tests (unit and Robolectric) pass, Android lint has 0 errors, and the release
AAB/APK build signed with the upload key. APK metadata checked: package, labels, no permissions.

**Not verified:** a real phone, and WhatsApp itself receiving the link. The tests check the exact intent
(`https://wa.me/...` with package `com.whatsapp` / `com.whatsapp.w4b`) but not WhatsApp's response. Test on a device before publishing:

- [ ] Both apps installed: each button opens the right app with the number and message pre-filled
- [ ] Only one app installed: the other button is disabled; the keyboard "Go" key opens the installed one
- [ ] Neither installed: the note shows; "Share chat link" works; tapping a disabled button does nothing
- [ ] A number that is not on WhatsApp: WhatsApp shows its own "not on WhatsApp" message
- [ ] Share an SMS to the app, use the text-selection menu, and Paste: the number is filled in
- [ ] Hindi and English phone language; dark mode; rotating the screen keeps the typed text
- [ ] Recent list: tap, long-press delete, clear all

## Next ideas

Home-screen widget or Quick Settings tile, scanning a number from a photo or visiting card (on-device ML Kit adds
about 2–3 MB), saved custom templates, notes and labels on recent numbers, and a pro unlock.
