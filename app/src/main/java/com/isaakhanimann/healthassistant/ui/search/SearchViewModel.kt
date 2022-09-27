package com.isaakhanimann.healthassistant.ui.search

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaakhanimann.healthassistant.data.room.experiences.ExperienceRepository
import com.isaakhanimann.healthassistant.data.substances.classes.Category
import com.isaakhanimann.healthassistant.data.substances.classes.SubstanceWithCategories
import com.isaakhanimann.healthassistant.data.substances.repositories.SubstanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    experienceRepo: ExperienceRepository,
    substanceRepo: SubstanceRepository
) : ViewModel() {

    private val allSubstancesFlow: Flow<List<SubstanceWithCategories>> =
        substanceRepo.getAllSubstancesWithCategoriesFlow()
    private val allCategoriesFlow: Flow<List<Category>> = substanceRepo.getAllCategoriesFlow()

    val customSubstancesFlow = experienceRepo.getCustomSubstancesFlow().stateIn(
        initialValue = emptyList(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000)
    )

    private val recentlyUsedSubstancesFlow: Flow<List<SubstanceWithCategories>> =
        experienceRepo.getLastUsedSubstanceNamesFlow(limit = 100).map { lastUsedSubstanceNames ->
            lastUsedSubstanceNames.mapNotNull {
                substanceRepo.getSubstanceWithCategories(
                    substanceName = it
                )
            }
        }

    private val _searchTextFlow = MutableStateFlow("")
    val searchTextFlow = _searchTextFlow.asStateFlow()

    private val filtersFlow = MutableStateFlow(listOf("common"))

    private val youUsedChipName = "you-used"
    private val customChipName = "custom"

    fun onFilterTapped(filterName: String) {
        viewModelScope.launch {
            when (filterName) {
                youUsedChipName -> {
                    isShowingYouUsedFlow.emit(isShowingYouUsedFlow.value.not())
                }
                customChipName -> {
                    isShowingCustomSubstancesFlow.emit(isShowingCustomSubstancesFlow.value.not())
                }
                else -> {
                    val filters = filtersFlow.value.toMutableList()
                    if (filters.contains(filterName)) {
                        filters.remove(filterName)
                    } else {
                        filters.add(filterName)
                    }
                    filtersFlow.emit(filters)
                }
            }
        }
    }

    private val isShowingYouUsedFlow = MutableStateFlow(false)
    val isShowingCustomSubstancesFlow = MutableStateFlow(false)

    val chipCategoriesFlow: StateFlow<List<CategoryChipModel>> =
        allCategoriesFlow.combine(filtersFlow) { categories, filters ->
            categories.map { category ->
                val isActive = filters.contains(category.name)
                CategoryChipModel(
                    chipName = category.name,
                    color = category.color,
                    isActive = isActive
                )
            }
        }.combine(isShowingYouUsedFlow) { chips, isShowingYouUsed ->
            val newChips = chips.toMutableList()
            newChips.add(
                0, CategoryChipModel(
                    chipName = youUsedChipName,
                    color = Color.Magenta,
                    isActive = isShowingYouUsed
                )
            )
            return@combine newChips
        }.combine(isShowingCustomSubstancesFlow) { chips, isShowingCustom ->
            val newChips = chips.toMutableList()
            newChips.add(
                0, CategoryChipModel(
                    chipName = customChipName,
                    color = Color.Cyan,
                    isActive = isShowingCustom
                )
            )
            return@combine newChips
        }.stateIn(
            initialValue = emptyList(),
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    private val allOrYouUsedSubstances =
        allSubstancesFlow.combine(recentlyUsedSubstancesFlow) { all, recents ->
            Pair(first = all, recents)
        }.combine(isShowingYouUsedFlow) { pair, isShowingYouUsed ->
            if (isShowingYouUsed) {
                return@combine pair.second
            } else {
                return@combine pair.first
            }
        }

    val filteredSubstances =
        allOrYouUsedSubstances.combine(filtersFlow) { substances, filters ->
            substances.filter { substanceWithCategories ->
                filters.all { substanceWithCategories.substance.categories.contains(it) }
            }
        }.combine(searchTextFlow) { substances, searchText ->
            getMatchingSubstances(searchText, substances)
        }.map { substancesWithCategories ->
            substancesWithCategories.map {
                SubstanceModel(
                    name = it.substance.name,
                    commonNames = it.substance.commonNames,
                    categories = it.categories.map { category ->
                        CategoryModel(
                            name = category.name,
                            color = category.color
                        )
                    }
                )
            }
        }.stateIn(
            initialValue = emptyList(),
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    fun filterSubstances(searchText: String) {
        viewModelScope.launch {
            _searchTextFlow.emit(searchText)
        }
    }

    companion object {
        fun getMatchingSubstances(
            searchText: String,
            substances: List<SubstanceWithCategories>
        ): List<SubstanceWithCategories> {
            return if (searchText.isEmpty()) {
                substances
            } else {
                if (searchText.length < 3) {
                    substances.filter { substanceWithCategories ->
                        substanceWithCategories.substance.name.startsWith(
                            prefix = searchText,
                            ignoreCase = true
                        ) ||
                                substanceWithCategories.substance.commonNames.any { commonName ->
                                    commonName.startsWith(prefix = searchText, ignoreCase = true)
                                }
                    }
                } else {
                    val containing = substances.filter { substanceWithCategories ->
                        substanceWithCategories.substance.name.contains(
                            other = searchText,
                            ignoreCase = true
                        ) ||
                                substanceWithCategories.substance.commonNames.any { commonName ->
                                    commonName.contains(other = searchText, ignoreCase = true)
                                }
                    }
                    val prefixAndContainingMatches =
                        containing.partition { substanceWithCategories ->
                            substanceWithCategories.substance.name.startsWith(
                                prefix = searchText,
                                ignoreCase = true
                            ) ||
                                    substanceWithCategories.substance.commonNames.any { commonName ->
                                        commonName.startsWith(
                                            prefix = searchText,
                                            ignoreCase = true
                                        )
                                    }
                        }
                    prefixAndContainingMatches.first + prefixAndContainingMatches.second
                }
            }
        }
    }
}

data class CategoryChipModel(
    val chipName: String,
    val color: Color,
    val isActive: Boolean
)

data class SubstanceModel(
    val name: String,
    val commonNames: List<String>,
    val categories: List<CategoryModel>
)

data class CategoryModel(
    val name: String,
    val color: Color
)
