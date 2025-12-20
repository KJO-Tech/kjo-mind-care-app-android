package tech.kjo.kjo_mind_care.usecase.notification

import tech.kjo.kjo_mind_care.data.repository.INotificationRepository
import javax.inject.Inject

class MarkAllNotificationsAsReadUseCase @Inject constructor(
    private val repository: INotificationRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> = repository.markAllNotificationsAsRead(userId)
}
