package com.xfinity.blueprint.architecture.component

import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.xfinity.blueprint.architecture.R
import com.xfinity.blueprint.view.ComponentView


abstract class BasicComponentView<T: BasicComponentViewHolder>: ComponentView<T> {
    abstract val basicComponentViewHolder: T
    fun setIcon(resId: Int?) {
        basicComponentViewHolder.icon?.setImageResource(resId ?: 0)
    }
    fun setComponentCtaAction(behavior: (() -> Unit)?) {
        basicComponentViewHolder.itemView.setOnClickListener {
            behavior?.invoke()
        }
    }
    /**
     * since this view has optional fields, the presenter needs to know which parts the component
     * has before it tries to present them
     */
    fun hasPart(basicComponentPart: BasicComponentPart): Boolean {
       return when (basicComponentPart) {
            BasicComponentPart.PRIMARY_LABEL -> basicComponentViewHolder.primaryLabel != null
            BasicComponentPart.SECONDARY_LABEL -> basicComponentViewHolder.secondaryLabel != null
            BasicComponentPart.TERTIARY_LABEL -> basicComponentViewHolder.tertiaryLabel != null
            BasicComponentPart.ICON -> basicComponentViewHolder.icon != null
            BasicComponentPart.PRIMARY_CTA -> basicComponentViewHolder.primaryCta != null || basicComponentViewHolder.primaryImageCta != null
            BasicComponentPart.SECONDARY_CTA -> basicComponentViewHolder.secondaryCta != null || basicComponentViewHolder.secondaryImageCta != null
            BasicComponentPart.TERTIARY_CTA -> basicComponentViewHolder.tertiaryCta != null || basicComponentViewHolder.tertiaryImageCta != null
        }
    }

    fun runWithPart(basicComponentPart: BasicComponentPart, behavior: (() -> Unit)?) {
        if (hasPart(basicComponentPart)) {
            behavior?.invoke()
        }
    }

    fun setPrimaryCtaIcon(resId: Int) {
        basicComponentViewHolder.primaryImageCta?.setImageResource(resId)
    }

    fun setPrimaryCtaAction(behavior: (() -> Unit)?) {
        basicComponentViewHolder.primaryCta?.setOnClickListener {
            behavior?.invoke()
        }

        basicComponentViewHolder.primaryImageCta?.setOnClickListener {
            behavior?.invoke()
        }
    }

    fun setSecondaryCtaIcon(resId: Int) {
        basicComponentViewHolder.secondaryImageCta?.setImageResource(resId)
    }

    fun setSecondaryCtaAction(behavior: (() -> Unit)?) {
        basicComponentViewHolder.secondaryCta?.setOnClickListener {
            behavior?.invoke()
        }

        basicComponentViewHolder.secondaryImageCta?.setOnClickListener {
            behavior?.invoke()
        }
    }

    fun setTertiaryCtaIcon(resId: Int) {
        basicComponentViewHolder.tertiaryImageCta?.setImageResource(resId)
    }

    fun setTertiaryCtaAction(behavior: (() -> Unit)?) {
        basicComponentViewHolder.tertiaryCta?.setOnClickListener {
            behavior?.invoke()
        }
        basicComponentViewHolder.tertiaryImageCta?.setOnClickListener {
            behavior?.invoke()
        }
    }

    fun makePartVisible(basicComponentPart: BasicComponentPart) {
        getPart(basicComponentPart)?.let {
            it.visibility = VISIBLE
        }
    }

    fun makePartGone(basicComponentPart: BasicComponentPart) {
        getPart(basicComponentPart)?.let {
            it.visibility = GONE
        }
    }
    fun setPartText(basicComponentPart: BasicComponentPart, labelText: String?) {
        (getPart(basicComponentPart) as? TextView)?.text = labelText
    }

    fun setPartTextBold(basicComponentPart: BasicComponentPart) {
        (getPart(basicComponentPart) as? TextView)?.setTypeface(null, Typeface.BOLD)
    }

    fun setPartTextItalic(basicComponentPart: BasicComponentPart) {
        (getPart(basicComponentPart) as? TextView)?.setTypeface(null, Typeface.ITALIC)
    }

    fun setPartTextBoldItalic(basicComponentPart: BasicComponentPart) {
        (getPart(basicComponentPart) as? TextView)?.setTypeface(null, Typeface.BOLD_ITALIC)
    }

    fun setPartTextUnderlined(basicComponentPart: BasicComponentPart) {
        (getPart(basicComponentPart) as? TextView)?.let { textView ->
            textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }
    }

    fun setPartTextColor(basicComponentPart: BasicComponentPart, @ColorRes colorResId: Int) {
        (getPart(basicComponentPart) as? TextView)?.let {
            val color = ContextCompat.getColor(it.context, colorResId)
            it.setTextColor(color)
        }
    }

    fun setPartFont(basicComponentPart: BasicComponentPart, fontFamilyPath: String) {
        (getPart(basicComponentPart) as? TextView)?.let {
            it.typeface = Typeface.createFromAsset(it.context.assets, fontFamilyPath)
        }
    }

    fun fetchImage(basicComponentPart: BasicComponentPart, imageUri: String, placeholderId: Int?) {
        (getPart(basicComponentPart) as? ImageView)?.let {
            val request = Picasso.get().load(imageUri)
            if (placeholderId != null && placeholderId != 0) {
                request.placeholder(placeholderId)
            }
            request.into(it)
        }
    }

    fun setPartImageResource(basicComponentPart: BasicComponentPart, resId: Int) {
        (getPart(basicComponentPart) as? ImageView)?.apply {
            setImageResource(resId)
        }
    }
    fun setPartIcon(basicComponentPart: BasicComponentPart, iconResource: IconResource?) {
        (getPart(basicComponentPart) as? ImageView)?.apply {
            iconResource?.resourceUri?.let {
                val request = Picasso.get().load(it)
                if (iconResource.placeholderId != null && iconResource.placeholderId != 0) {
                    request.placeholder(iconResource.placeholderId)
                }
                request.into(this)
            } ?: kotlin.run {
                setImageResource(iconResource?.resourceId ?: 0)
            }
        }
    }

    fun setPartAction(part: BasicComponentPart, behavior: (() -> Unit)?) {
        getPart(part)?.setOnClickListener {
            behavior?.invoke()
        }
    }

    fun getPart(basicComponentPart: BasicComponentPart) : View? =
        when (basicComponentPart) {
            BasicComponentPart.PRIMARY_LABEL -> basicComponentViewHolder.primaryLabel
            BasicComponentPart.SECONDARY_LABEL -> basicComponentViewHolder.secondaryLabel
            BasicComponentPart.TERTIARY_LABEL -> basicComponentViewHolder.tertiaryLabel
            BasicComponentPart.ICON -> basicComponentViewHolder.icon
            BasicComponentPart.PRIMARY_CTA -> basicComponentViewHolder.primaryCta
                ?: basicComponentViewHolder.primaryImageCta

            BasicComponentPart.SECONDARY_CTA -> basicComponentViewHolder.secondaryCta
                ?: basicComponentViewHolder.secondaryImageCta

            BasicComponentPart.TERTIARY_CTA -> basicComponentViewHolder.tertiaryCta
                ?: basicComponentViewHolder.tertiaryImageCta
        }
}

enum class BasicComponentPart {
    PRIMARY_LABEL,
    SECONDARY_LABEL,
    TERTIARY_LABEL,
    ICON,
    PRIMARY_CTA,
    SECONDARY_CTA,
    TERTIARY_CTA
}

//TODO - we could annotate this, and then auto-generate the BasicComponentView class inside blueprint
open class BasicComponentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val primaryLabel = itemView.findViewById(R.id.primaryLabel) as? TextView
    val secondaryLabel = itemView.findViewById(R.id.secondaryLabel) as? TextView
    val tertiaryLabel = itemView.findViewById(R.id.tertiaryLabel) as? TextView
    val icon = itemView.findViewById(R.id.icon) as? ImageView

    //BCVH provides three CTAs, any combination of buttons and imagebuttons. Each CTA must be
    //either a button or an imagebutton, and BCVH will figure out which one there is.  If a layout
    //specifies, for instance, a primary_cta AND a primary_image_cta, the primary_cta will be
    //preferred

    val primaryCta = itemView.findViewById(R.id.primaryCta) as? Button
    val primaryImageCta = itemView.findViewById(R.id.primaryImageCta) as? ImageButton
    val secondaryCta = itemView.findViewById(R.id.secondaryCta) as? Button
    val secondaryImageCta = itemView.findViewById(R.id.secondaryImageCta) as? ImageButton
    val tertiaryCta = itemView.findViewById(R.id.tertiaryCta) as? Button
    val tertiaryImageCta = itemView.findViewById(R.id.tertiaryImageCta) as? ImageButton
}