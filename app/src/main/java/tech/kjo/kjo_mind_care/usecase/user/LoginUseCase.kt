package tech.kjo.kjo_mind_care.usecase.user

import tech.kjo.kjo_mind_care.data.repository.AuthRepository
import tech.kjo.kjo_mind_care.utils.Resource
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Resource<String> {
        return repository.login(email, password)
    }
}