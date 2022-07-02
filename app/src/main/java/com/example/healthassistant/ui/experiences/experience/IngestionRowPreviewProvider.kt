package com.example.healthassistant.ui.experiences.experience

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.healthassistant.data.room.experiences.entities.Ingestion
import com.example.healthassistant.data.room.experiences.entities.Sentiment
import com.example.healthassistant.data.room.experiences.entities.SubstanceColor
import com.example.healthassistant.data.room.experiences.entities.SubstanceCompanion
import com.example.healthassistant.data.room.experiences.relations.IngestionWithCompanion
import com.example.healthassistant.data.substances.AdministrationRoute
import com.example.healthassistant.data.substances.DoseClass
import java.util.*

class IngestionRowPreviewProvider : PreviewParameterProvider<OneExperienceViewModel.IngestionElement> {
    override val values: Sequence<OneExperienceViewModel.IngestionElement> = sequenceOf(
        OneExperienceViewModel.IngestionElement(
            dateText = null,
            ingestionWithCompanion = IngestionWithCompanion(
                ingestion = Ingestion(
                    substanceName = "MDMA",
                    time = Date(),
                    administrationRoute = AdministrationRoute.ORAL,
                    dose = 90.0,
                    isDoseAnEstimate = false,
                    units = "mg",
                    experienceId = 0,
                    notes = "This is a note",
                    sentiment = Sentiment.SATISFIED
                ),
                substanceCompanion = SubstanceCompanion(
                    substanceName = "MDMA",
                    color = SubstanceColor.PINK
                )
            ),
            roaDuration = null,
            doseClass = DoseClass.COMMON
        ),
        OneExperienceViewModel.IngestionElement(
            dateText = null,
            ingestionWithCompanion = IngestionWithCompanion(
                ingestion = Ingestion(
                    substanceName = "LSD",
                    time = Date(),
                    administrationRoute = AdministrationRoute.ORAL,
                    dose = null,
                    isDoseAnEstimate = false,
                    units = "µg",
                    experienceId = 0,
                    notes = null,
                    sentiment = Sentiment.NEUTRAL
                ),
                substanceCompanion = SubstanceCompanion(
                    substanceName = "LSD",
                    color = SubstanceColor.BLUE
                )
            ),
            roaDuration = null,
            doseClass = DoseClass.COMMON
        )
    )
}