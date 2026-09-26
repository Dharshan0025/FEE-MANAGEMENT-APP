package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CourseEntity
import com.example.data.model.EventEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.FeeRecordEntity
import com.example.data.model.LeadEntity
import com.example.data.model.StudentEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StudentEntity::class,
        CourseEntity::class,
        FeeRecordEntity::class,
        ExpenseEntity::class,
        EventEntity::class,
        LeadEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun courseDao(): CourseDao
    abstract fun feeDao(): FeeDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun eventDao(): EventDao
    abstract fun leadDao(): LeadDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dema_nrithyalaya_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialAcademyData(database)
                    }
                }
            }
        }

        suspend fun populateInitialAcademyData(database: AppDatabase) {
            // 1. Courses
            val courses = listOf(
                CourseEntity(
                    id = 1,
                    title = "Bharatanatyam - Foundation (Adavus)",
                    discipline = "Bharatanatyam",
                    level = "Foundation",
                    standardMonthlyFee = 2500.0,
                    instructorName = "Guru Malavika S.",
                    batchTiming = "Tue & Thu, 5:00 PM - 6:30 PM"
                ),
                CourseEntity(
                    id = 2,
                    title = "Bharatanatyam - Varnam & Abhinaya",
                    discipline = "Bharatanatyam",
                    level = "Intermediate",
                    standardMonthlyFee = 3500.0,
                    instructorName = "Guru Malavika S.",
                    batchTiming = "Mon, Wed & Fri, 6:00 PM - 7:30 PM"
                ),
                CourseEntity(
                    id = 3,
                    title = "Arangetram Masterclass & Margam",
                    discipline = "Bharatanatyam",
                    level = "Advanced / Arangetram",
                    standardMonthlyFee = 5500.0,
                    instructorName = "Kalaimamani Sri Devanathan",
                    batchTiming = "Sat & Sun, 8:00 AM - 10:30 AM"
                ),
                CourseEntity(
                    id = 4,
                    title = "Carnatic Vocal for Dancers",
                    discipline = "Carnatic Vocal",
                    level = "All Levels",
                    standardMonthlyFee = 2800.0,
                    instructorName = "Vidwan R. Narayanan",
                    batchTiming = "Saturday, 11:00 AM - 1:00 PM"
                ),
                CourseEntity(
                    id = 5,
                    title = "Nattuvangam & Tala Shastra",
                    discipline = "Nattuvangam",
                    level = "Senior",
                    standardMonthlyFee = 4000.0,
                    instructorName = "Kalaimamani Sri Devanathan",
                    batchTiming = "Sunday, 4:00 PM - 6:00 PM"
                )
            )
            database.courseDao().insertCourses(courses)

            // 2. Students
            val students = listOf(
                StudentEntity(
                    id = 1,
                    admissionNo = "SDMA-2025-014",
                    fullName = "Ananya Sundaram",
                    guardianName = "R. Sundaram (Father)",
                    phoneNumber = "9840123456",
                    email = "sundaram.family@gmail.com",
                    discipline = "Bharatanatyam",
                    level = "Advanced / Arangetram",
                    batchName = "Weekend Arangetram Masterclass",
                    monthlyFee = 5500.0,
                    enrollmentDate = "2025-01-10",
                    status = "ACTIVE",
                    notes = "Preparing for Arangetram debut in Dec 2026. Focus on Simhendramadhyamam Varnam."
                ),
                StudentEntity(
                    id = 2,
                    admissionNo = "SDMA-2025-032",
                    fullName = "Meera Jayashankar",
                    guardianName = "Priya Jayashankar (Mother)",
                    phoneNumber = "9841098765",
                    email = "priya.j@outlook.com",
                    discipline = "Bharatanatyam",
                    level = "Intermediate",
                    batchName = "Mon-Wed-Fri Senior Batch",
                    monthlyFee = 3500.0,
                    enrollmentDate = "2025-04-12",
                    status = "ACTIVE",
                    notes = "Excellent footwork, learning Thillana in Dhanashree."
                ),
                StudentEntity(
                    id = 3,
                    admissionNo = "SDMA-2026-003",
                    fullName = "Diya Radhakrishnan",
                    guardianName = "Dr. K. Radhakrishnan",
                    phoneNumber = "9790112233",
                    email = "dr.radha@yahoo.co.in",
                    discipline = "Bharatanatyam",
                    level = "Foundation",
                    batchName = "Tue-Thu Junior Adavus",
                    monthlyFee = 2500.0,
                    enrollmentDate = "2026-02-01",
                    status = "ACTIVE",
                    notes = "Salangai Pooja completed in Aug 2026."
                ),
                StudentEntity(
                    id = 4,
                    admissionNo = "SDMA-2025-088",
                    fullName = "Kavya Subramanian",
                    guardianName = "Lakshmi S. (Mother)",
                    phoneNumber = "9444055667",
                    email = "lakshmi.sub@gmail.com",
                    discipline = "Carnatic Vocal",
                    level = "All Levels",
                    batchName = "Saturday Vocal Batch",
                    monthlyFee = 2800.0,
                    enrollmentDate = "2025-07-15",
                    status = "ACTIVE",
                    notes = "Special interest in padams and javalis accompaniment."
                ),
                StudentEntity(
                    id = 5,
                    admissionNo = "SDMA-2026-045",
                    fullName = "Rohan Venkatesh",
                    guardianName = "Venkatesh Babu",
                    phoneNumber = "9884234567",
                    email = "vbabu77@gmail.com",
                    discipline = "Nattuvangam",
                    level = "Senior",
                    batchName = "Sunday Tala Masterclass",
                    monthlyFee = 4000.0,
                    enrollmentDate = "2026-03-01",
                    status = "ACTIVE",
                    notes = "Practicing talam variations for Tisra & Khanda Jathis."
                )
            )
            database.studentDao().insertStudents(students)

            // 3. Fee Records & Receipts
            val fees = listOf(
                FeeRecordEntity(
                    id = 1,
                    receiptNo = "DEMA-REC-2026-104",
                    studentId = 1,
                    studentName = "Ananya Sundaram",
                    guardianPhone = "9840123456",
                    discipline = "Bharatanatyam",
                    periodLabel = "September 2026 Tuition",
                    feeType = "TUITION",
                    dueAmount = 5500.0,
                    paidAmount = 5500.0,
                    discountAmount = 0.0,
                    dueDate = "2026-09-05",
                    paymentDate = "2026-09-03",
                    paymentMode = "UPI",
                    transactionRef = "UPI/260903847291/GPay",
                    status = "PAID",
                    reminderCount = 0
                ),
                FeeRecordEntity(
                    id = 2,
                    receiptNo = "DEMA-REC-2026-105",
                    studentId = 1,
                    studentName = "Ananya Sundaram",
                    guardianPhone = "9840123456",
                    discipline = "Bharatanatyam",
                    periodLabel = "Margazhi Festival Costume Advance",
                    feeType = "COSTUME",
                    dueAmount = 4500.0,
                    paidAmount = 4500.0,
                    discountAmount = 0.0,
                    dueDate = "2026-09-10",
                    paymentDate = "2026-09-08",
                    paymentMode = "UPI",
                    transactionRef = "UPI/260908123990/PhonePe",
                    status = "PAID",
                    reminderCount = 0
                ),
                FeeRecordEntity(
                    id = 3,
                    receiptNo = "DEMA-REC-2026-106",
                    studentId = 2,
                    studentName = "Meera Jayashankar",
                    guardianPhone = "9841098765",
                    discipline = "Bharatanatyam",
                    periodLabel = "September 2026 Tuition",
                    feeType = "TUITION",
                    dueAmount = 3500.0,
                    paidAmount = 0.0,
                    discountAmount = 0.0,
                    dueDate = "2026-09-10",
                    paymentDate = null,
                    paymentMode = "UPI",
                    transactionRef = "",
                    status = "OVERDUE",
                    reminderCount = 1,
                    lastReminderDate = "2026-09-15"
                ),
                FeeRecordEntity(
                    id = 4,
                    receiptNo = "DEMA-REC-2026-107",
                    studentId = 3,
                    studentName = "Diya Radhakrishnan",
                    guardianPhone = "9790112233",
                    discipline = "Bharatanatyam",
                    periodLabel = "September 2026 Tuition",
                    feeType = "TUITION",
                    dueAmount = 2500.0,
                    paidAmount = 1500.0,
                    discountAmount = 0.0,
                    dueDate = "2026-09-12",
                    paymentDate = "2026-09-11",
                    paymentMode = "CASH",
                    transactionRef = "CASH-REC#092",
                    status = "PARTIAL",
                    reminderCount = 1,
                    lastReminderDate = "2026-09-18"
                ),
                FeeRecordEntity(
                    id = 5,
                    receiptNo = "DEMA-REC-2026-108",
                    studentId = 4,
                    studentName = "Kavya Subramanian",
                    guardianPhone = "9444055667",
                    discipline = "Carnatic Vocal",
                    periodLabel = "September 2026 Tuition",
                    feeType = "TUITION",
                    dueAmount = 2800.0,
                    paidAmount = 0.0,
                    discountAmount = 0.0,
                    dueDate = "2026-09-28",
                    paymentDate = null,
                    paymentMode = "UPI",
                    transactionRef = "",
                    status = "PENDING",
                    reminderCount = 0
                ),
                FeeRecordEntity(
                    id = 6,
                    receiptNo = "DEMA-REC-2026-109",
                    studentId = 5,
                    studentName = "Rohan Venkatesh",
                    guardianPhone = "9884234567",
                    discipline = "Nattuvangam",
                    periodLabel = "September 2026 Tuition",
                    feeType = "TUITION",
                    dueAmount = 4000.0,
                    paidAmount = 4000.0,
                    discountAmount = 0.0,
                    dueDate = "2026-09-05",
                    paymentDate = "2026-09-04",
                    paymentMode = "NET_BANKING",
                    transactionRef = "HDFC-N94829104",
                    status = "PAID",
                    reminderCount = 0
                )
            )
            database.feeDao().insertFeeRecords(fees)

            // 4. Academy Expenses
            val expenses = listOf(
                ExpenseEntity(
                    id = 1,
                    title = "Dance Studio Monthly Lease",
                    category = "Studio Rent",
                    amount = 22000.0,
                    date = "2026-09-01",
                    paidTo = "Kalashetra Trust Heritage Hall",
                    paymentMode = "NET_BANKING",
                    notes = "Monthly lease for main dance studio & practice hall."
                ),
                ExpenseEntity(
                    id = 2,
                    title = "Mridangam Artist Accompaniment",
                    category = "Musician Honorarium",
                    amount = 6500.0,
                    date = "2026-09-07",
                    paidTo = "Vidwan S. Parthasarathy",
                    paymentMode = "UPI",
                    notes = "Weekend Arangetram rehearsal accompaniment."
                ),
                ExpenseEntity(
                    id = 3,
                    title = "Brass Salangai & Bell Straps Procurement",
                    category = "Costumes",
                    amount = 4200.0,
                    date = "2026-09-12",
                    paidTo = "Sri Krishna Classical Crafts Mylapore",
                    paymentMode = "UPI",
                    notes = "10 sets of 4-line brass salangai for junior batch."
                ),
                ExpenseEntity(
                    id = 4,
                    title = "Studio Mirror & Teak Barre Maintenance",
                    category = "Admin",
                    amount = 2800.0,
                    date = "2026-09-16",
                    paidTo = "Senthil Carpenters",
                    paymentMode = "CASH",
                    notes = "Polishing dance studio barre and fixing mirror frame."
                )
            )
            database.expenseDao().insertExpenses(expenses)

            // 5. Events & Arangetrams
            val events = listOf(
                EventEntity(
                    id = 1,
                    title = "Margazhi Natya Utsavam 2026",
                    eventType = "Festival",
                    date = "2026-12-22",
                    time = "6:00 PM - 9:30 PM",
                    venue = "Vani Mahal Main Auditorium, Chennai",
                    participantFee = 3500.0,
                    registeredCount = 28,
                    status = "UPCOMING",
                    description = "Annual academy showcase featuring ensemble varnams and solo thillanas."
                ),
                EventEntity(
                    id = 2,
                    title = "Arangetram Solo Recital - Ananya",
                    eventType = "Arangetram",
                    date = "2026-11-15",
                    time = "5:30 PM - 8:30 PM",
                    venue = "Bharatiya Vidya Bhavan, Mylapore",
                    participantFee = 0.0,
                    registeredCount = 1,
                    status = "UPCOMING",
                    description = "Maiden full Margam solo dance presentation accompanied by live orchestra."
                ),
                EventEntity(
                    id = 3,
                    title = "Navarasa & Abhinaya Master Workshop",
                    eventType = "Workshop",
                    date = "2026-10-18",
                    time = "10:00 AM - 4:00 PM",
                    venue = "Sri DeMA Nrithyalaya Studio Hall",
                    participantFee = 1500.0,
                    registeredCount = 18,
                    status = "UPCOMING",
                    description = "Intensive workshop on Natyashastra rasas and facial expression techniques."
                )
            )
            database.eventDao().insertEvents(events)

            // 6. Admissions & Inquiries
            val leads = listOf(
                LeadEntity(
                    id = 1,
                    candidateName = "Samyuktha Suresh",
                    guardianName = "Suresh Kumar",
                    phone = "9840998877",
                    email = "suresh.k@gmail.com",
                    interestedDiscipline = "Bharatanatyam (Foundation)",
                    stage = "TRIAL_SCHEDULED",
                    trialDate = "2026-09-27",
                    inquiryDate = "2026-09-20",
                    notes = "7 years old. Looking for weekend foundation batch."
                ),
                LeadEntity(
                    id = 2,
                    candidateName = "Tara Ananth",
                    guardianName = "Vidya Ananth",
                    phone = "9790554433",
                    email = "vidya.ananth@hotmail.com",
                    interestedDiscipline = "Carnatic Vocal",
                    stage = "NEW_INQUIRY",
                    trialDate = null,
                    inquiryDate = "2026-09-23",
                    notes = "Interested in evening Carnatic music sessions."
                ),
                LeadEntity(
                    id = 3,
                    candidateName = "Swathi Balaji",
                    guardianName = "Balaji Narayanan",
                    phone = "9940123987",
                    email = "balaji.n@gmail.com",
                    interestedDiscipline = "Bharatanatyam (Intermediate)",
                    stage = "TRIAL_COMPLETED",
                    trialDate = "2026-09-22",
                    inquiryDate = "2026-09-14",
                    notes = "Learned adavus for 3 years elsewhere. Recommended for Varnam batch."
                )
            )
            database.leadDao().insertLeads(leads)
        }
    }
}
