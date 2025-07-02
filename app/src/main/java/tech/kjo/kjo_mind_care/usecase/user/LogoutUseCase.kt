package tech.kjo.kjo_mind_care.usecase.user

import tech.kjo.kjo_mind_care.data.repository.IAuthRepository
import tech.kjo.kjo_mind_care.utils.Resource
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val repository: IAuthRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.logout()
    }
}