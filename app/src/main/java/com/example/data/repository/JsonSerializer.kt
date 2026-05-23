package com.example.data.repository

import com.example.data.model.ResumeContent
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object JsonSerializer {
    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val resumeContentAdapter = moshi.adapter(ResumeContent::class.java)
    private val stringListAdapter = moshi.adapter<List<String>>(
        com.squareup.moshi.Types.newParameterizedType(List::class.java, String::class.java)
    )

    fun serializeResumeContent(content: ResumeContent): String {
        return resumeContentAdapter.toJson(content)
    }

    fun deserializeResumeContent(json: String): ResumeContent {
        return try {
            resumeContentAdapter.fromJson(json) ?: ResumeContent()
        } catch (e: Exception) {
            e.printStackTrace()
            ResumeContent()
        }
    }

    fun serializeStringList(list: List<String>): String {
        return stringListAdapter.toJson(list)
    }

    fun deserializeStringList(json: String): List<String> {
        return try {
            stringListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
