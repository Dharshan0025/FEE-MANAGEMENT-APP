package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.data.model.UserRole
import com.example.ui.components.AcademyTopBar
import com.example.ui.components.RoleSelectionDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EventsScreen
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.FeeManagementScreen
import com.example.ui.screens.LeadsScreen
import com.example.ui.screens.ParentPortalScreen
import com.example.ui.screens.StudentsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NrithyalayaViewModel

enum class MainTab(val title: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    FEES("Fees & UPI", Icons.Default.Payments),
    STUDENTS("Students", Icons.Default.Group),
    LEADS("Admissions", Icons.Default.HowToReg),
    OPERATIONS("Operations", Icons.Default.AccountBalanceWallet),
    PARENT_PORTAL("Student Portal", Icons.Default.School)
}

class MainActivity : ComponentActivity() {
    private val viewModel: NrithyalayaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: NrithyalayaViewModel) {
    val currentRole by viewModel.currentRole.collectAsState()
    var selectedTab by remember { mutableStateOf(MainTab.DASHBOARD) }
    var showRoleDialog by remember { mutableStateOf(false) }

    // If role switched to parent, set tab to PARENT_PORTAL
    val onRoleSelected: (UserRole) -> Unit = { role ->
        viewModel.switchRole(role)
        if (role == UserRole.STUDENT_PARENT) {
            selectedTab = MainTab.PARENT_PORTAL
        } else if (selectedTab == MainTab.PARENT_PORTAL) {
            selectedTab = MainTab.DASHBOARD
        }
    }

    // Android back handling
    if (selectedTab != MainTab.DASHBOARD) {
        BackHandler {
            selectedTab = MainTab.DASHBOARD
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AcademyTopBar(
                currentRole = currentRole,
                onSwitchRoleClick = { showRoleDialog = true }
            )
        },
        bottomBar = {
            NavigationBar {
                val availableTabs = if (currentRole == UserRole.STUDENT_PARENT) {
                    listOf(MainTab.PARENT_PORTAL, MainTab.FEES, MainTab.DASHBOARD)
                } else {
                    listOf(
                        MainTab.DASHBOARD,
                        MainTab.FEES,
                        MainTab.STUDENTS,
                        MainTab.LEADS,
                        MainTab.OPERATIONS,
                        MainTab.PARENT_PORTAL
                    )
                }

                availableTabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                        label = { Text(text = tab.title) },
                        modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                MainTab.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToFees = { selectedTab = MainTab.FEES },
                    onNavigateToStudents = { selectedTab = MainTab.STUDENTS },
                    onNavigateToLeads = { selectedTab = MainTab.LEADS },
                    onNavigateToExpenses = { selectedTab = MainTab.OPERATIONS },
                    onNavigateToEvents = { selectedTab = MainTab.OPERATIONS }
                )
                MainTab.FEES -> FeeManagementScreen(
                    viewModel = viewModel
                )
                MainTab.STUDENTS -> StudentsScreen(
                    viewModel = viewModel
                )
                MainTab.LEADS -> LeadsScreen(
                    viewModel = viewModel
                )
                MainTab.OPERATIONS -> OperationsScreen(
                    viewModel = viewModel
                )
                MainTab.PARENT_PORTAL -> ParentPortalScreen(
                    viewModel = viewModel
                )
            }
        }
    }

    if (showRoleDialog) {
        RoleSelectionDialog(
            currentRole = currentRole,
            onRoleSelected = onRoleSelected,
            onDismiss = { showRoleDialog = false }
        )
    }
}

/**
 * Operations screen housing Academy Expenses & Academy Events tabs
 */
@Composable
fun OperationsScreen(viewModel: NrithyalayaViewModel) {
    var selectedSubSection by remember { mutableStateOf(0) } // 0: Expenses, 1: Events

    androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxSize()) {
        androidx.compose.material3.TabRow(selectedTabIndex = selectedSubSection) {
            androidx.compose.material3.Tab(
                selected = selectedSubSection == 0,
                onClick = { selectedSubSection = 0 },
                text = { Text("Academy Expenses") }
            )
            androidx.compose.material3.Tab(
                selected = selectedSubSection == 1,
                onClick = { selectedSubSection = 1 },
                text = { Text("Events & Recitals") }
            )
        }

        if (selectedSubSection == 0) {
            ExpensesScreen(viewModel = viewModel)
        } else {
            EventsScreen(viewModel = viewModel)
        }
    }
}
