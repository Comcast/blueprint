package com.xfinity.blueprint.architecture.component

import androidx.annotation.VisibleForTesting
import com.xfinity.blueprint.presenter.ComponentPresenter

open class BasicComponentPresenter<T: BasicComponentView<*>>:
    ComponentPresenter<T, BasicComponentModel> {
    override fun present(view: T, model: BasicComponentModel) {
        presentOptionalText(view, BasicComponentPart.PRIMARY_LABEL, model.primaryLabel)
        presentOptionalText(view, BasicComponentPart.SECONDARY_LABEL, model.secondaryLabel)
        presentOptionalText(view, BasicComponentPart.TERTIARY_LABEL, model.tertiaryLabel)

        view.setComponentCtaAction(model.componentCta?.behavior)

        presentOptionalIcon(view, BasicComponentPart.ICON, model.iconResource)

        presentOptionalCta(view, BasicComponentPart.PRIMARY_CTA, model.ctas?.getPrimary())
        presentOptionalCta(view, BasicComponentPart.SECONDARY_CTA, model.ctas?.getSecondary())
        presentOptionalCta(view, BasicComponentPart.TERTIARY_CTA, model.ctas?.getTertiary())
    }

    @VisibleForTesting
    fun presentOptionalText(view: T, part: BasicComponentPart, label: Label?) {
        label?.let {
            view.setPartText(part, it.text)
            it.fontColor?.let { colorId ->
                view.setPartTextColor(part, colorId)
            }

            if (it.fontStyles?.isNotEmpty() == true) {
                setPartTextStyles(view, part, it.fontStyles)
            }

            it.fontFilePath?.let { fontFilePath ->
                view.setPartFont(part, fontFilePath)
            }
            view.makePartVisible(part)
        } ?: kotlin.run { view.makePartGone(part) }
    }

    @VisibleForTesting
    fun setPartTextStyles(view: T, part: BasicComponentPart, fontStyles: List<Label.FontStyle>) {
        if (fontStyles.contains(Label.FontStyle.UNDERLINED)) {
            view.setPartTextUnderlined(part)
        }

        if (fontStyles.contains(Label.FontStyle.BOLD) && fontStyles.contains(
                Label.FontStyle.ITALIC)) {
            view.setPartTextBoldItalic(part)
        } else {
            if (fontStyles.contains(Label.FontStyle.BOLD)) {
                view.setPartTextBold(part)
            }
            if (fontStyles.contains(Label.FontStyle.ITALIC)) {
                view.setPartTextItalic(part)
            }
        }
    }

    @VisibleForTesting
    fun presentOptionalCta(view: T, part: BasicComponentPart, cta: Cta?) {
        cta?.let {
            presentCta(view, part, it)
        } ?: view.makePartGone(part)
    }

    @VisibleForTesting
    fun presentCta(view: T, part: BasicComponentPart, cta: Cta) {
        presentOptionalIcon(view, part, cta.iconResource)
        cta.label?.let { label ->
            presentOptionalText(view, part, label)
        }
        view.makePartVisible(part)
        view.setPartAction(part, cta.behavior)
    }

    @VisibleForTesting
    fun presentOptionalIcon(view: T, part: BasicComponentPart, iconResource: IconResource?) {
        if (view.hasPart(part) && iconResource != null) {
            view.makePartVisible(part)
            iconResource.resourceUri?.let {
                view.fetchImage(part, it, iconResource.placeholderId)
            } ?: kotlin.run {
                view.setPartImageResource(part,iconResource.resourceId)
            }
        } else if (view.hasPart(part) && iconResource == null) {
            view.makePartGone(part)
        }
    }
}