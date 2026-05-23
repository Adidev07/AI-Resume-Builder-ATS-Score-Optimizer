package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resumes")
data class ResumeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val contentJson: String, // Moshi-serialized ResumeContent
    val templateId: String,  // e.g. "classic", "modern", "minimal"
    val primaryColor: String, // hex code (e.g. "#2563EB")
    val fontPairing: String,  // e.g. "Space Grotesk + Serif"
    val atsScore: Int,
    val isPublic: Boolean,
    val publicSlug: String,
    val createdAt: Long,
    val updatedAt: Long,
    val viewCount: Int = 0    // for tracking simulated public views
)

@Entity(tableName = "ats_checks")
data class AtsCheckEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val resumeId: String,
    val jobDescription: String,
    val score: Int,
    val resultJson: String, // JSON response from Gemini
    val createdAt: Long
)

@Entity(tableName = "cover_letters")
data class CoverLetterEntity(
    @PrimaryKey val id: String,
    val companyName: String,
    val role: String,
    val achievementsJson: String, // List of key achievements
    val tone: String,             // e.g., "Professional", "Enthusiastic"
    val generatedLetter: String,  // 3-paragraph generated text
    val createdAt: Long
)

@Entity(tableName = "job_applications")
data class JobApplicationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val company: String,
    val status: String, // "Applied", "Interview", "Offer", "Rejected"
    val dateApplied: String,
    val salary: String,
    val notes: String,
    val createdAt: Long
)
