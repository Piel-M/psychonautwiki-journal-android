package com.isaakhanimann.healthassistant.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.isaakhanimann.healthassistant.ui.search.substancerow.SubstanceRow
import com.isaakhanimann.healthassistant.ui.theme.horizontalPadding

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel = hiltViewModel(),
    onSubstanceTap: (substanceName: String) -> Unit,
    onCustomSubstanceTap: (substanceName: String) -> Unit,
    navigateToAddCustomSubstanceScreen: () -> Unit,
) {
    Column(modifier = modifier) {
        SearchField(
            searchText = searchViewModel.searchTextFlow.collectAsState().value,
            onChange = {
                searchViewModel.filterSubstances(searchText = it)
            },
            categories = searchViewModel.chipCategoriesFlow.collectAsState().value,
            onFilterTapped = searchViewModel::onFilterTapped
        )
        val activeFilters = searchViewModel.chipCategoriesFlow.collectAsState().value.filter { it.isActive }
        if (activeFilters.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.width(4.dp))
                }
                items(activeFilters.size) {
                    val categoryChipModel = activeFilters[it]
                    CategoryChipDelete(categoryChipModel = categoryChipModel) {
                        searchViewModel.onFilterTapped(filterName = categoryChipModel.chipName)
                    }
                }
                item {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
        val isShowingCustomSubstances =
            searchViewModel.isShowingCustomSubstancesFlow.collectAsState().value
        if (isShowingCustomSubstances) {
            val customSubstances = searchViewModel.customSubstancesFlow.collectAsState().value
            LazyColumn {
                items(customSubstances) { customSubstance ->
                    Text(
                        text = customSubstance.name,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCustomSubstanceTap(customSubstance.name)
                            }
                            .padding(horizontal = horizontalPadding, vertical = 6.dp),
                    )
                    Divider()
                }
                item {
                    TextButton(
                        onClick = navigateToAddCustomSubstanceScreen,
                        modifier = Modifier.padding(horizontal = horizontalPadding)
                    ) {
                        Text(text = "Add Custom Substance")
                    }
                }
            }
        } else {
            val filteredSubstances = searchViewModel.filteredSubstances.collectAsState().value
            if (filteredSubstances.isEmpty()) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    val activeCategoryNames = activeFilters.filter { it.isActive }.map { it.chipName }
                    if (activeCategoryNames.isEmpty()) {
                        Text("None found", modifier = Modifier.padding(10.dp))

                    } else {
                        val names = activeCategoryNames.joinToString(separator = ", ")
                        Text("None found in $names", modifier = Modifier.padding(10.dp))
                    }
                }
            } else {
                LazyColumn {
                    items(filteredSubstances.size) { i ->
                        val substance = filteredSubstances[i]
                        SubstanceRow(substanceModel = substance, onTap = onSubstanceTap)
                        if (i < filteredSubstances.size) {
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchField(
    searchText: String,
    onChange: (searchText: String) -> Unit,
    categories: List<CategoryChipModel>,
    onFilterTapped: (filterName: String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    TextField(
        value = searchText,
        onValueChange = { value ->
            onChange(value)
        },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Search Substances") },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
            )
        },
        trailingIcon = {
            if (searchText != "") {
                IconButton(
                    onClick = {
                        onChange("")
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                    )
                }
            } else {
                var isExpanded by remember { mutableStateOf(false) }
                val activeFilters = categories.filter { it.isActive }
                IconButton(
                    onClick = { isExpanded = true },
                ) {
                    BadgedBox(
                        badge = {
                            if (activeFilters.isNotEmpty()) {
                                Badge { Text(activeFilters.size.toString()) }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                }
                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    categories.forEach { categoryChipModel ->
                        DropdownMenuItem(
                            onClick = {
                                onFilterTapped(categoryChipModel.chipName)
                            },
                        ) {
                            if (categoryChipModel.isActive) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = "Check",
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                            } else {
                                Spacer(Modifier.size(ButtonDefaults.IconSize))
                            }
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text(categoryChipModel.chipName)
                        }
                    }
                }
            }
        },
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done,
            capitalization = KeyboardCapitalization.Words
        ),
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}