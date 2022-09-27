package com.isaakhanimann.healthassistant.ui.addingestion.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.isaakhanimann.healthassistant.data.room.experiences.entities.SubstanceColor
import com.isaakhanimann.healthassistant.data.substances.AdministrationRoute
import com.isaakhanimann.healthassistant.ui.journal.SectionTitle
import com.isaakhanimann.healthassistant.ui.search.SearchScreen
import com.isaakhanimann.healthassistant.ui.search.substance.roa.toReadableString

@Composable
fun AddIngestionSearchScreen(
    navigateToCheckInteractionsSkipNothing: (substanceName: String) -> Unit,
    navigateToCheckInteractionsSkipRoute: (substanceName: String, route: AdministrationRoute) -> Unit,
    navigateToCheckInteractionsSkipDose: (substanceName: String, route: AdministrationRoute, dose: Double?, units: String?, isEstimate: Boolean) -> Unit,
    navigateToCustomSubstanceChooseRoute: (substanceName: String) -> Unit,
    viewModel: AddIngestionSearchViewModel = hiltViewModel()
) {
    AddIngestionSearchScreen(
        navigateToCheckInteractionsSkipNothing = navigateToCheckInteractionsSkipNothing,
        searchModel = viewModel.modelFlow.collectAsState().value,
        navigateToCheckInteractionsSkipRoute = navigateToCheckInteractionsSkipRoute,
        navigateToCheckInteractionsSkipDose = navigateToCheckInteractionsSkipDose,
        navigateToCustomSubstanceChooseRoute = navigateToCustomSubstanceChooseRoute
    )
}

@Composable
fun AddIngestionSearchScreen(
    navigateToCheckInteractionsSkipNothing: (substanceName: String) -> Unit,
    navigateToCheckInteractionsSkipRoute: (substanceName: String, route: AdministrationRoute) -> Unit,
    navigateToCheckInteractionsSkipDose: (substanceName: String, route: AdministrationRoute, dose: Double?, units: String?, isEstimate: Boolean) -> Unit,
    navigateToCustomSubstanceChooseRoute: (substanceName: String) -> Unit,
    searchModel: AddIngestionSearchModel
) {
    Column {
        LinearProgressIndicator(progress = 0.17f, modifier = Modifier.fillMaxWidth())
        SearchScreen(
            onSubstanceTap = {
                navigateToCheckInteractionsSkipNothing(it)
            },
            modifier = Modifier.weight(1f),
            isShowingSettings = false,
            navigateToSettings = {},
            navigateToAddCustomSubstanceScreen = {},
            onCustomSubstanceTap = navigateToCustomSubstanceChooseRoute,
        )
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
        if (searchModel.doseSuggestions.isNotEmpty() || searchModel.routeSuggestions.isNotEmpty()) {
            SuggestionsSection(
                searchModel = searchModel,
                modifier = Modifier.heightIn(0.dp, screenHeight / 2),
                navigateToCheckInteractionsSkipRoute = navigateToCheckInteractionsSkipRoute,
                navigateToCheckInteractionsSkipDose = navigateToCheckInteractionsSkipDose
            )
        }
    }
}

@Preview
@Composable
fun SuggestionSectionPreview(@PreviewParameter(AddIngestionSearchScreenProvider::class) addIngestionSearchModel: AddIngestionSearchModel) {
    Scaffold {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                color = Color.Blue, modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(text = "Search Screen")
            }
            val screenHeight = LocalConfiguration.current.screenHeightDp.dp
            SuggestionsSection(
                searchModel = addIngestionSearchModel,
                navigateToCheckInteractionsSkipDose = { _: String, _: AdministrationRoute, _: Double?, _: String?, _: Boolean -> },
                navigateToCheckInteractionsSkipRoute = { _: String, _: AdministrationRoute -> },
                modifier = Modifier.heightIn(0.dp, screenHeight / 2)
            )
        }
    }

}

@Composable
fun SuggestionsSection(
    searchModel: AddIngestionSearchModel,
    navigateToCheckInteractionsSkipRoute: (substanceName: String, route: AdministrationRoute) -> Unit,
    navigateToCheckInteractionsSkipDose: (substanceName: String, route: AdministrationRoute, dose: Double?, units: String?, isEstimate: Boolean) -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        SectionTitle(title = "Quick Logging")
        LazyColumn {
            items(searchModel.routeSuggestions) {
                RouteSuggestion(routeSuggestionElement = it, onTap = {
                    navigateToCheckInteractionsSkipRoute(it.substanceName, it.administrationRoute)
                })
                Divider()
            }
            items(searchModel.doseSuggestions) {
                DoseSuggestion(doseSuggestionElement = it, onTap = {
                    navigateToCheckInteractionsSkipDose(
                        it.substanceName,
                        it.administrationRoute,
                        it.dose,
                        it.units,
                        it.isDoseAnEstimate
                    )
                })
                Divider()
            }
        }
    }

}

@Composable
fun RouteSuggestion(routeSuggestionElement: RouteSuggestionElement, onTap: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onTap)
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 5.dp)
    ) {
        ColorCircle(substanceColor = routeSuggestionElement.color)
        Text(
            text = routeSuggestionElement.substanceName + " " + routeSuggestionElement.administrationRoute.displayText,
        )
    }
}

@Composable
fun DoseSuggestion(doseSuggestionElement: DoseSuggestionElement, onTap: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onTap)
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 5.dp)
    ) {
        ColorCircle(substanceColor = doseSuggestionElement.color)
        Text(
            text = "${doseSuggestionElement.substanceName} ${doseSuggestionElement.administrationRoute.displayText}",
        )
        Spacer(modifier = Modifier.weight(1f))
        if (doseSuggestionElement.dose != null) {
            val isEstimateText = if (doseSuggestionElement.isDoseAnEstimate) "~" else ""
            val doseText = doseSuggestionElement.dose.toReadableString()
            Text(
                text = "$isEstimateText$doseText ${doseSuggestionElement.units}",
            )
        } else {
            Text(
                text = "Unknown Dose",
            )
        }
    }
}

@Composable
fun ColorCircle(substanceColor: SubstanceColor) {
    val isDarkTheme = isSystemInDarkTheme()
    Surface(
        shape = CircleShape,
        color = substanceColor.getComposeColor(isDarkTheme),
        modifier = Modifier
            .size(25.dp)
    ) {}
}