package tech.kjo.kjo_mind_care.usecase.user

import tech.kjo.kjo_mind_care.data.model.User
import tech.kjo.kjo_mind_care.data.repository.IAuthRepository
import javax.inject.Inject

class GetCurrentUserDetailsUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(): User? {
        return authRepository.getCurrentUserDetails()
    }
}