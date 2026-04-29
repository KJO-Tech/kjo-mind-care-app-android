package tech.kjo.kjo_mind_care.usecase.mood

import tech.kjo.kjo_mind_care.data.model.MoodEntry
import tech.kjo.kjo_mind_care.data.repository.IMoodRepository
import javax.inject.Inject

class SaveMoodUseCase @Inject constructor(
    private val moodRepository: IMoodRepository
) {
    suspend operator fun invoke(moodEntry: MoodEntry): Result<String> {
        return moodRepository.saveMoodEntry(moodEntry)
    }
}