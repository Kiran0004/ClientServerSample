package io.github.ohem.punkbrew.ui.details.items

import android.widget.TextView
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.github.ohem.punkbrew.R

class HeaderItem(private val text: String) : Item() {

    override fun getLayout() = R.layout.item_details_header

    override fun bind(holder: GroupieViewHolder, position: Int) {
        val title: TextView = holder.containerView.findViewById(R.id.item_details_header_title)
        title.text = text
    }
}