package com.isaakhanimann.healthassistant.ui.safer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.isaakhanimann.healthassistant.ui.theme.horizontalPadding
import com.isaakhanimann.healthassistant.ui.utils.JournalTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun DrugTestingScreen() {
    Scaffold(
        topBar = {
            JournalTopAppBar(title = "Drug Testing Services")
        }
    ) { padding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = horizontalPadding)
        ) {
            CardWithTesting(title = "Austria") {
                TestingServiceItem(
                    name = "Drogenarbeit Z6",
                    city = "Innsbruck",
                    url = "https://www.drogenarbeitz6.at/drug-checking.html"
                )
                Divider()
                TestingServiceItem(
                    name = "Checkit!",
                    city = "Vienna",
                    url = "https://checkit.wien/drug-checking-2/"
                )
                Divider()
                TestingServiceItem(
                    name = "Triptalks",
                    city = "Graz",
                    url = "https://triptalks.at"
                )
            }
            CardWithTesting(title = "Belgium") {
                TestingServiceItem(
                    name = "Modus Vivendi",
                    city = "Saint-Gilles",
                    url = "https://www.modusvivendi-be.org"
                )
                Divider()
                TestingServiceItem(
                    name = "Exaequo @ Rainbowhouse",
                    city = "Brussels",
                    url = "https://www.exaequo.be"
                )
            }
            CardWithTesting(title = "Canada") {
                TestingServiceItem(
                    name = "Get Your Drugs Tested",
                    city = "Vancouver",
                    url = "http://www.vch.ca/public-health/harm-reduction/overdose-prevention-response/drug-checking"
                )
            }
            CardWithTesting(title = "France") {
                TestingServiceItem(
                    name = "Asso Michel - CAARUD Médiane",
                    city = "Dunkerque",
                    url = "https://www.associationmichel.com/caarud-mediane-722/le-caarud-mediane-743/"
                )
                Divider()
                TestingServiceItem(
                    name = "Le MAS - CAARUD Pause diabolo",
                    city = "Lyon",
                    url = "https://www.mas-asso.fr/service/pause-diabolo/"
                )
                Divider()
                TestingServiceItem(
                    name = "Centre \"Les Wads\"",
                    city = "Metz",
                    url = "http://www.leswadscmsea.fr"
                )
            }
            CardWithTesting(title = "Italy") {
                TestingServiceItem(
                    name = "Neutravel Project",
                    city = "Torino",
                    url = "https://www.neutravel.net/drug-checking"
                )
            }
            CardWithTesting(title = "Netherlands") {
                TestingServiceItem(
                    name = "Drugs-test",
                    city = "33 locations",
                    url = "https://www.drugs-test.nl/en/testlocations/"
                )
            }
            CardWithTesting(title = "Spain") {
                TestingServiceItem(
                    name = "Energy Control",
                    city = "Various locations",
                    url = "https://energycontrol.org/servicio-de-analisis/"
                )
            }
            CardWithTesting(title = "Switzerland") {
                TestingServiceItem(
                    name = "DIBS / Safer Dance Basel",
                    city = "Basel",
                    url = "https://de.saferdancebasel.ch/drugchecking"
                )
                Divider()
                TestingServiceItem(
                    name = "DIB / rave it safe",
                    city = "Bern, Biel",
                    url = "https://www.raveitsafe.ch/angebotsdetails/dib-drug-checking-bern/"
                )
                Divider()
                TestingServiceItem(
                    name = "Nuit Blanche",
                    city = "Geneva",
                    url = "https://nuit-blanche.ch/drug-checking/"
                )
                Divider()
                TestingServiceItem(
                    name = "DIZ / Saferparty",
                    city = "Zurich",
                    url = "https://en.saferparty.ch/angebote/drug-checking"
                )
                Divider()
                TestingServiceItem(
                    name = "DILU Luzern",
                    city = "Luzern",
                    url = "https://www.gassenarbeit.ch/angebote/dilu"
                )
            }
            CardWithTesting(title = "United Kingdom") {
                TestingServiceItem(
                    name = "The Loop",
                    city = "Bristol",
                    url = "https://wearetheloop.org"
                )
            }
            val uriHandler = LocalUriHandler.current
            TextButton(onClick = {
                uriHandler.openUri("https://t.me/isaakhanimann")
            }) {
                Text(
                    "Report missing service",
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Composable
fun CardWithTesting(
    title: String,
    content: @Composable () -> Unit
) {
    Card(modifier = Modifier.padding(vertical = 5.dp)) {
        Column(
            Modifier.padding(vertical = 5.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Divider()
            content()
        }
    }
}

@Preview
@Composable
fun TestingServiceItemPreview() {
    TestingServiceItem(
        name = "DIZ / Saferparty",
        city = "Zurich",
        url = "https://en.saferparty.ch/angebote/drug-checking"
    )
}

@Composable
fun TestingServiceItem(
    name: String,
    city: String,
    url: String
) {
    val uriHandler = LocalUriHandler.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .clickable {
                uriHandler.openUri(url)
            }
            .padding(horizontal = horizontalPadding, vertical = 5.dp)
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.OpenInBrowser,
            contentDescription = "Open Link"
        )
        Text(
            text = name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = city,
            maxLines = 1,
            modifier = Modifier.width(IntrinsicSize.Max)
        )
    }
}

