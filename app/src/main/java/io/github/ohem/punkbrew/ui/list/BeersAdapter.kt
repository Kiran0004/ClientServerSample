package io.github.ohem.punkbrew.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.github.ohem.punkbrew.R
import io.github.ohem.punkbrew.data.db.BeerEntity
import kotlinx.android.synthetic.main.item_beer.view.*

class BeersAdapter : PagedListAdapter<BeerEntity, BeersAdapter.BeersViewHolder>(BEER_COMPARATOR) {

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_beer,
            parent,
            false
        )
        return BeersViewHolder(view)
    }

    override fun onBindViewHolder(holder: BeersViewHolder, position: Int) {
        getItem(position)?.let { beer ->
            holder.bind(beer)
            val itemView = holder.itemView
            itemView.setOnClickListener {
                onItemClickListener?.onItemClick(beer)
            }
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    class BeersViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        fun bind(beer : BeerEntity) {
            itemView.item_id.text = String.format("#%s", beer.id)
            itemView.item_name.text = beer.name
            itemView.item_tagline.text = beer.tagline
            Picasso.get()
                .load(beer.imageUrl)
                .placeholder(R.drawable.bottle)
                .error(R.drawable.bottle)
                .fit().centerInside()
                .into(itemView.item_image)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(beer: BeerEntity)
    }

    companion object {
        private val BEER_COMPARATOR =
            object : DiffUtil.ItemCallback<BeerEntity>() {
                override fun areItemsTheSame(oldItem: BeerEntity, newItem: BeerEntity): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: BeerEntity, newItem: BeerEntity): Boolean {
                    return oldItem.name == newItem.name
                }
            }
    }
}
