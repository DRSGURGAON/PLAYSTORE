/** Values shared by every My Media Nation app. Change here, not in each app. */
object Company {
    /** Every app's Play Store ID is "$ID_PREFIX.<app>". Never change an ID after publishing. */
    const val ID_PREFIX = "com.mymedianation"

    const val COMPILE_SDK = 36
    const val TARGET_SDK = 36 // Google Play requires the latest year's API level for new apps/updates.
    const val MIN_SDK = 26    // Android 8.0+

    /**
     * Upload-key signing (Play App Signing keeps the real app-signing key at Google).
     * One upload key is shared by all apps. Values come from environment variables
     * (CI secrets) and are never committed.
     */
    val uploadKeystoreFile: String? get() = System.getenv("MMN_UPLOAD_KEYSTORE_FILE")
    val uploadKeystorePassword: String? get() = System.getenv("MMN_UPLOAD_KEYSTORE_PASSWORD")
    val uploadKeyAlias: String? get() = System.getenv("MMN_UPLOAD_KEY_ALIAS")
    val uploadKeyPassword: String? get() = System.getenv("MMN_UPLOAD_KEY_PASSWORD")
}
