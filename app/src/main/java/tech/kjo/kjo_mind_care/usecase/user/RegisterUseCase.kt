package tech.kjo.kjo_mind_care.usecase.user

import tech.kjo.kjo_mind_care.data.repository.IAuthRepository
import tech.kjo.kjo_mind_care.utils.Resource
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val repository: IAuthRepository) {
    suspend operator fun invoke(fullName: String, email: String, password: String): Resource<String> {
        return repository.register(fullName, email, password)
    }
}
