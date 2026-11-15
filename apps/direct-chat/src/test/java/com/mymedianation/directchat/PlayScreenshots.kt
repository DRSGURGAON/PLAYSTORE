package com.mymedianation.directchat

import android.content.Intent
import android.content.pm.PackageInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import android.widget.EditText
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * Renders the real screen (actual layouts, theme, strings) to PNGs for the Play listing.
 * 1080x1920 phone screenshots land in apps/direct-chat/build/play-screenshots/.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PlayScreenshots {

    private fun shoot(name: String, message: String, number: String) {
        val pm = shadowOf(RuntimeEnvironment.getApplication().packageManager)
        listOf(PhoneUtils.PKG_WHATSAPP, PhoneUtils.PKG_WHATSAPP_BUSINESS)
            .forEach { pm.installPackage(PackageInfo().apply { packageName = it }) }
        // Sample recent list (numbers are illustrative).
        val store = HistoryStore(RuntimeEnvironment.getApplication())
        store.add("919812345678", "W"); store.add("918076543210", "B"); store.add("919485519555", "W")

        val a = Robolectric.buildActivity(MainActivity::class.java,
            Intent(RuntimeEnvironment.getApplication(), MainActivity::class.java)).setup().get()
        a.findViewById<EditText>(R.id.number).setText(number)
        a.findViewById<EditText>(R.id.message).setText(message)
        shadowOf(Looper.getMainLooper()).idle()
        a.findViewById<android.view.View>(R.id.scroll).scrollTo(0, 0)

        val root = a.window.decorView
        val bmp = Bitmap.createBitmap(root.width, root.height, Bitmap.Config.ARGB_8888)
        root.draw(Canvas(bmp))
        val out = File("build/play-screenshots").apply { mkdirs() }
        File(out, "$name.png").outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test @Config(qualifiers = "en-rIN-w360dp-h640dp-xxhdpi")
    fun english() = shoot("1-en", "Your order is ready.", "98765 43210")

    @Test @Config(qualifiers = "hi-w360dp-h640dp-xxhdpi")
    fun hindi() = shoot("2-hi", "नमस्ते, आपका ऑर्डर तैयार है।", "98765 43210")

    @Test @Config(qualifiers = "en-rIN-w360dp-h640dp-night-xxhdpi")
    fun dark() = shoot("3-en-dark", "Please share your location.", "+91 98123 45678")
}
