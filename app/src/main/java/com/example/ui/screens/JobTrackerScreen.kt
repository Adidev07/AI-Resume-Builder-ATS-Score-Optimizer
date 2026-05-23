package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.JobApplicationEntity
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobTrackerScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val applications by viewModel.jobApplications.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var jobTitle by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var statusSelected by remember { mutableStateOf("Applied") }
    var payAmount by remember { mutableStateOf("$120,000 / year") }
    var interviewNotes by remember { mutableStateOf("") }

    val statusTabs = listOf("All", "Applied", "Interview", "Offer", "Rejected")
    var filteredStatusIndex by remember { mutableStateOf(0) }

    val filteredList = remember(applications, filteredStatusIndex) {
        if (filteredStatusIndex == 0) {
            applications
        } else {
            applications.filter { it.status.equals(statusTabs[filteredStatusIndex], true) }
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Job Application Tracker", fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Manage and log interview pipelines", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                            .clickable { showAddDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Job", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Pipeline filters row
            ScrollableTabRow(
                selectedTabIndex = filteredStatusIndex,
                edgePadding = 12.dp,
                modifier = Modifier.fillMaxWidth(),
                divider = { Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)) }
            ) {
                statusTabs.forEachIndexed { index, label ->
                    Tab(
                        selected = filteredStatusIndex == index,
                        onClick = { filteredStatusIndex = index },
                        text = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // List viewport representation
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Icon(
                            Icons.Default.WorkOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No applications logged here", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Log jobs from LinkedIn, Indeed, or directly to track follow-up interview remarks.", fontSize = 11.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { application ->
                        JobItemWidget(
                            application = application,
                            onUpdateStatus = { targetStatus ->
                                viewModel.updateJobApplicationStatus(application.id, targetStatus)
                            },
                            onDelete = {
                                viewModel.deleteJobApplication(application.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // --- ADD DIALOG MODAL ---
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (jobTitle.isNotEmpty() && companyName.isNotEmpty()) {
                            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            viewModel.addJobApplication(
                                jobTitle,
                                companyName,
                                statusSelected,
                                sdf.format(java.util.Date()),
                                payAmount,
                                interviewNotes
                            )
                            showAddDialog = false
                            // Clear states
                            jobTitle = ""
                            companyName = ""
                            interviewNotes = ""
                        } else {
                            Toast.makeText(context, "Fill Position and Company fields first", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Add application")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Log New Target Application") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = jobTitle,
                        onValueChange = { jobTitle = it },
                        label = { Text("Role Position title") },
                        placeholder = { Text("e.g. Senior Product Designer") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Company Name") },
                        placeholder = { Text("e.g. Netflix") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = payAmount,
                        onValueChange = { payAmount = it },
                        label = { Text("Salary Target Offering") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Radio group for initial status
                    Text("Current Pipeline Status:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
                    val states = listOf("Applied", "Interview", "Offer", "Rejected")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        states.forEach { s ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (statusSelected == s) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f))
                                    .clickable { statusSelected = s }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = s,
                                    fontSize = 10.sp,
                                    color = if (statusSelected == s) Color.White else Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = interviewNotes,
                        onValueChange = { interviewNotes = it },
                        label = { Text("Internal Notes (Optional)") },
                        placeholder = { Text("HR call scheduled next Monday...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}

@Composable
fun JobItemWidget(
    application: JobApplicationEntity,
    onUpdateStatus: (String) -> Unit,
    onDelete: () -> Unit
) {
    val statuses = listOf("Applied", "Interview", "Offer", "Rejected")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when (application.status) {
                                    "Offer" -> Color(0xFF10B981).copy(alpha = 0.15f)
                                    "Interview" -> Color(0xFF3B82F6).copy(alpha = 0.15f)
                                    "Rejected" -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                    else -> Color(0xFF6B7280).copy(alpha = 0.15f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (application.status) {
                                "Offer" -> Icons.Default.DoneAll
                                "Interview" -> Icons.Default.Call
                                "Rejected" -> Icons.Default.Close
                                else -> Icons.Default.Send
                            },
                            contentDescription = null,
                            tint = when (application.status) {
                                "Offer" -> Color(0xFF10B981)
                                "Interview" -> Color(0xFF3B82F6)
                                "Rejected" -> Color(0xFFEF4444)
                                else -> Color(0xFF6B7280)
                            },
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(application.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${application.company} · ${application.salary}", fontSize = 11.sp, color = Color.Gray)
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                }
            }

            if (application.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text("Notes: " + application.notes, fontSize = 10.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            // Fast pipeline progression selection bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Update status:", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    statuses.forEach { s ->
                        val isSel = application.status.equals(s, true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isSel) {
                                        when (s) {
                                            "Offer" -> Color(0xFF10B981)
                                            "Interview" -> Color(0xFF3B82F6)
                                            "Rejected" -> Color(0xFFEF4444)
                                            else -> Color(0xFF6B7280)
                                        }
                                    } else {
                                        Color.LightGray.copy(alpha = 0.2f)
                                    }
                                )
                                .clickable { onUpdateStatus(s) }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = s,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) Color.White else Color.DarkGray
                            )
                        }
                    }
                }
            }
        }
    }
}
