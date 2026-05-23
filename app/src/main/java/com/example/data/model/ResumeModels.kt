package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ContactInfo(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val website: String = "",
    val linkedin: String = ""
) {
    fun isNotEmpty(): Boolean = firstName.isNotEmpty() || lastName.isNotEmpty() || email.isNotEmpty()
}

@JsonClass(generateAdapter = true)
data class WorkExperience(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String = "",
    val company: String = "",
    val location: String = "",
    val dateRange: String = "",
    val description: String = "" // STAR format bullet points split by newlines
)

@JsonClass(generateAdapter = true)
data class Education(
    val id: String = java.util.UUID.randomUUID().toString(),
    val degree: String = "",
    val institution: String = "",
    val location: String = "",
    val dateRange: String = "",
    val description: String = ""
)

@JsonClass(generateAdapter = true)
data class Project(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String = "",
    val roleOrDescription: String = "",
    val link: String = "",
    val bulletPoints: String = ""
)

@JsonClass(generateAdapter = true)
data class Certification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val issuer: String = "",
    val date: String = ""
)

@JsonClass(generateAdapter = true)
data class Language(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val proficiency: String = "" // e.g., Native, Fluent, Intermediate
)

@JsonClass(generateAdapter = true)
data class ResumeContent(
    val contactInfo: ContactInfo = ContactInfo(),
    val summary: String = "",
    val workExperience: List<WorkExperience> = emptyList(),
    val education: List<Education> = emptyList(),
    val skills: List<String> = emptyList(),
    val projects: List<Project> = emptyList(),
    val certifications: List<Certification> = emptyList(),
    val languages: List<Language> = emptyList()
)
