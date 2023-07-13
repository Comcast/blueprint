package com.xfinity.blueprint.architecture.component

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class BasicComponentPresenterTest {
    @Mock lateinit var model: BasicComponentModel
    @Mock lateinit var view: BasicComponentView<BasicComponentViewHolder>
    @Mock lateinit var presenter: BasicComponentPresenter<BasicComponentView<BasicComponentViewHolder>>

    @Before
    fun setup() {
        presenter = BasicComponentPresenter()
    }

    @Test
    fun `verify title is set correctly`() {
        //given
        val title = "title"
        val titleCaptor = argumentCaptor<String>()
        val partCaptor = argumentCaptor<BasicComponentPart>()

        //when
        whenever(model.primaryLabel).thenReturn(Label(title))

        presenter.present(view, model)

        verify(view, times(1)).setPartText(partCaptor.capture(), titleCaptor.capture())
        assertEquals(title, titleCaptor.firstValue)
        assertEquals(BasicComponentPart.PRIMARY_LABEL, partCaptor.firstValue)
    }

    @Test
    fun `verify optional text is presented correctly when text it not null`() {
        //given
        val labels = listOf(Label("title0", 0, fontFilePath = "fontPath0"),
            Label("title1", 1, fontFilePath = "fontPath"),
            Label("title2", 2, fontFilePath = "fontPath"))
        val partList = listOf(BasicComponentPart.PRIMARY_LABEL, BasicComponentPart.SECONDARY_LABEL,
            BasicComponentPart.TERTIARY_LABEL)

        val titleCaptor = argumentCaptor<String>()
        val partCaptor = argumentCaptor<BasicComponentPart>()
        val colorCaptor = argumentCaptor<Int>()
        val fontPathCaptor = argumentCaptor<String>()

        partList.forEachIndexed { index, basicComponentPart ->
            presenter.presentOptionalText(view, basicComponentPart, labels[index])
        }

        verify(view, times(3)).setPartText(partCaptor.capture(), titleCaptor.capture())
        verify(view, times(3)).makePartVisible(partCaptor.capture())
        verify(view, times(3)).setPartTextColor(partCaptor.capture(), colorCaptor.capture())
        verify(view, times(3)).setPartFont(partCaptor.capture(), fontPathCaptor.capture())
        verify(view, never()).makePartGone(any())

        assertEquals(labels[0].text, titleCaptor.allValues[0])
        assertEquals(labels[1].text, titleCaptor.allValues[1])
        assertEquals(labels[2].text, titleCaptor.allValues[2])

        assertEquals(labels[0].fontColor, colorCaptor.allValues[0])
        assertEquals(labels[1].fontColor, colorCaptor.allValues[1])
        assertEquals(labels[2].fontColor, colorCaptor.allValues[2])

        assertEquals(labels[0].fontFilePath, fontPathCaptor.allValues[0])
        assertEquals(labels[1].fontFilePath, fontPathCaptor.allValues[1])
        assertEquals(labels[2].fontFilePath, fontPathCaptor.allValues[2])

        assertEquals(partList[0], partCaptor.allValues[0])
        assertEquals(partList[1], partCaptor.allValues[1])
        assertEquals(partList[2], partCaptor.allValues[2])
    }

    @Test
    fun `verify optional text is presented correctly when text is null`() {
        //given
        val partList = listOf(BasicComponentPart.PRIMARY_LABEL, BasicComponentPart.SECONDARY_LABEL,
            BasicComponentPart.TERTIARY_LABEL)
        val partCaptor = argumentCaptor<BasicComponentPart>()

        //when
        partList.forEach { basicComponentPart ->
            presenter.presentOptionalText(view, basicComponentPart, null)
        }

        verify(view, never()).setPartText(any(), any())
        verify(view, never()).makePartVisible(any())
        verify(view, times(3)).makePartGone(partCaptor.capture())

        assertEquals(partList[0], partCaptor.allValues[0])
        assertEquals(partList[1], partCaptor.allValues[1])
        assertEquals(partList[2], partCaptor.allValues[2])
    }

    @Test
    fun `verify bold text style is presented correctly`() {
        val fontStyles = listOf(Label.FontStyle.BOLD)
        val partCaptor = argumentCaptor<BasicComponentPart>()
        presenter.setPartTextStyles(view, BasicComponentPart.PRIMARY_LABEL, fontStyles)

        verify(view, times(1)).setPartTextBold(partCaptor.capture())
        verify(view, never()).setPartTextBoldItalic(any())
        verify(view, never()).setPartTextItalic(any())
        verify(view, never()).setPartTextUnderlined(any())
    }

    @Test
    fun `verify italic text style is presented correctly`() {
        val fontStyles = listOf(Label.FontStyle.ITALIC)
        val partCaptor = argumentCaptor<BasicComponentPart>()
        presenter.setPartTextStyles(view, BasicComponentPart.PRIMARY_LABEL, fontStyles)

        verify(view, never()).setPartTextBold(partCaptor.capture())
        verify(view, never()).setPartTextBoldItalic(any())
        verify(view, times(1)).setPartTextItalic(any())
        verify(view, never()).setPartTextUnderlined(any())
    }
    @Test
    fun `verify bold italic text style is presented correctly`() {
        val fontStyles = listOf(Label.FontStyle.BOLD, Label.FontStyle.ITALIC)
        val partCaptor = argumentCaptor<BasicComponentPart>()
        presenter.setPartTextStyles(view, BasicComponentPart.PRIMARY_LABEL, fontStyles)

        verify(view, never()).setPartTextBold(partCaptor.capture())
        verify(view, times(1)).setPartTextBoldItalic(any())
        verify(view, never()).setPartTextItalic(any())
        verify(view, never()).setPartTextUnderlined(any())
    }

    @Test
    fun `verify underlined text style is presented correctly`() {
        val fontStyles = listOf(Label.FontStyle.UNDERLINED)
        val partCaptor = argumentCaptor<BasicComponentPart>()
        presenter.setPartTextStyles(view, BasicComponentPart.PRIMARY_LABEL, fontStyles)

        verify(view, never()).setPartTextBold(partCaptor.capture())
        verify(view, never()).setPartTextBoldItalic(any())
        verify(view, never()).setPartTextItalic(any())
        verify(view, times(1)).setPartTextUnderlined(any())
    }

    @Test
    fun `verify bold underlined text style is presented correctly`() {
        val fontStyles = listOf(Label.FontStyle.BOLD, Label.FontStyle.UNDERLINED)
        val partCaptor = argumentCaptor<BasicComponentPart>()
        presenter.setPartTextStyles(view, BasicComponentPart.PRIMARY_LABEL, fontStyles)

        verify(view, times(1)).setPartTextBold(partCaptor.capture())
        verify(view, never()).setPartTextBoldItalic(any())
        verify(view, never()).setPartTextItalic(any())
        verify(view, times(1)).setPartTextUnderlined(any())
    }

    @Test
    fun `verify italic underlined text style is presented correctly`() {
        val fontStyles = listOf(Label.FontStyle.ITALIC, Label.FontStyle.UNDERLINED)
        val partCaptor = argumentCaptor<BasicComponentPart>()
        presenter.setPartTextStyles(view, BasicComponentPart.PRIMARY_LABEL, fontStyles)

        verify(view, never()).setPartTextBold(partCaptor.capture())
        verify(view, never()).setPartTextBoldItalic(any())
        verify(view, times(1)).setPartTextItalic(any())
        verify(view, times(1)).setPartTextUnderlined(any())
    }

    @Test
    fun `verify bold italic underlined text style is presented correctly`() {
        val fontStyles = listOf(Label.FontStyle.BOLD, Label.FontStyle.ITALIC, Label.FontStyle.UNDERLINED)
        val partCaptor = argumentCaptor<BasicComponentPart>()
        presenter.setPartTextStyles(view, BasicComponentPart.PRIMARY_LABEL, fontStyles)

        verify(view, never()).setPartTextBold(partCaptor.capture())
        verify(view, times(1)).setPartTextBoldItalic(any())
        verify(view, never()).setPartTextItalic(any())
        verify(view, times(1)).setPartTextUnderlined(any())
    }

    @Test
    fun `verify optional CTA is presented correctly when it is not null`() {
        //given
        val ctas = listOf(getFakeCta(0), getFakeCta(1), getFakeCta(2))
        val partList = listOf(BasicComponentPart.PRIMARY_CTA, BasicComponentPart.SECONDARY_CTA,
            BasicComponentPart.TERTIARY_CTA)

        val ctaBehaviorCaptor = argumentCaptor<() -> Unit>()
        val ctaLabelCaptor = argumentCaptor<Label>()
        val ctaIconCaptor = argumentCaptor<IconResource>()
        val partCaptor = argumentCaptor<BasicComponentPart>()
        val viewCaptor = argumentCaptor<BasicComponentView<BasicComponentViewHolder>>()

        val presenterSpy = spy(presenter)
        //when
        partList.forEachIndexed { index, basicComponentPart ->
            presenterSpy.presentOptionalCta(view, basicComponentPart, ctas[index])
        }

        verify(presenterSpy, times(3)).presentOptionalText(viewCaptor.capture(),
            partCaptor.capture(), ctaLabelCaptor.capture())
        verify(presenterSpy, times(3)).presentOptionalIcon(viewCaptor.capture(),
            partCaptor.capture(), ctaIconCaptor.capture())

        //the cta is made visible if it is not null, but presenting the label also makes it visible, so we actually wind up calling it
        //6 times instead of 3. There's no noticeable consequence for this, so it's fine.
        verify(view, times(6)).makePartVisible(partCaptor.capture())
        verify(view, times(3)).setPartAction(partCaptor.capture(), ctaBehaviorCaptor.capture())
        verify(view, never()).makePartGone(any())

        //we test the rest of the label presentation in other tests, no need to repeat that here.
        assertEquals(ctas[0].label?.text, ctaLabelCaptor.allValues[0].text)
        assertEquals(ctas[1].label?.text, ctaLabelCaptor.allValues[1].text)
        assertEquals(ctas[2].label?.text, ctaLabelCaptor.allValues[2].text)

        assertEquals(ctas[0].iconResource, ctaIconCaptor.allValues[0])
        assertEquals(ctas[1].iconResource, ctaIconCaptor.allValues[1])
        assertEquals(ctas[2].iconResource, ctaIconCaptor.allValues[2])

        //TODO - test actions
    }

    @Test
    fun `verify optional CTA is presented correctly when it is null`() {
        //given
        val partList = listOf(BasicComponentPart.PRIMARY_CTA, BasicComponentPart.SECONDARY_CTA,
            BasicComponentPart.TERTIARY_CTA)

        val partCaptor = argumentCaptor<BasicComponentPart>()

        //when
        partList.forEach { basicComponentPart ->
            presenter.presentOptionalCta(view, basicComponentPart, null)
        }

        verify(view, never()).setPartText(any(), any())
        verify(view, never()).setPartIcon(any(), any())
        verify(view, never()).makePartVisible(any())
        verify(view, never()).setPartAction(any(), any())
        verify(view, times(3)).makePartGone(partCaptor.capture())


        assertEquals(partList[0], partCaptor.allValues[0])
        assertEquals(partList[1], partCaptor.allValues[1])
        assertEquals(partList[2], partCaptor.allValues[2])

        //TODO - test actions
    }

    @Test
    fun `verify optional icon is presented correctly for resource id`() {
        //given
        val partList = listOf(BasicComponentPart.ICON, BasicComponentPart.PRIMARY_CTA,
            BasicComponentPart.SECONDARY_CTA, BasicComponentPart.TERTIARY_CTA)

        val partCaptor = argumentCaptor<BasicComponentPart>()
        val iconResIdCaptor = argumentCaptor<Int>()

        //given
        whenever(view.hasPart(any())).thenReturn(true)
        val imageResIdList = listOf(IconResource(1000), IconResource(2000),
            IconResource(3000), IconResource(4000))

        partList.forEachIndexed { index, basicComponentPart ->
            val iconResource = imageResIdList[index]
            presenter.presentOptionalIcon(view, basicComponentPart, iconResource)
        }

        verify(view, never()).fetchImage(any(), any(), any())
        verify(view, never()).makePartGone(any())

        verify(view, times(4)).makePartVisible(any())
        verify(view, times(4)).setPartImageResource(partCaptor.capture(), iconResIdCaptor.capture())

        assertEquals(partList[0], partCaptor.allValues[0])
        assertEquals(partList[1], partCaptor.allValues[1])
        assertEquals(partList[2], partCaptor.allValues[2])
        assertEquals(partList[3], partCaptor.allValues[3])

        assertEquals(imageResIdList[0].resourceId, iconResIdCaptor.allValues[0])
        assertEquals(imageResIdList[1].resourceId, iconResIdCaptor.allValues[1])
        assertEquals(imageResIdList[2].resourceId, iconResIdCaptor.allValues[2])
        assertEquals(imageResIdList[3].resourceId, iconResIdCaptor.allValues[3])
    }

    @Test
    fun `verify optional icon is presented correctly for image uri`() {
        //given
        val partList = listOf(BasicComponentPart.ICON, BasicComponentPart.PRIMARY_CTA,
            BasicComponentPart.SECONDARY_CTA, BasicComponentPart.TERTIARY_CTA)

        val partCaptor = argumentCaptor<BasicComponentPart>()
        val imageUriCaptor = argumentCaptor<String>()
        val placeholderIdCaptor = argumentCaptor<Int>()

        //given
        whenever(view.hasPart(any())).thenReturn(true)

        val iconResourceList = listOf(IconResource(1000, resourceUri = "uri1"), IconResource(2000, resourceUri = "uri1"),
            IconResource(3000, resourceUri = "uri1"), IconResource(4000, resourceUri = "uri1"))

        partList.forEachIndexed { index, basicComponentPart ->
            val iconResource = iconResourceList[index]
            presenter.presentOptionalIcon(view, basicComponentPart, iconResource)
        }

        verify(view, never()).setPartImageResource(any(), any())
        verify(view, never()).makePartGone(any())

        verify(view, times(4)).makePartVisible(any())
        verify(view, times(4)).fetchImage(partCaptor.capture(), imageUriCaptor.capture(),
            placeholderIdCaptor.capture())

        assertEquals(partList[0], partCaptor.allValues[0])
        assertEquals(partList[1], partCaptor.allValues[1])
        assertEquals(partList[2], partCaptor.allValues[2])
        assertEquals(partList[3], partCaptor.allValues[3])

        assertEquals(iconResourceList[0].resourceUri, imageUriCaptor.allValues[0])
        assertEquals(iconResourceList[1].resourceUri, imageUriCaptor.allValues[1])
        assertEquals(iconResourceList[2].resourceUri, imageUriCaptor.allValues[2])
        assertEquals(iconResourceList[3].resourceUri, imageUriCaptor.allValues[3])

        assertEquals(iconResourceList[0].placeholderId, placeholderIdCaptor.allValues[0])
        assertEquals(iconResourceList[1].placeholderId, placeholderIdCaptor.allValues[1])
        assertEquals(iconResourceList[2].placeholderId, placeholderIdCaptor.allValues[2])
        assertEquals(iconResourceList[3].placeholderId, placeholderIdCaptor.allValues[3])
    }
    private fun getFakeCta(id: Int): Cta {
        return Cta(id.toString(), Label("ctaLabel$id", fontColor = id), IconResource(id))
    }
}

fun validateBasicComponentModel(expected: BasicComponentModel, actual: BasicComponentModel) {
    assertEquals(expected.iconResource, actual.iconResource)
    assertEquals(expected.primaryLabel, actual.primaryLabel)
    assertEquals(expected.secondaryLabel, actual.secondaryLabel)
    assertEquals(expected.tertiaryLabel, actual.tertiaryLabel)

    val expectedCtas = mutableListOf(expected.componentCta)
    expected.ctas?.let { expectedCtas.addAll(it) }
    val actualCtas = mutableListOf(actual.componentCta)
    actual.ctas?.let { actualCtas.addAll(it) }
    expectedCtas.forEachIndexed { index, expectedCta ->
        validateCta(expectedCta, actualCtas[index])
    }
}

fun validateCta(expected: Cta?, actual: Cta?) {
    assertEquals(expected?.label, actual?.label)
    assertEquals(expected?.iconResource, actual?.iconResource)
    //TODO - validate behaviors
}