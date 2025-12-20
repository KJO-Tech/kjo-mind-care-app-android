package tech.kjo.kjo_mind_care.usecase.notification

import tech.kjo.kjo_mind_care.data.model.Notification
import tech.kjo.kjo_mind_care.data.repository.INotificationRepository
import javax.inject.Inject

class AddNotificationUseCase @Inject constructor(
    private val repository: INotificationRepository
) {
    suspend operator fun invoke(userId: String, notification: Notification): Result<String> = repository.addNotification(userId, notification)
}
