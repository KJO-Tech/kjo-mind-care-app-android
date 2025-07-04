package tech.kjo.kjo_mind_care.data.repository

import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.model.Blog
import tech.kjo.kjo_mind_care.data.model.MoodEntry

interface IMoodRepository {
    suspend fun getMoodEntries(userId: String): Flow<List<MoodEntry>>
    suspend fun saveMoodEntry(moodEntry: MoodEntry): Result<String>
    fun getMoodEntriesCountByUserId(userId: String): Flow<Long>

}