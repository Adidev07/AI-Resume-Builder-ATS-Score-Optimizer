package com.example.data.repository

import com.example.data.db.AtsCheckEntity
import com.example.data.db.CoverLetterEntity
import com.example.data.db.JobApplicationEntity
import com.example.data.db.ResumeDao
import com.example.data.db.AtsCheckDao
import com.example.data.db.CoverLetterDao
import com.example.data.db.JobApplicationDao
import com.example.data.db.ResumeEntity
import com.example.data.model.ResumeContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppRepository(
    private val resumeDao: ResumeDao,
    private val atsCheckDao: AtsCheckDao,
    private val coverLetterDao: CoverLetterDao,
    private val jobApplicationDao: JobApplicationDao
) {
    // Resumes
    val allResumes: Flow<List<ResumeEntity>> = resumeDao.getAllResumes()

    fun getResumeByIdFlow(id: String): Flow<ResumeEntity?> = resumeDao.getResumeByIdFlow(id)

    suspend fun getResumeById(id: String): ResumeEntity? = resumeDao.getResumeById(id)

    suspend fun insertResume(resume: ResumeEntity) = resumeDao.insertResume(resume)

    suspend fun updateResume(resume: ResumeEntity) = resumeDao.updateResume(resume)

    suspend fun deleteResume(id: String) = resumeDao.deleteResume(id)

    suspend fun incrementViewCount(id: String) = resumeDao.incrementViewCount(id)

    // ATS Checks
    val allAtsChecks: Flow<List<AtsCheckEntity>> = atsCheckDao.getAllChecks()

    fun getChecksForResume(resumeId: String): Flow<List<AtsCheckEntity>> =
        atsCheckDao.getChecksForResume(resumeId)

    suspend fun insertAtsCheck(check: AtsCheckEntity) = atsCheckDao.insertCheck(check)

    suspend fun deleteAtsCheck(id: Int) = atsCheckDao.deleteCheck(id)

    // Cover Letters
    val allCoverLetters: Flow<List<CoverLetterEntity>> = coverLetterDao.getAllCoverLetters()

    suspend fun insertCoverLetter(letter: CoverLetterEntity) = coverLetterDao.insertCoverLetter(letter)

    suspend fun deleteCoverLetter(id: String) = coverLetterDao.deleteCoverLetter(id)

    // Job Applications (Tracker)
    val allJobApplications: Flow<List<JobApplicationEntity>> = jobApplicationDao.getAllApplications()

    suspend fun insertJobApplication(application: JobApplicationEntity) =
        jobApplicationDao.insertApplication(application)

    suspend fun updateJobApplicationStatus(id: String, status: String) =
        jobApplicationDao.updateStatus(id, status)

    suspend fun deleteJobApplication(id: String) = jobApplicationDao.deleteApplication(id)
}
