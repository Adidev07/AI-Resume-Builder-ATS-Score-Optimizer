package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.viewmodel.MainViewModel
import org.json.JSONObject

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AtsCheckerScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val resumes by viewModel.resumes.collectAsState()
    val isAtsChecking by viewModel.isAtsChecking.collectAsState()
    val atsResultJsonStr by viewModel.atsResult.collectAsState()

    var selectedResumeIndex by remember { mutableStateOf(0) }
    var targetJobDescription by remember { mutableStateOf("") }
    val safeResumeIndex = if (resumes.isEmpty()) 0 else selectedResumeIndex.coerceIn(0, resumes.size - 1)
    
    // Tracks before and after score comparison
    var beforeScore by remember { mutableStateOf<Int?>(null) }
    var isAutoFixed by remember { mutableStateOf(false) }

    val root = remember(atsResultJsonStr) {
        try {
            if (atsResultJsonStr != null) JSONObject(atsResultJsonStr!!) else JSONObject()
        } catch (e: Exception) {
            JSONObject()
        }
    }

    val score = root.optInt("score", 65)
    val verdict = root.optString("verdict", "Moderate Match")
    
    // Keyword array extraction
    val keywordObj = root.optJSONObject("keywordMatch") ?: JSONObject()
    val matchPct = keywordObj.optInt("matchPct", 50)
    val foundKeywords = remember(root) {
        keywordObj.optJSONArray("found")?.let { arr ->
            List(arr.length()) { arr.getString(it) }
        } ?: emptyList()
    }
    val missingKeywords = remember(root) {
        keywordObj.optJSONArray("missing")?.let { arr ->
            List(arr.length()) { arr.getString(it) }
        } ?: emptyList()
    }

    // Suggestions List
    val suggestions = remember(root) {
        root.optJSONArray("topSuggestions")?.let { arr ->
            List(arr.length()) { arr.getString(it) }
        } ?: emptyList()
    }

    // Section breakdown
    val sectionsObj = root.optJSONObject("sections") ?: JSONObject()
    val contactScore = sectionsObj.optInt("contact", 80)
    val summaryScore = sectionsObj.optInt("summary", 80)
    val workScore = sectionsObj.optInt("experience", 80)
    val skillsScore = sectionsObj.optInt("skills", 80)
    val educationScore = sectionsObj.optInt("education", 80)

    // Formatting block
    val formattingIssues = remember(root) {
        root.optJSONArray("formattingIssues")?.let { arr ->
            List(arr.length()) { arr.getString(it) }
        } ?: emptyList()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("ats_checker_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- HEADER TITLE ---
        item {
            Column {
                Text(
                    text = "ATS Scanner & Optimizer",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Simulate automated scanner algorithms to verify keyword weight, section parsing, and file barriers.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        // --- STEP 1: RESUME SELECTION ---
        if (resumes.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("1. Target Active Resume Document:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                                .clickable {
                                    if (resumes.isNotEmpty()) {
                                        selectedResumeIndex = (safeResumeIndex + 1) % resumes.size
                                    }
                                    isAutoFixed = false
                                    beforeScore = null
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val activeSelection = resumes.getOrNull(safeResumeIndex)
                            if (activeSelection != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(activeSelection.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Unoptimized Score: ${activeSelection.atsScore}%", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        // --- STEP 2: PASTE JOB DESCRIPTION ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("2. Paste target recruitment role descriptor:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = targetJobDescription,
                        onValueChange = { targetJobDescription = it },
                        placeholder = { Text("e.g. Seeking an Android Developer expert in Kotlin, Jetpack Compose, state flows, local caching...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isAtsChecking) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Button(
                            onClick = {
                                if (resumes.isNotEmpty() && targetJobDescription.isNotEmpty()) {
                                    val targetResume = resumes.getOrNull(safeResumeIndex)
                                    if (targetResume != null) {
                                        beforeScore = targetResume.atsScore
                                        viewModel.runAtsAnalysis(targetResume.id, targetJobDescription)
                                        isAutoFixed = false
                                    }
                                } else {
                                    Toast.makeText(context, "Insert descriptor & select resume", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Execute Scanner AI Analysis", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- STEP 3: RESULTS PRESENTATION & SCOREBAR ---
        if (atsResultJsonStr != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("AI ANALYSIS VERDICT", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(verdict, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Side-by-side Score Comparison if auto-fixed
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (beforeScore != null && isAutoFixed) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Before", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("$beforeScore%", fontWeight = FontWeight.Black, color = Color.Gray, fontSize = 13.sp)
                                }
                            }

                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(if (isAutoFixed) "After Auto-Fix" else "ATS Match Score", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            score >= 85 -> Color(0xFF10B981).copy(alpha = 0.15f)
                                            score >= 70 -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                                            else -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$score%",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = when {
                                        score >= 85 -> Color(0xFF10B981)
                                        score >= 70 -> Color(0xFFF59E0B)
                                        else -> Color(0xFFEF4444)
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ONE-CLICK AUTO-FIX ACTION BUTTON
                    if (!isAutoFixed && missingKeywords.isNotEmpty() && resumes.isNotEmpty()) {
                        Button(
                            onClick = {
                                val targetResume = resumes.getOrNull(safeResumeIndex)
                                if (targetResume != null) {
                                    viewModel.runAtsAutoFix(targetResume.id, missingKeywords)
                                    isAutoFixed = true
                                    Toast.makeText(context, "Keywords injected inside Skills! Score boosted!", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("One-Click Auto-Fix AI Suggestions", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Text("Add missing terms dynamically into your technical list indexes.", fontSize = 9.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                    } else if (isAutoFixed) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFDCFCE7), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎉 AUTO-FIX APPLIED: Missing skills merged in-place!", color = Color(0xFF15803D), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Keyword analysis index cards
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
                            Text("Keyword Density Matrix", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Matches: $matchPct%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Match list
                        Text("IDENTIFIED KEYWORDS MATCHES:", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF15803D))
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (foundKeywords.isEmpty()) {
                                Text("No matching keywords scanned.", fontSize = 11.sp, color = Color.Gray)
                            } else {
                                foundKeywords.forEach { word ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFD1FAE5), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF065F46), modifier = Modifier.size(10.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(word, color = Color(0xFF065F46), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("MISSING KEYWORDS (CRITICAL BLOCKERS):", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFFB91C1C))
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (missingKeywords.isEmpty()) {
                                Text("All keywords from job descriptor successfully matched!", fontSize = 11.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                            } else {
                                missingKeywords.forEach { word ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFFEE2E2), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFF991B1B), modifier = Modifier.size(10.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(word, color = Color(0xFF991B1B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Formatting Warnings Block
            if (formattingIssues.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Format Scanner Warnings", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFD97706))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            formattingIssues.forEach { issue ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("•", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 6.dp))
                                    Text(issue, fontSize = 11.sp, lineHeight = 14.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Section Completeness Breakdown
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Parser Section Readings", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        val sections = listOf(
                            "Contact Information Info" to contactScore,
                            "Professional Profile Summary" to summaryScore,
                            "Employment Role Work Experience" to workScore,
                            "Technical Skills Repository" to skillsScore,
                            "Academic Education Log" to educationScore
                        )

                        sections.forEach { (title, valScore) ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(title, fontSize = 11.sp)
                                    Text("$valScore%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                LinearProgressIndicator(
                                    progress = { valScore.toFloat() / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(CircleShape),
                                    color = if (valScore >= 80) Color(0xFF10B981) else Color(0xFFF59E0B)
                                )
                            }
                        }
                    }
                }
            }

            // Career Suggestions
            if (suggestions.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Coach Suggested Improvements", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            suggestions.forEach { tip ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(Icons.Default.ArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Text(tip, fontSize = 11.sp, lineHeight = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
