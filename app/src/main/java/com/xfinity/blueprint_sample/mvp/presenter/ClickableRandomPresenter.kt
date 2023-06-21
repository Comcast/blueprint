package com.xfinity.blueprint_sample.mvp.presenter

import com.xfinity.blueprint.presenter.ComponentPresenter
import com.xfinity.blueprint_annotations.DefaultPresenter
import com.xfinity.blueprint_sample.R
import com.xfinity.blueprint_sample.mvp.model.ClickableRandomModel
import com.xfinity.blueprint_sample.mvp.view.ClickableRandomView
import com.xfinity.blueprint_sample.mvp.view.ClickableRandomViewHolder
import com.xfinity.blueprint_sample.mvp.view.HeaderView

@DefaultPresenter(viewClass = ClickableRandomView::class)
class ClickableRandomPresenter: ComponentPresenter<ClickableRandomView, ClickableRandomModel> {
    var clicked: Boolean = true
    override fun present(view: ClickableRandomView, model: ClickableRandomModel) {

        view.viewHolder.itemView.setOnClickListener{
            if(clicked){
                view.viewHolder.xfintiy_text.text = "You've clicked this!"
                view.viewHolder.xfinity_app.setImageResource(R.drawable.android)
                clicked = false
            }
            else if (!clicked){
                view.viewHolder.xfintiy_text.text = "Click this!"
                view.viewHolder.xfinity_app.setImageResource(R.drawable.xfinity_app)
                clicked = true
            }

        }


    }
}