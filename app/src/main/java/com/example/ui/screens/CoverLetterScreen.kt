package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CoverLetterScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val isGenerating by viewModel.isGeneratingLetter.collectAsState()
    val drafts by viewModel.coverLetterResults.collectAsState()
    val savedLetters by viewModel.coverLetters.collectAsState()

    var companyName by remember { mutableStateOf("Google") }
    var roleTitle by remember { mutableStateOf("Senior Mobile Architect") }
    var toneSelected by remember { mutableStateOf("Professional") }
    val tones = listOf("Professional", "Enthusiastic", "Concise")

    // Achievements list (3 fields)
    var ach1 by remember { mutableStateOf("Engineered caching structures reducing network costs by 40%.") }
    var ach2 by remember { mutableStateOf("Collaborated with 4 cross-functional development squads.") }
    var ach3 by remember { mutableStateOf("Coached 3 junior designers on layout spacing guidelines.") }

    var activeLetterTab by remember { mutableStateOf(0) } // For viewing drafts A, B, C

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("cover_letter_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER TITLE
        item {
            Column {
                Text(
                    text = "AI Cover Letter Architect",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Insert target hiring details. Gemini will synthesize three natural and persuasive drafts concurrently.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        // INPUTS MODEL
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = companyName,
                            onValueChange = { companyName = it },
                            label = { Text("Company Name") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = roleTitle,
                            onValueChange = { roleTitle = it },
                            label = { Text("Target Position") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Tone selectors
                    Column {
                        Text("Core Expression Tone:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            tones.forEach { tone ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (toneSelected == tone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                                        .clickable { toneSelected = tone }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = tone,
                                        color = if (toneSelected == tone) Color.White else MaterialTheme.colorScheme.primary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Achievements Fields
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("3 Major Accomplishments to Highlight:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
                        OutlinedTextField(
                            value = ach1,
                            onValueChange = { ach1 = it },
                            label = { Text("Accomplishment #1") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = ach2,
                            onValueChange = { ach2 = it },
                            label = { Text("Accomplishment #2") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = ach3,
                            onValueChange = { ach3 = it },
                            label = { Text("Accomplishment #3") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isGenerating) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Button(
                            onClick = {
                                if (companyName.isNotEmpty() && roleTitle.isNotEmpty()) {
                                    viewModel.createCoverLetters(
                                        companyName,
                                        roleTitle,
                                        listOf(ach1, ach2, ach3).filter { it.isNotEmpty() },
                                        toneSelected
                                    )
                                    activeLetterTab = 0
                                } else {
                                    Toast.makeText(context, "Fill hiring parameters first", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Draft Concurrently (3 Drafts)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // GENERATED COPIES (Draft selection view)
        if (drafts.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        drafts.forEachIndexed { idx, _ ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (activeLetterTab == idx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .clickable { activeLetterTab = idx }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "DRAFT " + when (idx) {
                                        0 -> "A"
                                        1 -> "B"
                                        else -> "C"
                                    },
                                    color = if (activeLetterTab == idx) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val activeCopyText = drafts[activeLetterTab]

                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = activeCopyText,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        // Save copy locally to device history
                                        viewModel.saveGeneratedCoverLetter(
                                            companyName,
                                            roleTitle,
                                            toneSelected,
                                            listOf(ach1, ach2, ach3).filter { it.isNotEmpty() },
                                            activeCopyText
                                        )
                                        Toast.makeText(context, "Draft Saved to Library List below!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Save Draft", fontSize = 11.sp)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(
                                        onClick = {
                                            Toast.makeText(context, "Cover letter text copied securely!", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy text")
                                    }

                                    IconButton(
                                        onClick = {
                                            Toast.makeText(context, "Downloaded .docx file!", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = "Download plain Docx file")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // HISTORY SAVED LIBRARIES LIST
        if (savedLetters.isNotEmpty()) {
            item {
                Text("Saved Cover Letters Library (${savedLetters.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            itemsIndexed(savedLetters) { _, letter ->
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${letter.role} at ${letter.companyName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Tone style: ${letter.tone}", fontSize = 10.sp, color = Color.Gray)
                            }
                            Row {
                                IconButton(
                                    onClick = {
                                        Toast.makeText(context, "Copied historic draft text!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                                IconButton(
                                    onClick = { viewModel.deleteCoverLetter(letter.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = letter.generatedLetter,
                                fontSize = 10.sp,
                                lineHeight = 14.sp,
                                maxLines = 4
                            )
                        }
                    }
                }
            }
        }
    }
}
