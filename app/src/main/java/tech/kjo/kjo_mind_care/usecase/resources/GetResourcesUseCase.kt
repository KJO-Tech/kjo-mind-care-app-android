package tech.kjo.kjo_mind_care.usecase.resources

import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.model.Emergency
import tech.kjo.kjo_mind_care.data.repository.IEmergencyResourceRepository
import javax.inject.Inject

class GetEmergencyResourcesUseCase @Inject constructor(
    private val repo: IEmergencyResourceRepository
) {
    operator fun invoke(): Flow<List<Emergency>> =
        repo.fetchResources()
}