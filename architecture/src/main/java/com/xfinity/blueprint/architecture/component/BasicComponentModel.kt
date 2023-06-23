package com.xfinity.blueprint.architecture.component

import com.xfinity.blueprint.architecture.component.Cta.Companion.PRIMARY_CTA_ID
import com.xfinity.blueprint.architecture.component.Cta.Companion.SECONDARY_CTA_ID
import com.xfinity.blueprint.architecture.component.Cta.Companion.TERTIARY_CTA_ID
import com.xfinity.blueprint.model.ComponentModel

data class BasicComponentModel(val primaryLabel: String? = null, val secondaryLabel: String? = null,
                               val tertiaryLabel: String? = null,
                               val iconResource: IconResource? = null,
                               val componentCta: Cta? = null, val ctas: List<Cta>? = null) :
    ComponentModel

data class Cta(val id: String, val label: String? = null, val iconResource: IconResource? = null,
               val behavior: (() -> Unit)? = null) {

    companion object {
        const val COMPONENT_CTA = "componentCta"
        const val PRIMARY_CTA_ID = "primary"
        const val SECONDARY_CTA_ID = "secondary"
        const val TERTIARY_CTA_ID = "tertiary"
    }
}

data class IconResource(val resourceId: Int, val resourceUri: String? = null,
                        val placeholderId: Int? = null)

fun List<Cta>.getPrimary() = find {
    it.id == PRIMARY_CTA_ID
}

fun List<Cta>.getSecondary() = find {
    it.id == SECONDARY_CTA_ID
}

fun List<Cta>.getTertiary() = find {
    it.id == TERTIARY_CTA_ID
}