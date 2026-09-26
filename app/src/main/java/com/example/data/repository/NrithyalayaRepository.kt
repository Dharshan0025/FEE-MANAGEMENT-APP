package com.example.data.repository

import com.example.data.db.CourseDao
import com.example.data.db.EventDao
import com.example.data.db.ExpenseDao
import com.example.data.db.FeeDao
import com.example.data.db.LeadDao
import com.example.data.db.StudentDao
import com.example.data.model.CourseEntity
import com.example.data.model.EventEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.FeeRecordEntity
import com.example.data.model.LeadEntity
import com.example.data.model.StudentEntity
import kotlinx.coroutines.flow.Flow

class NrithyalayaRepository(
    private val studentDao: StudentDao,
    private val courseDao: CourseDao,
    private val feeDao: FeeDao,
    private val expenseDao: ExpenseDao,
    private val eventDao: EventDao,
    private val leadDao: LeadDao
) {
    // Students
    val allStudents: Flow<List<StudentEntity>> = studentDao.getAllStudents()
    val activeStudentCount: Flow<Int> = studentDao.getActiveStudentCount()
    suspend fun getStudentById(id: Long) = studentDao.getStudentById(id)
    suspend fun insertStudent(student: StudentEntity): Long = studentDao.insertStudent(student)
    suspend fun updateStudent(student: StudentEntity) = studentDao.updateStudent(student)
    suspend fun deleteStudent(student: StudentEntity) = studentDao.deleteStudent(student)

    // Fees
    val allFeeRecords: Flow<List<FeeRecordEntity>> = feeDao.getAllFeeRecords()
    val totalCollections: Flow<Double?> = feeDao.getTotalCollections()
    val totalOutstanding: Flow<Double?> = feeDao.getTotalOutstanding()
    fun getFeesForStudent(studentId: Long): Flow<List<FeeRecordEntity>> = feeDao.getFeesForStudent(studentId)
    suspend fun insertFeeRecord(feeRecord: FeeRecordEntity): Long = feeDao.insertFeeRecord(feeRecord)
    suspend fun updateFeeRecord(feeRecord: FeeRecordEntity) = feeDao.updateFeeRecord(feeRecord)
    suspend fun deleteFeeRecord(feeRecord: FeeRecordEntity) = feeDao.deleteFeeRecord(feeRecord)

    // Expenses
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    val totalExpenses: Flow<Double?> = expenseDao.getTotalExpenses()
    suspend fun insertExpense(expense: ExpenseEntity): Long = expenseDao.insertExpense(expense)
    suspend fun deleteExpense(expense: ExpenseEntity) = expenseDao.deleteExpense(expense)

    // Events
    val allEvents: Flow<List<EventEntity>> = eventDao.getAllEvents()
    suspend fun insertEvent(event: EventEntity): Long = eventDao.insertEvent(event)
    suspend fun updateEvent(event: EventEntity) = eventDao.updateEvent(event)
    suspend fun deleteEvent(event: EventEntity) = eventDao.deleteEvent(event)

    // Leads
    val allLeads: Flow<List<LeadEntity>> = leadDao.getAllLeads()
    suspend fun insertLead(lead: LeadEntity): Long = leadDao.insertLead(lead)
    suspend fun updateLead(lead: LeadEntity) = leadDao.updateLead(lead)
    suspend fun deleteLead(lead: LeadEntity) = leadDao.deleteLead(lead)

    // Courses
    val allCourses: Flow<List<CourseEntity>> = courseDao.getAllCourses()
}
