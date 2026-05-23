package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ResumeDao {
    @Query("SELECT * FROM resumes ORDER BY updatedAt DESC")
    fun getAllResumes(): Flow<List<ResumeEntity>>

    @Query("SELECT * FROM resumes WHERE id = :id LIMIT 1")
    suspend fun getResumeById(id: String): ResumeEntity?

    @Query("SELECT * FROM resumes WHERE id = :id LIMIT 1")
    fun getResumeByIdFlow(id: String): Flow<ResumeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResume(resume: ResumeEntity)

    @Update
    suspend fun updateResume(resume: ResumeEntity)

    @Query("DELETE FROM resumes WHERE id = :id")
    suspend fun deleteResume(id: String)

    @Query("UPDATE resumes SET viewCount = viewCount + 1 WHERE id = :id")
    suspend fun incrementViewCount(id: String)
}

@Dao
interface AtsCheckDao {
    @Query("SELECT * FROM ats_checks ORDER BY createdAt DESC")
    fun getAllChecks(): Flow<List<AtsCheckEntity>>

    @Query("SELECT * FROM ats_checks WHERE resumeId = :resumeId ORDER BY createdAt DESC")
    fun getChecksForResume(resumeId: String): Flow<List<AtsCheckEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheck(check: AtsCheckEntity)

    @Query("DELETE FROM ats_checks WHERE id = :id")
    suspend fun deleteCheck(id: Int)
}

@Dao
interface CoverLetterDao {
    @Query("SELECT * FROM cover_letters ORDER BY createdAt DESC")
    fun getAllCoverLetters(): Flow<List<CoverLetterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoverLetter(letter: CoverLetterEntity)

    @Query("DELETE FROM cover_letters WHERE id = :id")
    suspend fun deleteCoverLetter(id: String)
}

@Dao
interface JobApplicationDao {
    @Query("SELECT * FROM job_applications ORDER BY createdAt DESC")
    fun getAllApplications(): Flow<List<JobApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: JobApplicationEntity)

    @Query("UPDATE job_applications SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("DELETE FROM job_applications WHERE id = :id")
    suspend fun deleteApplication(id: String)
}
