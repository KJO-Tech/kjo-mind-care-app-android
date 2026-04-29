package tech.kjo.kjo_mind_care.usecase.notification

import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.model.Notification
import tech.kjo.kjo_mind_care.data.repository.INotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: INotificationRepository
) {
    operator fun invoke(userId: String): Flow<List<Notification>> = repository.getNotifications(userId)
}
