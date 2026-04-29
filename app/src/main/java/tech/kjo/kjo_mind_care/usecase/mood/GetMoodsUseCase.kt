package tech.kjo.kjo_mind_care.usecase.mood

import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.model.Mood
import tech.kjo.kjo_mind_care.data.repository.IMoodRepository
import javax.inject.Inject

class GetMoodsUseCase @Inject constructor(
    private val moodRepository: IMoodRepository
) {
    suspend operator fun invoke(): Flow<List<Mood>> = moodRepository.getMoods()
}
