package com.mymedianation.directchat

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog

/** Behaviour of the real screen: buttons, intents sent to WhatsApp, share/selection, history, Hindi UI. */
@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    private fun install(vararg pkgs: String) {
        val pm = shadowOf(RuntimeEnvironment.getApplication().packageManager)
        pkgs.forEach { pm.installPackage(PackageInfo().apply { packageName = it }) }
    }

    private fun launch(intent: Intent? = null): ActivityController<MainActivity> {
        val i = intent ?: Intent(RuntimeEnvironment.getApplication(), MainActivity::class.java)
        return Robolectric.buildActivity(MainActivity::class.java, i).setup()
    }

    private fun MainActivity.text(id: Int) = findViewById<TextView>(id).text.toString()
    private fun MainActivity.type(id: Int, s: String) = findViewById<EditText>(id).setText(s)
    private fun MainActivity.click(id: Int) = findViewById<View>(id).performClick()
    private fun MainActivity.started(): Intent? = shadowOf(this).nextStartedActivity

    @Test fun bothAppsInstalled_buttonsEnabled_noWarning() {
        install(PhoneUtils.PKG_WHATSAPP, PhoneUtils.PKG_WHATSAPP_BUSINESS)
        val a = launch().get()
        assertTrue(a.findViewById<Button>(R.id.btn_whatsapp).isEnabled)
        assertTrue(a.findViewById<Button>(R.id.btn_business).isEnabled)
        assertEquals(View.GONE, a.findViewById<View>(R.id.none_installed).visibility)
        assertEquals("91", a.text(R.id.country_code))
        assertTrue("cursor starts in the number field", a.findViewById<View>(R.id.number).isFocused)
    }

    @Test fun noAppInstalled_buttonsDisabled_warningShown_shareStillWorks() {
        val a = launch().get()
        assertFalse(a.findViewById<Button>(R.id.btn_whatsapp).isEnabled)
        assertFalse(a.findViewById<Button>(R.id.btn_business).isEnabled)
        assertEquals("WhatsApp not installed", a.text(R.id.btn_whatsapp))
        assertEquals(View.VISIBLE, a.findViewById<View>(R.id.none_installed).visibility)
        a.type(R.id.number, "9485519555")
        a.click(R.id.btn_share_link)
        val chooser = a.started()!!
        assertEquals(Intent.ACTION_CHOOSER, chooser.action)
        val inner = chooser.getParcelableExtra(Intent.EXTRA_INTENT, Intent::class.java)!!
        assertEquals("https://wa.me/919485519555", inner.getStringExtra(Intent.EXTRA_TEXT))
    }

    @Test fun whatsappButton_opensPersonalWhatsAppWithNumberAndMessage() {
        install(PhoneUtils.PKG_WHATSAPP, PhoneUtils.PKG_WHATSAPP_BUSINESS)
        val a = launch().get()
        a.type(R.id.number, "98765 43210")
        a.type(R.id.message, "Namaste ji")
        a.click(R.id.btn_whatsapp)
        val i = a.started()!!
        assertEquals(Intent.ACTION_VIEW, i.action)
        assertEquals("com.whatsapp", i.`package`)
        assertEquals(Uri.parse("https://wa.me/919876543210?text=Namaste%20ji"), i.data)
    }

    @Test fun businessButton_opensWhatsAppBusiness() {
        install(PhoneUtils.PKG_WHATSAPP_BUSINESS)
        val a = launch().get()
        assertFalse(a.findViewById<Button>(R.id.btn_whatsapp).isEnabled)
        a.type(R.id.number, "+44 7911 123456")
        a.click(R.id.btn_business)
        val i = a.started()!!
        assertEquals("com.whatsapp.w4b", i.`package`)
        assertEquals(Uri.parse("https://wa.me/447911123456"), i.data)
    }

    @Test fun emptyAndInvalidNumbers_showErrorAndOpenNothing() {
        install(PhoneUtils.PKG_WHATSAPP)
        val a = launch().get()
        a.click(R.id.btn_whatsapp)
        assertEquals(View.VISIBLE, a.findViewById<View>(R.id.error).visibility)
        assertEquals("Enter a phone number.", a.text(R.id.error))
        assertNull(a.started())
        a.type(R.id.number, "12345")
        a.click(R.id.btn_whatsapp)
        assertTrue(a.text(R.id.error).startsWith("This doesn't look like a valid number"))
        assertNull(a.started())
    }

    @Test fun keyboardGoKey_opensInstalledApp() {
        install(PhoneUtils.PKG_WHATSAPP_BUSINESS)
        val a = launch().get()
        a.type(R.id.number, "9485519555")
        a.findViewById<EditText>(R.id.number).onEditorAction(android.view.inputmethod.EditorInfo.IME_ACTION_GO)
        assertEquals("com.whatsapp.w4b", a.started()!!.`package`)
    }

    @Test fun countryCode_isRemembered() {
        install(PhoneUtils.PKG_WHATSAPP)
        val a = launch().get()
        a.type(R.id.country_code, "971")
        a.type(R.id.number, "501234567")
        a.click(R.id.btn_whatsapp)
        assertEquals(Uri.parse("https://wa.me/971501234567"), a.started()!!.data)
        val b = launch().get()
        assertEquals("971", b.text(R.id.country_code))
    }

    @Test fun sharedText_fillsNumber() {
        val i = Intent(Intent.ACTION_SEND).setClass(RuntimeEnvironment.getApplication(), MainActivity::class.java)
            .setType("text/plain").putExtra(Intent.EXTRA_TEXT, "Order ke liye call karein 94855 19555")
        assertEquals("9485519555", launch(i).get().text(R.id.number))
    }

    @Test fun selectedText_processText_fillsNumber() {
        val i = Intent(Intent.ACTION_PROCESS_TEXT).setClass(RuntimeEnvironment.getApplication(), MainActivity::class.java)
            .setType("text/plain").putExtra(Intent.EXTRA_PROCESS_TEXT, "+91-98765-43210")
        assertEquals("+919876543210", launch(i).get().text(R.id.number))
    }

    @Test fun newShareWhileOpen_replacesNumber() {
        val c = launch()
        c.newIntent(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, "mob 9876543210"))
        assertEquals("9876543210", c.get().text(R.id.number))
    }

    @Test fun pasteButton_readsClipboard() {
        val a = launch().get()
        val cm = a.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("x", "Ramesh: 98123 45678"))
        a.click(R.id.btn_paste)
        assertEquals("9812345678", a.text(R.id.number))
    }

    @Test fun templateChip_fillsMessage() {
        val a = launch().get()
        val row = a.findViewById<LinearLayout>(R.id.templates)
        assertEquals(5, row.childCount)
        row.getChildAt(2).performClick()
        assertEquals("Your order is ready.", a.text(R.id.message))
    }

    @Test fun history_recordsOpenedChat_tapRefills_clearRemoves() {
        install(PhoneUtils.PKG_WHATSAPP, PhoneUtils.PKG_WHATSAPP_BUSINESS)
        val a = launch().get()
        a.type(R.id.number, "9485519555")
        a.click(R.id.btn_business)
        val list = a.findViewById<LinearLayout>(R.id.history_list)
        assertEquals(1, list.childCount)
        assertEquals(View.VISIBLE, a.findViewById<View>(R.id.history_title).visibility)
        assertEquals("+91 94855 19555", list.getChildAt(0).findViewById<TextView>(R.id.h_number).text.toString())
        assertEquals("Business · just now", list.getChildAt(0).findViewById<TextView>(R.id.h_meta).text.toString())
        a.click(R.id.btn_clear)
        assertEquals("", a.text(R.id.number))
        list.getChildAt(0).performClick()
        assertEquals("+919485519555", a.text(R.id.number))
        // Same number again moves to top instead of duplicating.
        a.click(R.id.btn_whatsapp)
        assertEquals(1, list.childCount)
        // Menu > Clear recent > confirm.
        shadowOf(a).clickMenuItem(R.id.menu_clear_history)
        (ShadowDialog.getLatestDialog() as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).performClick()
        shadowOf(Looper.getMainLooper()).idle() // dialog button clicks are posted to the main thread
        assertEquals(0, list.childCount)
        assertEquals(View.GONE, a.findViewById<View>(R.id.history_title).visibility)
    }

    @Test fun aboutDialog_hasDisclaimer_andPrivacyLinkOpensPolicy() {
        val a = launch().get()
        shadowOf(a).clickMenuItem(R.id.menu_about)
        val d = ShadowDialog.getLatestDialog() as AlertDialog
        val msg = shadowOf(d).message.toString()
        assertTrue(msg.contains("Version 1.0.0"))
        assertTrue(msg.contains("not affiliated with, endorsed or sponsored by WhatsApp or Meta"))
        assertTrue(msg.contains("My Media Nation"))
        d.getButton(AlertDialog.BUTTON_NEUTRAL).performClick()
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(Uri.parse("https://drsgurgaon.github.io/PLAYSTORE/privacy/direct-chat.html"), a.started()!!.data)
    }

    @Test fun rotation_keepsTypedText() {
        val c = launch()
        c.get().type(R.id.number, "9485519555")
        c.get().type(R.id.message, "hello")
        c.recreate()
        assertEquals("9485519555", c.get().text(R.id.number))
        assertEquals("hello", c.get().text(R.id.message))
    }

    @Test @Config(qualifiers = "hi")
    fun hindiPhone_showsHindiUi() {
        install(PhoneUtils.PKG_WHATSAPP)
        val a = launch().get()
        assertEquals("WhatsApp में खोलें", a.text(R.id.btn_whatsapp))
        assertEquals("नंबर पेस्ट करें", a.text(R.id.btn_paste))
        val row = a.findViewById<LinearLayout>(R.id.templates)
        assertEquals("नमस्ते!", (row.getChildAt(0) as Button).text.toString())
        assertNotNull(a.title)
    }

    @Test @Config(qualifiers = "night")
    fun darkMode_launchesFine() {
        install(PhoneUtils.PKG_WHATSAPP)
        assertTrue(launch().get().findViewById<Button>(R.id.btn_whatsapp).isEnabled)
    }

    private fun assertContentBelowToolbar() {
        val a = launch().get()
        val tb = IntArray(2).also { a.findViewById<View>(R.id.toolbar).getLocationInWindow(it) }
        val sc = IntArray(2).also { a.findViewById<View>(R.id.scroll).getLocationInWindow(it) }
        val toolbarBottom = tb[1] + a.findViewById<View>(R.id.toolbar).height
        assertTrue("toolbar has height", a.findViewById<View>(R.id.toolbar).height > 0)
        assertTrue("content starts below toolbar ($sc vs $toolbarBottom)", sc[1] >= toolbarBottom)
        assertEquals("What'z Direct Chat", a.actionBar!!.title.toString())
    }

    @Test @Config(sdk = [35]) fun android15_edgeToEdge_contentNotUnderToolbar() = assertContentBelowToolbar()

    @Test @Config(sdk = [34]) fun android14_contentNotUnderToolbar() = assertContentBelowToolbar()

    @Test @Config(sdk = [26]) fun android8_minSdk_launchesAndOpensChat() {
        install(PhoneUtils.PKG_WHATSAPP)
        val a = launch().get()
        a.type(R.id.number, "9485519555")
        a.click(R.id.btn_whatsapp)
        assertEquals(Uri.parse("https://wa.me/919485519555"), a.started()!!.data)
    }
}
