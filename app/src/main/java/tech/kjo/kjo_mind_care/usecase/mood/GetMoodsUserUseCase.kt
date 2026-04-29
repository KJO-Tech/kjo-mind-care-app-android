package tech.kjo.kjo_mind_care.usecase.mood

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import tech.kjo.kjo_mind_care.data.model.Blog
import tech.kjo.kjo_mind_care.data.model.MoodEntry
import tech.kjo.kjo_mind_care.data.repository.IMoodRepository
import javax.inject.Inject

class GetMoodsUserUseCase @Inject constructor(
    private val moodRepository: IMoodRepository
) {
    suspend operator fun invoke(userId: String): Flow<List<MoodEntry>> {
        return moodRepository.getMoodEntries(userId)
    }
}