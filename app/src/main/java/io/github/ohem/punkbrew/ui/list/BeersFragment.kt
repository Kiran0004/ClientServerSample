package io.github.ohem.punkbrew.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.ohem.punkbrew.R
import io.github.ohem.punkbrew.data.db.BeerEntity
import io.github.ohem.punkbrew.ui.base.BaseFragment
import io.github.ohem.punkbrew.util.hide
import io.github.ohem.punkbrew.util.show
import kotlinx.android.synthetic.main.fragment_beers.*

abstract class BeersFragment : BaseFragment<BeersViewModel>(), BeersView<BeersViewModel> {

    abstract override val viewModel: BeersViewModel

    protected val adapter = BeersAdapter()
    override val layoutId: Int = R.layout.fragment_beers

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        adapter.setOnItemClickListener(object : BeersAdapter.OnItemClickListener {
            override fun onItemClick(beer: BeerEntity) {
                onBeerClicked(beer)
            }

        })

        return view
    }

    override fun initView() {
        beers_recycler_view.also {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
        }
    }

    override fun onDestroyView() {
        beers_recycler_view.adapter = null
        super.onDestroyView()
    }

    override fun onBeerUpdated() {
        viewModel.beers.value?.dataSource?.addInvalidatedCallback {
            adapter.notifyDataSetChanged()
        }
        viewModel.beers.value?.dataSource?.invalidate()
    }

    override fun onLoading() {
        beers_progress_bar.show()
        beers_recycler_view.hide()
        beers_empty_list.hide()
    }

    override fun onContentReceived() {
        beers_recycler_view.show()
        beers_progress_bar.hide()
        beers_empty_list.hide()
    }

    override fun onEmptyContent() {
        beers_empty_list.show()
        beers_recycler_view.hide()
        beers_progress_bar.hide()
    }
}
