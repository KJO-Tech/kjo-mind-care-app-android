package tech.kjo.kjo_mind_care.usecase.user

import tech.kjo.kjo_mind_care.data.model.User
import tech.kjo.kjo_mind_care.data.repository.IAuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(): User? {
        val uid = authRepository.getCurrentUserUid()
        return if (uid != null) {
            authRepository.getUserDetails(uid)
        } else {
            null
        }
    }
}