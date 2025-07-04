package tech.kjo.kjo_mind_care.data.repository

import kotlinx.coroutines.flow.Flow
import tech.kjo.kjo_mind_care.data.model.Emergency

interface IEmergencyResourceRepository {
    fun fetchResources(): Flow<List<Emergency>>
}