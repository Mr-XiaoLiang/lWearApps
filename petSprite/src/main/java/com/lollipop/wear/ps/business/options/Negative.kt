package com.lollipop.wear.ps.business.options

import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.type.Antibiotic
import com.lollipop.wear.ps.engine.state.type.Food
import com.lollipop.wear.ps.engine.state.type.Toy

object Negative : OptionList {

    override val options: Array<GameOption> = arrayOf(
        *Disease.options
    )

    object Disease : OptionList {

        abstract class DiseaseOption(
            override val key: String,
            override val name: Int,
            override val antibody: Int,
            override val dopamine: Int,
            override val kcal: Int
        ) : GameOption, Antibiotic, Toy, Food

        object Cold : DiseaseOption(
            "negative_disease_cold",
            R.string.label_negative_disease_cold,
            -10,
            -10,
            -10
        )

        object Fever : DiseaseOption(
            "negative_disease_fever",
            R.string.label_negative_disease_fever,
            -20,
            -60,
            -50
        )

        object Diarrhea : DiseaseOption(
            "negative_disease_diarrhea",
            R.string.label_negative_disease_diarrhea,
            -20,
            -50,
            -70
        )


        override val options: Array<GameOption> = arrayOf(
            Cold,
            Fever,
            Diarrhea
        )

    }

}