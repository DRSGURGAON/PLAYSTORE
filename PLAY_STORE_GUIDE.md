# Play Store par app daalne ki guide (personal developer account)

This guide works for every app in this repo. The examples are for **What'z Direct Chat**
(`apps/direct-chat`, ID `com.mymedianation.directchat`).

## 0. One-time setup (sirf ek baar)

1. **Upload key**: `mymedianation-upload.jks` aur `upload-key-details.txt` ko 2 jagah safe rakho
   (Google Drive + pen drive). Same key **saare apps** ke liye hai. Kabhi GitHub par commit mat karna.
2. **GitHub secrets** (repo > Settings > Secrets and variables > Actions > New repository secret):
   - `MMN_UPLOAD_KEYSTORE_BASE64`: `mymedianation-upload.jks.base64.txt` ka poora content
   - `MMN_UPLOAD_KEYSTORE_PASSWORD`, `MMN_UPLOAD_KEY_PASSWORD`: details file wala password
   - `MMN_UPLOAD_KEY_ALIAS`: `upload`
   Iske baad har push par **Actions > Android apps > play-bundles** mein signed `.aab` milega.
3. **Privacy policy page** (support email: blogspriyankakumari@gmail.com):
   Settings > Pages > Source: *Deploy from a branch* > `main` / `/docs`.
   GitHub Free plan par Pages ke liye repo **public** hona chahiye (code mein koi secret nahi hai).

## 1. Play Console mein app banao
Play Console > **Create app**
- App name: `What'z Direct Chat` · Default language: English (India) – en-IN
- App or game: **App** · Free or paid: **Free** (free app baad mein paid nahi ho sakta)
- Declarations tick karo > Create

## 2. App content (left menu > Policy > App content)
Saare jawab `play/direct-chat/DATA_SAFETY_AND_FORMS.md` mein hain: privacy policy, ads,
app access, content rating, target audience, data safety, advertising ID, government apps,
financial features, health.

## 3. Store listing (Grow users > Store presence > Main store listing)
- Title / short / full description: `play/direct-chat/listing/en-IN/`
- Hindi translation add karo (Manage translations > Hindi – hi-IN): `listing/hi-IN/`
- App icon: `graphics/icon-512.png` · Feature graphic: `graphics/feature-graphic-1024x500.png`
- **Phone screenshots (min 2, max 8)**: apne phone par app chala kar lo. Do screens lo:
  number + message bhara hua, aur "Recent" list. Bina tasveer ke submit nahi hoga.
- Category: Communication · Contact email daalo

## 4. Closed testing: personal account ke liye zaroori
New personal accounts must run a **closed test with at least 12 testers, opted in for 14 days
in a row**, before they can apply for production.
1. Test and release > Testing > **Closed testing** > Create track (ya "Alpha")
2. Testers: Google Group ya email list. **Kam se kam 12 log** (12 se zyada rakho, koi beech mein chhod de to bhi chale)
3. **Create release** > upload `direct-chat-release.aab` > Release notes: `listing/*/release_notes.txt`
   - Pehli baar **Play App Signing** accept karo (Google app-signing key rakhta hai; hum sirf upload key se sign karte hain)
4. Review ke liye bhejo. Approve hone ke baad testers ko opt-in link bhejo. Unhe link kholna,
   "Become a tester" dabana, aur app install karna hai. **14 din tak opt-in rehna zaroori hai.**
5. 14 din baad: Dashboard > **Apply for production**. Isme testing ke baare mein kuch sawaal aate hain,
   jaise testers ne kya feedback diya aur aapne kya badla.

## 5. Production
Approval ke baad: Production > Create release > wahi (ya naya) `.aab` > Countries: India (ya sab) >
Rollout. Review aam taur par kuch ghante se kuch din leta hai.

## Har update par
1. App ke `build.gradle.kts` mein `versionCode` +1 aur `versionName` badlo (jaise 1.0.1)
2. Push > Actions se `.aab` lo > Play Console mein naya release

## Naam ke baare mein
"What'z" sunne mein "WhatsApp" jaisa lagta hai. Google Play ki impersonation aur
intellectual-property policy ki wajah se reject ya strike ka risk hai. Rejection aaye to
naam badlo:
- `apps/direct-chat/src/main/res/values/strings.xml` > `app_name`
- `play/direct-chat/listing/*/title.txt`
- graphics dobara banao: `node tools/render-play-graphics.js play/direct-chat "Naya Naam" "बिना नंबर सेव किए चैट करें" "Chat without saving the number"`
- privacy page ka title
App ID (`com.mymedianation.directchat`) kabhi nahi badalti, isliye naam badalna safe hai.
