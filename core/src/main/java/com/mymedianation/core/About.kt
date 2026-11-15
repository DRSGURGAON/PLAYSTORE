package com.mymedianation.core

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri

/** The same About dialog for every app: name, version, app-specific text, company and privacy link. */
object About {
    fun show(activity: Activity, appName: CharSequence, body: CharSequence, privacyUrl: String) {
        val version = activity.packageManager.getPackageInfo(activity.packageName, 0).versionName
        val text = activity.getString(R.string.mmn_about_footer, version, body)
        AlertDialog.Builder(activity)
            .setTitle(appName)
            .setMessage(text)
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(R.string.mmn_privacy_policy) { _, _ ->
                try {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl)))
                } catch (_: ActivityNotFoundException) { /* no browser: nothing to do */ }
            }
            .show()
    }
}
