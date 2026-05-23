package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.ResumeEntity
import com.example.data.model.*
import com.example.data.repository.JsonSerializer
import com.example.ui.theme.ResumeStyleConfigs
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResumeBuilderScreen(
    viewModel: MainViewModel,
    resumeId: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentResumeState by viewModel.currentResume.collectAsState()
    val plan by viewModel.currentPlan.collectAsState()
    val isPro = plan == "pro"

    // Set active select resume on load
    LaunchedEffect(resumeId) {
        viewModel.selectResume(resumeId)
    }

    if (currentResumeState == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val resume = currentResumeState!!
    
    // Parse ResumeContent
    val resumeContent = remember(resume.contentJson) {
        JsonSerializer.deserializeResumeContent(resume.contentJson)
    }

    // Builder Tabs: 0 -> Form Inputs, 1 -> Theme & Fonts, 2 -> Live Preview, 3 -> AI Writer & Export
    var activeTab by remember { mutableStateOf(0) }
    val tabs = listOf("Form Inputs", "Theme Style", "Live Document", "AI Writer & Export")

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
                            Text(resume.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Aesthetic Completeness: ${resume.atsScore}%", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                        }
                    }

                    // Score bar
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Score: ${resume.atsScore}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
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
            // Scrollable Tab Layout
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                edgePadding = 12.dp,
                modifier = Modifier.fillMaxWidth(),
                divider = { Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)) }
            ) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = activeTab == index,
                        onClick = { activeTab = index },
                        text = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Main Editor viewport mapping active selection
            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    0 -> FormInputsTab(
                        content = resumeContent,
                        onUpdate = { updated -> viewModel.updateResumeContent(resume.id, updated) }
                    )
                    1 -> ThemeSelectionTab(
                        currentTemplateId = resume.templateId,
                        currentColorHex = resume.primaryColor,
                        currentFontPairing = resume.fontPairing,
                        onUpdate = { templateId, colorHex, font ->
                            viewModel.updateResumeConfig(resume.id, templateId, colorHex, font)
                        }
                    )
                    2 -> LivePreviewTab(
                        content = resumeContent,
                        templateId = resume.templateId,
                        primaryColorHex = resume.primaryColor,
                        fontPairingName = resume.fontPairing,
                        isWatermarked = !isPro
                    )
                    3 -> AIExportTab(
                        viewModel = viewModel,
                        resume = resume,
                        content = resumeContent,
                        isPro = isPro
                    )
                }
            }
        }
    }
}

// ====================== FORMS TAB ======================
@Composable
fun FormInputsTab(
    content: ResumeContent,
    onUpdate: (ResumeContent) -> Unit
) {
    var expandedSection by remember { mutableStateOf<String?>("contact") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. CONTACT INFO ---
        item {
            FormSectionHeader("Contact Information", "contact", expandedSection) {
                expandedSection = if (expandedSection == "contact") null else "contact"
            }
            if (expandedSection == "contact") {
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = content.contactInfo.firstName,
                                onValueChange = { onUpdate(content.copy(contactInfo = content.contactInfo.copy(firstName = it))) },
                                label = { Text("First Name") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = content.contactInfo.lastName,
                                onValueChange = { onUpdate(content.copy(contactInfo = content.contactInfo.copy(lastName = it))) },
                                label = { Text("Last Name") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        OutlinedTextField(
                            value = content.contactInfo.email,
                            onValueChange = { onUpdate(content.copy(contactInfo = content.contactInfo.copy(email = it))) },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = content.contactInfo.phone,
                            onValueChange = { onUpdate(content.copy(contactInfo = content.contactInfo.copy(phone = it))) },
                            label = { Text("Phone Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = content.contactInfo.location,
                            onValueChange = { onUpdate(content.copy(contactInfo = content.contactInfo.copy(location = it))) },
                            label = { Text("Location (City, State / Country)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = content.contactInfo.website,
                            onValueChange = { onUpdate(content.copy(contactInfo = content.contactInfo.copy(website = it))) },
                            label = { Text("Portfolio Website") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = content.contactInfo.linkedin,
                            onValueChange = { onUpdate(content.copy(contactInfo = content.contactInfo.copy(linkedin = it))) },
                            label = { Text("LinkedIn Link") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // --- 2. SUMMARY ---
        item {
            FormSectionHeader("Professional Summary", "summary", expandedSection) {
                expandedSection = if (expandedSection == "summary") null else "summary"
            }
            if (expandedSection == "summary") {
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = content.summary,
                            onValueChange = { onUpdate(content.copy(summary = it)) },
                            placeholder = { Text("Explain your key technical experiences and immediate career highlights.") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4
                        )
                    }
                }
            }
        }

        // --- 3. WORK EXPERIENCE ---
        item {
            FormSectionHeader("Work Experience", "work", expandedSection) {
                expandedSection = if (expandedSection == "work") null else "work"
            }
            if (expandedSection == "work") {
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Section controller: reorder actions and listings
                        content.workExperience.forEachIndexed { index, job ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Position #${index + 1}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Row {
                                        // Simple drag reorder triggers
                                        IconButton(
                                            onClick = {
                                                if (index > 0) {
                                                    val mutable = content.workExperience.toMutableList()
                                                    val temp = mutable[index]
                                                    mutable[index] = mutable[index - 1]
                                                    mutable[index - 1] = temp
                                                    onUpdate(content.copy(workExperience = mutable))
                                                }
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(14.dp))
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = {
                                                val mutable = content.workExperience.toMutableList()
                                                mutable.removeAt(index)
                                                onUpdate(content.copy(workExperience = mutable))
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))

                                OutlinedTextField(
                                    value = job.role,
                                    onValueChange = { value ->
                                        val mutable = content.workExperience.toMutableList()
                                        mutable[index] = job.copy(role = value)
                                        onUpdate(content.copy(workExperience = mutable))
                                    },
                                    label = { Text("Role / Position Title") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = job.company,
                                    onValueChange = { value ->
                                        val mutable = content.workExperience.toMutableList()
                                        mutable[index] = job.copy(company = value)
                                        onUpdate(content.copy(workExperience = mutable))
                                    },
                                    label = { Text("Company Name") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = job.location,
                                        onValueChange = { value ->
                                            val mutable = content.workExperience.toMutableList()
                                            mutable[index] = job.copy(location = value)
                                            onUpdate(content.copy(workExperience = mutable))
                                        },
                                        label = { Text("Location") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = job.dateRange,
                                        onValueChange = { value ->
                                            val mutable = content.workExperience.toMutableList()
                                            mutable[index] = job.copy(dateRange = value)
                                            onUpdate(content.copy(workExperience = mutable))
                                        },
                                        label = { Text("Dates (e.g., 2021-Pres)") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = job.description,
                                    onValueChange = { value ->
                                        val mutable = content.workExperience.toMutableList()
                                        mutable[index] = job.copy(description = value)
                                        onUpdate(content.copy(workExperience = mutable))
                                    },
                                    label = { Text("Bullet Achievements (STAR layout)") },
                                    placeholder = { Text("- Spearheaded cached data pipelines speeding loads by 24%.\n- Governed cross-functional cloud rollouts.") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val mutable = content.workExperience.toMutableList()
                                mutable.add(WorkExperience())
                                onUpdate(content.copy(workExperience = mutable))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Job Entry", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // --- 4. EDUCATION ---
        item {
            FormSectionHeader("Education & Qualifications", "education", expandedSection) {
                expandedSection = if (expandedSection == "education") null else "education"
            }
            if (expandedSection == "education") {
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        content.education.forEachIndexed { index, edu ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Degree #${index + 1}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    IconButton(
                                        onClick = {
                                            val mutable = content.education.toMutableList()
                                            mutable.removeAt(index)
                                            onUpdate(content.copy(education = mutable))
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
                                    }
                                }

                                OutlinedTextField(
                                    value = edu.degree,
                                    onValueChange = { value ->
                                        val mutable = content.education.toMutableList()
                                        mutable[index] = edu.copy(degree = value)
                                        onUpdate(content.copy(education = mutable))
                                    },
                                    label = { Text("Degree (e.g. B.S. Architecture)") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = edu.institution,
                                    onValueChange = { value ->
                                        val mutable = content.education.toMutableList()
                                        mutable[index] = edu.copy(institution = value)
                                        onUpdate(content.copy(education = mutable))
                                    },
                                    label = { Text("Institution Name") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = edu.location,
                                        onValueChange = { value ->
                                            val mutable = content.education.toMutableList()
                                            mutable[index] = edu.copy(location = value)
                                            onUpdate(content.copy(education = mutable))
                                        },
                                        label = { Text("Location") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = edu.dateRange,
                                        onValueChange = { value ->
                                            val mutable = content.education.toMutableList()
                                            mutable[index] = edu.copy(dateRange = value)
                                            onUpdate(content.copy(education = mutable))
                                        },
                                        label = { Text("Dates") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val mutable = content.education.toMutableList()
                                mutable.add(Education())
                                onUpdate(content.copy(education = mutable))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Education Qualification", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // --- 5. SKILLS INDEX ---
        item {
            FormSectionHeader("Skills Directory Indices", "skills", expandedSection) {
                expandedSection = if (expandedSection == "skills") null else "skills"
            }
            if (expandedSection == "skills") {
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Insert comma separated values representing technical packages or methodologies:",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val rawSkills = remember(content.skills) {
                            content.skills.joinToString(", ")
                        }

                        OutlinedTextField(
                            value = rawSkills,
                            onValueChange = { value ->
                                val list = value.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                onUpdate(content.copy(skills = list))
                            },
                            placeholder = { Text("e.g. Kotlin, Compose, AWS, Python, Scrum") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FormSectionHeader(title: String, id: String, expandedId: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = when (id) {
                    "contact" -> Icons.Default.ContactMail
                    "summary" -> Icons.Default.Description
                    "work" -> Icons.Default.Work
                    "education" -> Icons.Default.School
                    else -> Icons.Default.SettingsApplications
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
        Icon(
            imageVector = if (expandedId == id) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}


// ====================== THEME TAB ======================
@Composable
fun ThemeSelectionTab(
    currentTemplateId: String,
    currentColorHex: String,
    currentFontPairing: String,
    onUpdate: (templateId: String, colorHex: String, font: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ACCENT COLORS
        item {
            Text("Select Palette Accent Color:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ResumeStyleConfigs.accentColors.forEach { config ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(config.color)
                            .border(
                                3.dp,
                                if (currentColorHex == config.hex) Color.White else Color.Transparent,
                                CircleShape
                            )
                            .clickable { onUpdate(currentTemplateId, config.hex, currentFontPairing) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentColorHex == config.hex) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // FONTS SELECTOR
        item {
            Text("Select Typography Font Pairing:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ResumeStyleConfigs.fontPairings.forEach { config ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUpdate(currentTemplateId, currentColorHex, config.name) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(config.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Preview title text vs standard lists summary", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                            if (currentFontPairing == config.name) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        // THEMATIC LAYOUT TEMPLATE
        item {
            Text("Select ATS-Optimized Sheet Layout:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            ResumeStyleConfigs.templates.forEach { config ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(
                            2.dp,
                            if (currentTemplateId == config.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onUpdate(config.id, currentColorHex, currentFontPairing) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (config.id) {
                                    "classic" -> Icons.Default.ViewAgenda
                                    "modern" -> Icons.Default.ViewSidebar
                                    "minimal" -> Icons.Default.CropPortrait
                                    "creative" -> Icons.Default.Category
                                    else -> Icons.Default.Article
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(config.title, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            Text(config.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), lineHeight = 14.sp)
                        }

                        if (currentTemplateId == config.id) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}


// ====================== DYNAMIC PREVIEW TAB ======================
@Composable
fun LivePreviewTab(
    content: ResumeContent,
    templateId: String,
    primaryColorHex: String,
    fontPairingName: String,
    isWatermarked: Boolean
) {
    val accentColor = remember(primaryColorHex) {
        safeParseColor(primaryColorHex)
    }

    val fontConfig = remember(fontPairingName) {
        ResumeStyleConfigs.fontPairings.find { it.name == fontPairingName } ?: ResumeStyleConfigs.fontPairings[0]
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0)) // slate-200 paper frame
            .padding(12.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // --- HEADER BLOCK (VARYING TEMPLATE SPECIFICS) ---
                    item {
                        when (templateId) {
                            "modern" -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(BorderStroke(1.5.dp, accentColor), RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        "${content.contactInfo.firstName} ${content.contactInfo.lastName}".uppercase(),
                                        fontFamily = fontConfig.titleFont,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = accentColor
                                    )
                                    Text(
                                        text = "${content.contactInfo.email} | ${content.contactInfo.phone} | ${content.contactInfo.location}",
                                        fontSize = 9.sp,
                                        color = Color.DarkGray
                                    )
                                    if (content.contactInfo.website.isNotEmpty()) {
                                        Text(text = "Web: ${content.contactInfo.website} | IN: ${content.contactInfo.linkedin}", fontSize = 8.sp, color = accentColor)
                                    }
                                }
                            }
                            "classic" -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "${content.contactInfo.firstName} ${content.contactInfo.lastName}",
                                        fontFamily = fontConfig.titleFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = Color.Black
                                    )
                                    Divider(color = accentColor, thickness = 2.dp, modifier = Modifier.padding(vertical = 4.dp))
                                    Text(
                                        text = "${content.contactInfo.email} · ${content.contactInfo.phone} · ${content.contactInfo.location}",
                                        fontSize = 9.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                            "executive" -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Divider(color = accentColor, thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "${content.contactInfo.firstName} ${content.contactInfo.lastName}".uppercase(),
                                        fontFamily = fontConfig.titleFont,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = Color.Black,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Divider(color = accentColor, thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${content.contactInfo.email} | ${content.contactInfo.phone} | ${content.contactInfo.location}",
                                        fontSize = 9.sp,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                            else -> { // MINIMALIST / CREATIVE Default representation
                                Column {
                                    Text(
                                        "${content.contactInfo.firstName} ${content.contactInfo.lastName}",
                                        fontFamily = fontConfig.titleFont,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 22.sp,
                                        color = accentColor
                                    )
                                    Text(
                                        text = "${content.contactInfo.location} · ${content.contactInfo.email}",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    // --- SUMMARY BLOCK ---
                    if (content.summary.isNotEmpty()) {
                        item {
                            Column {
                                PreviewSectionTitle("Professional Profile", accentColor, fontConfig.titleFont)
                                Text(
                                    text = content.summary,
                                    fontFamily = fontConfig.bodyFont,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }

                    // --- EXPERIENCE LIST ---
                    if (content.workExperience.isNotEmpty()) {
                        item {
                            PreviewSectionTitle("Work Experience", accentColor, fontConfig.titleFont)
                        }
                        itemsIndexed(content.workExperience) { _, job ->
                            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(job.role, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                                    Text(job.dateRange, fontSize = 9.sp, color = Color.DarkGray)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${job.company}, ${job.location}", fontWeight = FontWeight.Medium, fontSize = 10.sp, color = accentColor)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                // Split bullet points list
                                job.description.split("\n").forEach { bullet ->
                                    if (bullet.trim().isNotEmpty()) {
                                        Text(
                                            text = bullet,
                                            fontFamily = fontConfig.bodyFont,
                                            fontSize = 9.sp,
                                            lineHeight = 12.sp,
                                            color = Color.DarkGray,
                                            modifier = Modifier.padding(start = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // --- EDUCATION LIST ---
                    if (content.education.isNotEmpty()) {
                        item {
                            PreviewSectionTitle("Education", accentColor, fontConfig.titleFont)
                        }
                        itemsIndexed(content.education) { _, edu ->
                            Column(modifier = Modifier.padding(bottom = 2.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(edu.degree, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                                    Text(edu.dateRange, fontSize = 9.sp, color = Color.DarkGray)
                                }
                                Text("${edu.institution} · ${edu.location}", fontSize = 10.sp, color = accentColor)
                                if (edu.description.isNotEmpty()) {
                                    Text(edu.description, fontSize = 9.sp, color = Color.DarkGray)
                                }
                            }
                        }
                    }

                    // --- SKILLS BLOCK ---
                    if (content.skills.isNotEmpty()) {
                        item {
                            Column {
                                PreviewSectionTitle("Skills Profile", accentColor, fontConfig.titleFont)
                                val combinedSkills = content.skills.joinToString(" • ")
                                Text(
                                    text = combinedSkills,
                                    fontFamily = fontConfig.bodyFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                // WATERMARK OVERLAY
                if (isWatermarked) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(Color.Yellow.copy(alpha = 0.85f))
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("WATERMARK: Created with ResumeAI Free Tier. Upgrade to Pro.", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewSectionTitle(title: String, tintColor: Color, fontFamily: FontFamily) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp)) {
        Text(
            text = title.uppercase(),
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = tintColor,
            letterSpacing = 0.5.sp
        )
        Divider(color = tintColor.copy(alpha = 0.4f), thickness = 1.dp, modifier = Modifier.padding(top = 2.dp))
    }
}


// ====================== AI WRITER & EXPORTS TAB ======================
@Composable
fun AIExportTab(
    viewModel: MainViewModel,
    resume: ResumeEntity,
    content: ResumeContent,
    isPro: Boolean
) {
    val context = LocalContext.current
    var jobDescriptionInput by remember { mutableStateOf("") }
    val isImproving by viewModel.isImproving.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI IMPROVE FORM
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFEA580C))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI STAR Optimizer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        "Paste a target job description below. Gemini will rewrite experience bullets to dynamically incorporate matching keywords, STAR metrics, and powerful verbs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = jobDescriptionInput,
                        onValueChange = { jobDescriptionInput = it },
                        placeholder = { Text("Paste job specifications here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isImproving) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Button(
                            onClick = {
                                if (jobDescriptionInput.isNotEmpty()) {
                                    viewModel.optimizeResumeWithAi(resume.id, jobDescriptionInput) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Resume Optimized Successfully!", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(context, "API Optimization Failed! Check API key configuration.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Please paste job description first!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Optimize Experiences with AI", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // PHYSICAL EXPORTS CONTROL PANELS
        item {
            Text("Download and Export Formats", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // PDF
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFEF4444))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Export Pixel-Perfect PDF", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(if (isPro) "HQ resolution, watermark removed" else "Includes ResumeAI Free watermark", fontSize = 9.sp, color = Color.Gray)
                            }
                        }
                        Button(
                            onClick = {
                                Toast.makeText(context, "PDF saved/shared successfully to local client downloads!", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Save PDF", fontSize = 10.sp)
                        }
                    }
                    
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // Word .docx
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color(0xFF2563EB))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Export ATS-Safe Word (.docx)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Single-column plain formatting", fontSize = 9.sp, color = Color.Gray)
                            }
                        }
                        Button(
                            onClick = {
                                if (isPro) {
                                    Toast.makeText(context, "Word document exported successfully!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Word Export is. Pro exclusive features! Please upgrade in Landing billing tab.", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier.height(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPro) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                contentColor = if (isPro) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text("Save DOCX", fontSize = 10.sp)
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // Text Copier
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF10B981))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Copy Plain Text", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Ideal for copy-pasting directly into forms", fontSize = 9.sp, color = Color.Gray)
                            }
                        }
                        Button(
                            onClick = {
                                val fullResumeText = """
                                    ${resume.title}
                                    ${content.contactInfo.firstName} ${content.contactInfo.lastName}
                                    ${content.contactInfo.email} | ${content.contactInfo.phone} | ${content.contactInfo.location}
                                    
                                    SUMMARY:
                                    ${content.summary}
                                    
                                    WORK EXPERIENCE:
                                    ${content.workExperience.joinToString("\n") { "${it.role} at ${it.company}\n${it.description}" }}
                                """.trimIndent()

                                Toast.makeText(context, "Plain text copied to clipboard successfully!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Copy Raw Text", fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // SHARE LINKS PANEL
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Public Share Link (Read-Only)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Switch(
                            checked = resume.isPublic,
                            onCheckedChange = { value ->
                                viewModel.toggleResumePublicState(resume.id, value)
                                if (value) {
                                    viewModel.simulatePublicView(resume.id) // simulate dynamic viewing log
                                }
                            }
                        )
                    }
                    if (resume.isPublic) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "https://resume.ai/share/${resume.publicSlug}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(0.8f),
                                maxLines = 1
                            )
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        Toast.makeText(context, "Read-Only share link copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun safeParseColor(hexStr: String?, defaultColor: Color = Color(0xFF2563EB)): Color {
    if (hexStr.isNullOrEmpty()) return defaultColor
    return try {
        val cleaned = hexStr.trim()
        val formatted = if (cleaned.startsWith("#")) cleaned else "#$cleaned"
        Color(android.graphics.Color.parseColor(formatted))
    } catch (e: Exception) {
        defaultColor
    }
}

