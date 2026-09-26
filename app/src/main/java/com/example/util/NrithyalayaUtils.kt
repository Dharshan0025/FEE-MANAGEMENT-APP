package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.FeeRecordEntity
import com.example.data.model.StudentEntity
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.util.Locale

object NrithyalayaUtils {

    const val ACADEMY_NAME = "Sri DeMA Nrithyalaya Arts"
    const val ACADEMY_TAGLINE = "Classical Dance & Fine Arts Academy"
    const val ACADEMY_PHONE = "+91 98401 23456"
    const val ACADEMY_EMAIL = "admissions@demanrithyalaya.org"
    const val ACADEMY_ADDRESS = "No. 42, Natya Marg, Mylapore, Chennai - 600004"
    const val ACADEMY_UPI_ID = "demanrithyalaya@okhdfcbank"
    const val ACADEMY_PAYEE_NAME = "Sri DeMA Nrithyalaya Arts"

    fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        return format.format(amount).replace("INR", "₹").trim()
    }

    /**
     * Constructs a standard Indian UPI payment URI
     * e.g., upi://pay?pa=demanrithyalaya@okhdfcbank&pn=Sri%20DeMA%20Nrithyalaya%20Arts&am=3500.00&cu=INR&tn=Fee-DEMA-REC-106
     */
    fun createUpiUri(amount: Double, receiptNo: String, studentName: String): Uri {
        val note = "Fee payment for $studentName ($receiptNo)"
        val encodedNote = URLEncoder.encode(note, StandardCharsets.UTF_8.toString())
        val encodedPayee = URLEncoder.encode(ACADEMY_PAYEE_NAME, StandardCharsets.UTF_8.toString())
        val formattedAmount = String.format(Locale.US, "%.2f", amount)
        val uriString = "upi://pay?pa=$ACADEMY_UPI_ID&pn=$encodedPayee&am=$formattedAmount&cu=INR&tn=$encodedNote"
        return Uri.parse(uriString)
    }

    /**
     * Launches UPI Intent on device
     */
    fun launchUpiPayment(context: Context, amount: Double, receiptNo: String, studentName: String) {
        val upiUri = createUpiUri(amount, receiptNo, studentName)
        val intent = Intent(Intent.ACTION_VIEW, upiUri)
        val chooser = Intent.createChooser(intent, "Pay Fee via UPI (GPay, PhonePe, Paytm, etc.)")
        try {
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "No UPI payment apps found. You can pay directly to UPI ID: $ACADEMY_UPI_ID", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Prepares professional WhatsApp reminder text
     */
    fun buildWhatsAppReminderText(fee: FeeRecordEntity): String {
        val pending = fee.dueAmount - fee.paidAmount
        return """
            🙏 *Namaskaram from Sri DeMA Nrithyalaya Arts* 🙏
            
            Dear Parent / Student,
            
            This is an automated fee reminder for:
            👤 *Student:* ${fee.studentName}
            🎭 *Discipline:* ${fee.discipline}
            📅 *Period / Purpose:* ${fee.periodLabel}
            💰 *Pending Amount:* ${formatCurrency(pending)}
            ⏳ *Due Date:* ${fee.dueDate}
            
            *Payment Options:*
            • Pay via UPI to: *${ACADEMY_UPI_ID}*
            • Or pay via the Sri DeMA Nrithyalaya mobile portal
            
            If already paid, please share the transaction reference.
            
            Warm regards,
            *Sri DeMA Nrithyalaya Arts*
            Natya Marg, Mylapore, Chennai
            📞 $ACADEMY_PHONE
        """.trimIndent()
    }

    /**
     * Launches WhatsApp with pre-filled message
     */
    fun launchWhatsApp(context: Context, rawPhone: String, message: String) {
        try {
            var cleanPhone = rawPhone.replace("[^0-9]".toRegex(), "")
            if (!cleanPhone.startsWith("91") && cleanPhone.length == 10) {
                cleanPhone = "91$cleanPhone"
            }
            val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
            val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open WhatsApp: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Launches SMS client
     */
    fun launchSms(context: Context, rawPhone: String, message: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$rawPhone")
                putExtra("sms_body", message)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not launch SMS client: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Phone dialer
     */
    fun launchCall(context: Context, rawPhone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$rawPhone"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open dialer: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Formats official Digital Receipt
     */
    fun formatDigitalReceipt(fee: FeeRecordEntity, student: StudentEntity? = null): String {
        return """
            ==============================================
                    SRI DEMA NRITHYALAYA ARTS
                Classical Dance & Fine Arts Academy
              No. 42, Natya Marg, Mylapore, Chennai - 600004
                   Contact: $ACADEMY_PHONE
            ==============================================
                       OFFICIAL DIGITAL RECEIPT
            Receipt No : ${fee.receiptNo}
            Date       : ${fee.paymentDate ?: "Pending"}
            Status     : ${fee.status}
            ----------------------------------------------
            STUDENT DETAILS:
            Student Name : ${fee.studentName}
            Admission No : ${student?.admissionNo ?: "N/A"}
            Discipline   : ${fee.discipline}
            Batch/Level  : ${student?.level ?: "Regular"}
            ----------------------------------------------
            PAYMENT BREAKDOWN:
            Particulars  : ${fee.periodLabel} (${fee.feeType})
            Gross Due    : ${formatCurrency(fee.dueAmount)}
            Discount     : ${formatCurrency(fee.discountAmount)}
            ----------------------------------------------
            NET PAID     : ${formatCurrency(fee.paidAmount)}
            Balance Due  : ${formatCurrency((fee.dueAmount - fee.paidAmount).coerceAtLeast(0.0))}
            Mode of Pay  : ${fee.paymentMode}
            Ref / UTR    : ${fee.transactionRef.ifEmpty { "N/A" }}
            ==============================================
            * Computer Generated Official Electronic Receipt *
            Authorized Signature: Sri DeMA Nrithyalaya Arts
            ==============================================
        """.trimIndent()
    }

    /**
     * Share receipt text via Android Sharesheet
     */
    fun shareReceipt(context: Context, receiptText: String, receiptNo: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Fee Receipt - $receiptNo - Sri DeMA Nrithyalaya Arts")
            putExtra(Intent.EXTRA_TEXT, receiptText)
        }
        context.startActivity(Intent.createChooser(intent, "Share Digital Receipt"))
    }
}
