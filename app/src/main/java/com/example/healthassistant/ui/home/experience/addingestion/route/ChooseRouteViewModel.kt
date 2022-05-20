package com.example.healthassistant.ui.home.experience.addingestion.route

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.healthassistant.data.substances.Substance
import com.example.healthassistant.data.substances.repositories.SubstanceRepository
import com.example.healthassistant.ui.main.routers.EXPERIENCE_ID_KEY
import com.example.healthassistant.ui.main.routers.SUBSTANCE_NAME_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChooseRouteViewModel @Inject constructor(
    repository: SubstanceRepository,
    state: SavedStateHandle
): ViewModel() {
    val substance: Substance? = repository.getSubstance(state.get<String>(SUBSTANCE_NAME_KEY) ?: "")
    val experienceId: Int? = state.get<String>(EXPERIENCE_ID_KEY)?.toIntOrNull()
}