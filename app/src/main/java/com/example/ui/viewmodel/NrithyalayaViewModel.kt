package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.CourseEntity
import com.example.data.model.EventEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.FeeRecordEntity
import com.example.data.model.LeadEntity
import com.example.data.model.StudentEntity
import com.example.data.model.UserRole
import com.example.data.repository.NrithyalayaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NrithyalayaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NrithyalayaRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = NrithyalayaRepository(
            studentDao = database.studentDao(),
            courseDao = database.courseDao(),
            feeDao = database.feeDao(),
            expenseDao = database.expenseDao(),
            eventDao = database.eventDao(),
            leadDao = database.leadDao()
        )
    }

    // Role-based state
    private val _currentRole = MutableStateFlow(UserRole.ADMIN)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Student selected for parent portal view
    private val _selectedParentStudentId = MutableStateFlow<Long?>(1L)
    val selectedParentStudentId: StateFlow<Long?> = _selectedParentStudentId.asStateFlow()

    fun switchRole(role: UserRole) {
        _currentRole.value = role
    }

    fun selectParentStudent(studentId: Long) {
        _selectedParentStudentId.value = studentId
    }

    // Reactive data flows
    val allStudents: StateFlow<List<StudentEntity>> = repository.allStudents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allFeeRecords: StateFlow<List<FeeRecordEntity>> = repository.allFeeRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allExpenses: StateFlow<List<ExpenseEntity>> = repository.allExpenses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allEvents: StateFlow<List<EventEntity>> = repository.allEvents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allLeads: StateFlow<List<LeadEntity>> = repository.allLeads.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allCourses: StateFlow<List<CourseEntity>> = repository.allCourses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalCollections: StateFlow<Double?> = repository.totalCollections.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val totalOutstanding: StateFlow<Double?> = repository.totalOutstanding.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val totalExpenses: StateFlow<Double?> = repository.totalExpenses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    // ACTIONS: Fees & Collections
    fun recordFeePayment(
        feeId: Long,
        amountToPay: Double,
        paymentMode: String,
        transactionRef: String,
        discount: Double = 0.0
    ) {
        viewModelScope.launch {
            val record = allFeeRecords.value.find { it.id == feeId } ?: return@launch
            val newPaidAmount = record.paidAmount + amountToPay
            val newTotalDue = (record.dueAmount - discount).coerceAtLeast(0.0)
            val newStatus = when {
                newPaidAmount >= newTotalDue -> "PAID"
                newPaidAmount > 0 -> "PARTIAL"
                else -> record.status
            }
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val updated = record.copy(
                paidAmount = newPaidAmount,
                discountAmount = record.discountAmount + discount,
                paymentMode = paymentMode,
                transactionRef = transactionRef,
                paymentDate = today,
                status = newStatus
            )
            repository.updateFeeRecord(updated)
        }
    }

    fun markReminderSent(feeId: Long) {
        viewModelScope.launch {
            val record = allFeeRecords.value.find { it.id == feeId } ?: return@launch
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val updated = record.copy(
                reminderCount = record.reminderCount + 1,
                lastReminderDate = today
            )
            repository.updateFeeRecord(updated)
        }
    }

    fun createFeeRecord(
        student: StudentEntity,
        periodLabel: String,
        feeType: String,
        dueAmount: Double,
        dueDate: String
    ) {
        viewModelScope.launch {
            val randomSuffix = (100..999).random()
            val receiptNo = "DEMA-REC-${SimpleDateFormat("yyMM", Locale.getDefault()).format(Date())}-$randomSuffix"
            val newRecord = FeeRecordEntity(
                receiptNo = receiptNo,
                studentId = student.id,
                studentName = student.fullName,
                guardianPhone = student.phoneNumber,
                discipline = student.discipline,
                periodLabel = periodLabel,
                feeType = feeType,
                dueAmount = dueAmount,
                paidAmount = 0.0,
                discountAmount = 0.0,
                dueDate = dueDate,
                status = "PENDING"
            )
            repository.insertFeeRecord(newRecord)
        }
    }

    // ACTIONS: Students
    fun addStudent(
        fullName: String,
        guardianName: String,
        phoneNumber: String,
        email: String,
        discipline: String,
        level: String,
        batchName: String,
        monthlyFee: Double,
        notes: String
    ) {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val randomId = (100..999).random()
            val admissionNo = "SDMA-2026-$randomId"
            val student = StudentEntity(
                admissionNo = admissionNo,
                fullName = fullName,
                guardianName = guardianName,
                phoneNumber = phoneNumber,
                email = email,
                discipline = discipline,
                level = level,
                batchName = batchName,
                monthlyFee = monthlyFee,
                enrollmentDate = today,
                notes = notes
            )
            val studentId = repository.insertStudent(student)

            // Automatically generate first month's tuition fee invoice
            val receiptNo = "DEMA-REC-2026-$randomId"
            val initialFee = FeeRecordEntity(
                receiptNo = receiptNo,
                studentId = studentId,
                studentName = fullName,
                guardianPhone = phoneNumber,
                discipline = discipline,
                periodLabel = "First Month Tuition & Admission",
                feeType = "TUITION",
                dueAmount = monthlyFee,
                paidAmount = 0.0,
                dueDate = today,
                status = "PENDING"
            )
            repository.insertFeeRecord(initialFee)
        }
    }

    fun deleteStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.deleteStudent(student)
        }
    }

    // ACTIONS: Expenses
    fun addExpense(
        title: String,
        category: String,
        amount: Double,
        paidTo: String,
        paymentMode: String,
        notes: String
    ) {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val expense = ExpenseEntity(
                title = title,
                category = category,
                amount = amount,
                date = today,
                paidTo = paidTo,
                paymentMode = paymentMode,
                notes = notes
            )
            repository.insertExpense(expense)
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // ACTIONS: Events
    fun addEvent(
        title: String,
        eventType: String,
        date: String,
        time: String,
        venue: String,
        participantFee: Double,
        description: String
    ) {
        viewModelScope.launch {
            val event = EventEntity(
                title = title,
                eventType = eventType,
                date = date,
                time = time,
                venue = venue,
                participantFee = participantFee,
                description = description
            )
            repository.insertEvent(event)
        }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    // ACTIONS: Leads & Admissions
    fun addLead(
        candidateName: String,
        guardianName: String,
        phone: String,
        email: String,
        interestedDiscipline: String,
        notes: String
    ) {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val lead = LeadEntity(
                candidateName = candidateName,
                guardianName = guardianName,
                phone = phone,
                email = email,
                interestedDiscipline = interestedDiscipline,
                stage = "NEW_INQUIRY",
                inquiryDate = today,
                notes = notes
            )
            repository.insertLead(lead)
        }
    }

    fun updateLeadStage(lead: LeadEntity, newStage: String, trialDate: String? = null) {
        viewModelScope.launch {
            repository.updateLead(lead.copy(stage = newStage, trialDate = trialDate ?: lead.trialDate))
        }
    }

    fun deleteLead(lead: LeadEntity) {
        viewModelScope.launch {
            repository.deleteLead(lead)
        }
    }

    fun convertLeadToStudent(
        lead: LeadEntity,
        batchName: String,
        level: String,
        monthlyFee: Double
    ) {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val randomId = (100..999).random()
            val admissionNo = "SDMA-2026-$randomId"
            val newStudent = StudentEntity(
                admissionNo = admissionNo,
                fullName = lead.candidateName,
                guardianName = lead.guardianName,
                phoneNumber = lead.phone,
                email = lead.email,
                discipline = lead.interestedDiscipline,
                level = level,
                batchName = batchName,
                monthlyFee = monthlyFee,
                enrollmentDate = today,
                notes = "Converted from Inquiry on $today. ${lead.notes}"
            )
            val studentId = repository.insertStudent(newStudent)

            // Mark lead as enrolled
            repository.updateLead(lead.copy(stage = "ENROLLED"))

            // Create initial fee record
            val receiptNo = "DEMA-REC-2026-$randomId"
            val initialFee = FeeRecordEntity(
                receiptNo = receiptNo,
                studentId = studentId,
                studentName = lead.candidateName,
                guardianPhone = lead.phone,
                discipline = lead.interestedDiscipline,
                periodLabel = "Admission & First Month Fee",
                feeType = "TUITION",
                dueAmount = monthlyFee,
                paidAmount = 0.0,
                dueDate = today,
                status = "PENDING"
            )
            repository.insertFeeRecord(initialFee)
        }
    }
}
