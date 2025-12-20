package tech.kjo.kjo_mind_care.usecase.notification

import tech.kjo.kjo_mind_care.data.repository.INotificationRepository
import javax.inject.Inject

class MarkNotificationAsReadUseCase @Inject constructor(
    private val repository: INotificationRepository
) {
    suspend operator fun invoke(userId: String, notificationId: String): Result<Unit> = repository.markNotificationAsRead(userId, notificationId)
}
