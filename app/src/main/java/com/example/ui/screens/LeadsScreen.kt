package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeadEntity
import com.example.ui.components.AddLeadDialog
import com.example.ui.components.ConvertLeadDialog
import com.example.ui.viewmodel.NrithyalayaViewModel
import com.example.util.NrithyalayaUtils

@Composable
fun LeadsScreen(
    viewModel: NrithyalayaViewModel
) {
    val context = LocalContext.current
    val leads by viewModel.allLeads.collectAsState()

    var selectedStageFilter by remember { mutableStateOf("ALL") }
    var showAddLeadDialog by remember { mutableStateOf(false) }
    var selectedLeadToConvert by remember { mutableStateOf<LeadEntity?>(null) }

    val filteredLeads = remember(leads, selectedStageFilter) {
        if (selectedStageFilter == "ALL") leads
        else leads.filter { it.stage == selectedStageFilter }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddLeadDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Inquiry")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Inquiry")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Pipeline Stage Chips
            LazyRow(
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val stages = listOf(
                    "ALL" to "All Inquiries (${leads.size})",
                    "NEW_INQUIRY" to "New Inquiries",
                    "TRIAL_SCHEDULED" to "Trial Scheduled",
                    "TRIAL_COMPLETED" to "Trial Completed",
                    "ENROLLED" to "Enrolled",
                    "DROPPED" to "Dropped"
                )
                items(stages) { (key, label) ->
                    val isSelected = selectedStageFilter == key
                    Surface(
                        onClick = { selectedStageFilter = key },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            if (filteredLeads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No inquiries in this admission stage.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredLeads) { lead ->
                        LeadCard(
                            lead = lead,
                            onConvertClick = { selectedLeadToConvert = lead },
                            onStageChange = { newStage ->
                                viewModel.updateLeadStage(lead, newStage)
                            },
                            onWhatsAppClick = {
                                NrithyalayaUtils.launchWhatsApp(
                                    context,
                                    lead.phone,
                                    "🙏 Namaskaram ${lead.guardianName}, thank you for your interest in Sri DeMA Nrithyalaya Arts for ${lead.candidateName}."
                                )
                            },
                            onCallClick = { NrithyalayaUtils.launchCall(context, lead.phone) },
                            onDeleteClick = { viewModel.deleteLead(lead) }
                        )
                    }
                }
            }
        }
    }

    if (showAddLeadDialog) {
        AddLeadDialog(
            onAdd = { name, guardian, phone, email, discipline, notes ->
                viewModel.addLead(name, guardian, phone, email, discipline, notes)
                showAddLeadDialog = false
            },
            onDismiss = { showAddLeadDialog = false }
        )
    }

    selectedLeadToConvert?.let { lead ->
        ConvertLeadDialog(
            lead = lead,
            onConvert = { batch, level, fee ->
                viewModel.convertLeadToStudent(lead, batch, level, fee)
                selectedLeadToConvert = null
            },
            onDismiss = { selectedLeadToConvert = null }
        )
    }
}

@Composable
fun LeadCard(
    lead: LeadEntity,
    onConvertClick: () -> Unit,
    onStageChange: (String) -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lead.candidateName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                LeadStageBadge(stage = lead.stage)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Discipline: ${lead.interestedDiscipline}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Guardian: ${lead.guardianName} • Phone: ${lead.phone}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (lead.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notes: ${lead.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (lead.trialDate != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📅 Trial Class: ${lead.trialDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onCallClick) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onWhatsAppClick) {
                        Icon(imageVector = Icons.Default.Message, contentDescription = "WhatsApp", tint = Color(0xFF25D366))
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }

                if (lead.stage != "ENROLLED") {
                    Button(
                        onClick = onConvertClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.HowToReg, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Enroll Student", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun LeadStageBadge(stage: String) {
    val (label, bgColor, textColor) = when (stage) {
        "NEW_INQUIRY" -> Triple("New Inquiry", Color(0xFFE3F2FD), Color(0xFF1565C0))
        "TRIAL_SCHEDULED" -> Triple("Trial Scheduled", Color(0xFFFFF3E0), Color(0xFFE65100))
        "TRIAL_COMPLETED" -> Triple("Trial Completed", Color(0xFFEDE7F6), Color(0xFF512DA8))
        "ENROLLED" -> Triple("Enrolled", Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "DROPPED" -> Triple("Dropped", Color(0xFFFFEBEE), Color(0xFFC62828))
        else -> Triple(stage, Color(0xFFECEFF1), Color(0xFF455A64))
    }

    Surface(shape = RoundedCornerShape(12.dp), color = bgColor) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
