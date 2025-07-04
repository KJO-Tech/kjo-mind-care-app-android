package tech.kjo.kjo_mind_care.usecase.mood

import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.repository.IMoodRepository
import javax.inject.Inject

class GetMoodEntriesCountUseCase @Inject constructor(
    private val moodRepository: IMoodRepository
) {
    operator fun invoke(userId: String): Flow<Long> {
        return moodRepository.getMoodEntriesCountByUserId(userId)
    }
}