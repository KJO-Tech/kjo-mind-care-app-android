package tech.kjo.kjo_mind_care.usecase.mood

import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.model.MoodEntry
import tech.kjo.kjo_mind_care.data.repository.IMoodRepository
import javax.inject.Inject

class GetMoodEntriesUseCase @Inject constructor(
    private val moodRepository: IMoodRepository
) {
    suspend operator fun invoke(userId: String): Flow<List<MoodEntry>> {
        return moodRepository.getMoodEntries(userId)
    }
}
