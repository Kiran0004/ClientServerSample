package io.github.ohem.punkbrew.ui.catalog

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import io.github.ohem.punkbrew.R
import io.github.ohem.punkbrew.data.db.BeerEntity
import io.github.ohem.punkbrew.ui.MainActivity.Companion.BACK_STACK_DETAILS_TAG
import io.github.ohem.punkbrew.ui.details.DetailsFragment
import io.github.ohem.punkbrew.ui.list.BeersFragment
import io.github.ohem.punkbrew.util.getFormattedBeerName
import kotlinx.android.synthetic.main.fragment_beers.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class CatalogFragment : BeersFragment() {

    override val viewModel: CatalogViewModel by viewModel()

    private var hasSearchFocus = false
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        restoreSavedInstanceState(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.searchBeers(viewModel.currentQuery)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_catalog, menu)
        val searchItem = menu.findItem(R.id.catalog_menu_search)
        searchItem.let { item ->
            val searchManager
                    = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView = item.actionView as SearchView
            searchView?.let { view ->
                view.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
                view.isIconified = true
                viewModel.currentQuery?.let { query ->
                    item.expandActionView()
                    view.setQuery(query, false)
                    if (!hasSearchFocus) {
                        view.clearFocus()
                    }
                }
                view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(s: String): Boolean {
                            view.clearFocus()
                            hasSearchFocus = false
                            searchByName(s)
                            return true
                        }

                        override fun onQueryTextChange(s: String): Boolean {
                            hasSearchFocus = true
                            searchByName(s)
                            return true
                        }
                    })
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun setupToolbar() {
        beers_toolbar.also {
            (activity as AppCompatActivity).setSupportActionBar(it)
        }
    }

    override fun initView() {
        super.initView()
        viewModel.beers.observe(this, Observer {
            Timber.d("Received list of beers with size of: ${it.size}")
            if (it.size > 0) {
                onContentReceived()
            } else {
                onEmptyContent()
            }
            adapter.submitList(it)
        })

        viewModel.networkErrors.observe(this, Observer {
            Timber.d("Network error: %s", it ?: "...")
            it?.let {
                showNetworkError(it)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SEARCH_FOCUS_KEY, searchView?.hasFocus() ?: false)
    }

    override fun onDestroyView() {
        searchView = null
        super.onDestroyView()
    }

    private fun restoreSavedInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            hasSearchFocus = savedInstanceState.getBoolean(SEARCH_FOCUS_KEY, false)
        }
    }

    private fun searchByName(query: String) {
        beers_recycler_view.scrollToPosition(0)
        viewModel.searchBeers(getFormattedBeerName(query))
        adapter.submitList(null)
    }

    private fun showNetworkError(text: String?) {
        val message = requireContext().applicationContext.getString(R.string.format_error, text)
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    override fun onBeerClicked(beer: BeerEntity) {
        viewModel.hideKeyboard(requireContext().applicationContext, searchView)
        searchView?.clearFocus()
        requireFragmentManager().beginTransaction()
            .replace(
                R.id.root_container,
                DetailsFragment.newInstance(beer.id),
                BACK_STACK_DETAILS_TAG
            )
            .addToBackStack(null)
            .commit()
    }


    companion object {
        private const val SEARCH_FOCUS_KEY = "search_focus"

        fun newInstance(): CatalogFragment {
            return CatalogFragment()
        }
    }
}
