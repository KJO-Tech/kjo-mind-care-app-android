package tech.kjo.kjo_mind_care.ui.main.resources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import tech.kjo.kjo_mind_care.data.model.Emergency
import tech.kjo.kjo_mind_care.data.repository.IEmergencyResourceRepository
import tech.kjo.kjo_mind_care.usecase.resources.GetEmergencyResourcesUseCase
import javax.inject.Inject


@HiltViewModel
class EmergencyResourcesViewModel @Inject constructor(
    private val getResources: GetEmergencyResourcesUseCase
) : ViewModel() {

    val resources: StateFlow<List<Emergency>> =
        getResources()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}