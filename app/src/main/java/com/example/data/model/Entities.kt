package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole(val label: String, val roleDescription: String) {
    ADMIN("Director / Guru", "Full administrative & financial access"),
    ACCOUNTS("Accounts Manager", "Fee collections, receipts & ledger"),
    TEACHER("Dance Faculty", "Student batches & recital readiness"),
    STUDENT_PARENT("Student / Parent", "Self-service fee payment & receipts")
}

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val discipline: String, // Bharatanatyam, Carnatic Vocal, Mohiniyattam, Nattuvangam, Mridangam
    val level: String,      // Foundation, Intermediate, Advanced, Arangetram Prep
    val standardMonthlyFee: Double,
    val instructorName: String,
    val batchTiming: String
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val admissionNo: String,
    val fullName: String,
    val guardianName: String,
    val phoneNumber: String,
    val email: String,
    val discipline: String,
    val level: String,
    val batchName: String,
    val monthlyFee: Double,
    val enrollmentDate: String,
    val status: String = "ACTIVE", // ACTIVE, ON_LEAVE, ALUMNI
    val notes: String = ""
)

@Entity(tableName = "fee_records")
data class FeeRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val receiptNo: String,
    val studentId: Long,
    val studentName: String,
    val guardianPhone: String,
    val discipline: String,
    val periodLabel: String,      // e.g. "September 2026", "Quarter 3", "Margazhi Costume"
    val feeType: String,          // TUITION, ADMISSION, COSTUME, ARANGETRAM, EXAM
    val dueAmount: Double,
    val paidAmount: Double,
    val discountAmount: Double = 0.0,
    val dueDate: String,
    val paymentDate: String? = null,
    val paymentMode: String = "UPI", // UPI, CASH, NET_BANKING, CHEQUE
    val transactionRef: String = "",
    val status: String = "PENDING",  // PAID, PENDING, OVERDUE, PARTIAL
    val reminderCount: Int = 0,
    val lastReminderDate: String? = null
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // Studio Rent, Musician Honorarium, Costumes, Lighting & Sound, Salangai Pooja, Admin
    val amount: Double,
    val date: String,
    val paidTo: String,
    val paymentMode: String,
    val notes: String = ""
)

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val eventType: String, // Utsavam, Arangetram, Salangai Pooja, Workshop, Recital
    val date: String,
    val time: String,
    val venue: String,
    val participantFee: Double,
    val registeredCount: Int = 0,
    val status: String = "UPCOMING", // UPCOMING, COMPLETED
    val description: String = ""
)

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val candidateName: String,
    val guardianName: String,
    val phone: String,
    val email: String,
    val interestedDiscipline: String,
    val stage: String = "NEW_INQUIRY", // NEW_INQUIRY, TRIAL_SCHEDULED, TRIAL_COMPLETED, ENROLLED, DROPPED
    val trialDate: String? = null,
    val inquiryDate: String,
    val notes: String = ""
)
