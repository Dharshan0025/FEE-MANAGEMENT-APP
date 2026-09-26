package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.NrithyalayaUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DeMA Nrithyalaya", appName)
    }

    @Test
    fun `upi payment uri generation is valid`() {
        val uri = NrithyalayaUtils.createUpiUri(
            amount = 3500.0,
            receiptNo = "DEMA-REC-2026-106",
            studentName = "Meera Jayashankar"
        )
        val uriString = uri.toString()
        assertTrue(uriString.startsWith("upi://pay"))
        assertTrue(uriString.contains("pa=${NrithyalayaUtils.ACADEMY_UPI_ID}"))
        assertTrue(uriString.contains("3500.00"))
    }

    @Test
    fun `currency formatting contains rupee symbol`() {
        val formatted = NrithyalayaUtils.formatCurrency(2500.0)
        assertTrue(formatted.contains("₹"))
    }
}
