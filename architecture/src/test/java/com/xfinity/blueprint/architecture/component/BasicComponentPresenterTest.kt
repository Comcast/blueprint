package com.xfinity.blueprint.architecture.component

import junit.framework.Assert.assertEquals
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
        whenever(model.primaryLabel).thenReturn(title)

        presenter.present(view, model)

        verify(view, times(1)).setPartText(partCaptor.capture(), titleCaptor.capture())
        assertEquals(title, titleCaptor.firstValue)
        assertEquals(BasicComponentPart.PRIMARY_LABEL, partCaptor.firstValue)
    }

    @Test
    fun `verify optional text is presented correctly when text it not null`() {
        //given
        val titles = listOf("title0", "title1", "title2")
        val partList = listOf(BasicComponentPart.PRIMARY_LABEL, BasicComponentPart.SECONDARY_LABEL,
            BasicComponentPart.TERTIARY_LABEL)

        val titleCaptor = argumentCaptor<String>()
        val partCaptor = argumentCaptor<BasicComponentPart>()

        partList.forEachIndexed { index, basicComponentPart ->
            presenter.presentOptionalText(view, basicComponentPart, titles[index])
        }

        verify(view, times(3)).setPartText(partCaptor.capture(), titleCaptor.capture())
        verify(view, times(3)).makePartVisible(partCaptor.capture())
        verify(view, never()).makePartGone(any())

        assertEquals(titles[0], titleCaptor.allValues[0])
        assertEquals(titles[1], titleCaptor.allValues[1])
        assertEquals(titles[2], titleCaptor.allValues[2])

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
    fun `verify optional CTA is presented correctly when it is not null`() {
        //given
        val ctas = listOf(getFakeCta(0), getFakeCta(1), getFakeCta(2))
        val partList = listOf(BasicComponentPart.PRIMARY_CTA, BasicComponentPart.SECONDARY_CTA,
            BasicComponentPart.TERTIARY_CTA)

        val ctaBehaviorCaptor = argumentCaptor<() -> Unit>()
        val ctaLabelCaptor = argumentCaptor<String>()
        val ctaIconCaptor = argumentCaptor<IconResource>()
        val partCaptor = argumentCaptor<BasicComponentPart>()
        val viewCaptor = argumentCaptor<BasicComponentView<BasicComponentViewHolder>>()

        val presenterSpy = spy(presenter)
        //when
        partList.forEachIndexed { index, basicComponentPart ->
            presenterSpy.presentOptionalCta(view, basicComponentPart, ctas[index])
        }

        verify(view, times(3)).setPartText(partCaptor.capture(), ctaLabelCaptor.capture())
        verify(presenterSpy, times(3)).presentOptionalIcon(viewCaptor.capture(), partCaptor.capture(), ctaIconCaptor.capture())
        verify(view, times(3)).makePartVisible(partCaptor.capture())
        verify(view, times(3)).setPartAction(partCaptor.capture(), ctaBehaviorCaptor.capture())
        verify(view, never()).makePartGone(any())

        assertEquals(ctas[0].label, ctaLabelCaptor.allValues[0])
        assertEquals(ctas[1].label, ctaLabelCaptor.allValues[1])
        assertEquals(ctas[2].label, ctaLabelCaptor.allValues[2])

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
        return Cta(id.toString(), "ctaLabel$id", IconResource(id))
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